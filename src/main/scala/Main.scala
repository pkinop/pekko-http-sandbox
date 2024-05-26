import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext
import spray.json.RootJsonFormat
import org.apache.pekko.Done

import scala.concurrent.Future
import org.apache.pekko.http.scaladsl.server.{Directives, Route}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}
import org.apache.pekko.http.scaladsl.server.Directives.{complete, path}
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}

import scala.io.StdIn


object HttpServerRoutingMinimal {
  implicit val system: ActorSystem[?] = ActorSystem(
    Behaviors.empty, "SprayExemple"
  )
  implicit val executionContext: ExecutionContext = system.executionContext
  /*
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
  */

  def main(args: Array[String]): Unit = {
    val route: Route =
      path("hello") {
        Directives.get {
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              "<h1>Say hello to Pekko HTTP</h1>"
            )
          )
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
