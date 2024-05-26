import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext
import spray.json.RootJsonFormat
import org.apache.pekko.Done

import scala.concurrent.Future
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes, Uri}
import org.apache.pekko.http.scaladsl.server.Directives.{LongNumber, complete, onSuccess, path, pathPrefix}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}

import scala.io.StdIn


object HttpServerRoutingMinimal {
  implicit val system: ActorSystem[?] = ActorSystem(
    Behaviors.empty, "SprayExemple"
  )
  implicit val executionContext: ExecutionContext = system.executionContext
  var orders: List[Item] = Nil
  
  final case class Item(name: String, id: Long)
  final case class Order(items: List[Item])
  
  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat2(Item.apply)
  implicit val orderFormat: RootJsonFormat[Order] = jsonFormat1(Order.apply)
  
  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    orders.find(_.id == itemId)
  }
  
  def saveOrder(order: Order): Future[Done] = {
    orders = order.items ::: orders
    Future { Done }
  }

  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        get {
          pathPrefix("item" / LongNumber) { id =>
            val maybeItem: Future[Option[Item]] = fetchItem(id)

            onSuccess(maybeItem) {
              case Some(item) => complete(item)
              case None => complete(StatusCodes.NotFound)
            }
          }
        },
        post {
          path("create-order") {
            entity(as[Order]) { order =>
              val saved: Future[Done] = saveOrder(order)
              onSuccess(saved) { _ =>
                complete("order created")
              }
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
