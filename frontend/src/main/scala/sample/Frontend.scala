package sample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn
import scala.reflect.ClassTag

object Frontend extends App {
  implicit val system = ActorSystem("frontend")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    path(Segment) { (str) =>
      get {
        complete(
          Http().singleRequest(HttpRequest(
            uri = s"http://localhost:9001/request/$str/${str.length}")) map {
            response =>
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                s"${responseAs[String](response)} + [frontend response]")
          })
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

  println(s"Frontend online at http://localhost:9000/\nPress enter to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

  def responseAs[T: FromResponseUnmarshaller: ClassTag](
      response: HttpResponse)(implicit timeout: Duration = 1.second): T = {
    Await.result(Unmarshal(response).to[T], timeout)
  }
}
