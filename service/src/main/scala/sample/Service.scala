package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.StdIn

object Service extends App {
  implicit val system = ActorSystem("service")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  // direct remote actor selection just for this sample — should be clustered actor in real app
  val backend =
    system.actorSelection("akka://backend@127.0.0.1:25521/user/backend")

  val route =
    path("request" / Segment / IntNumber) { (str, num) =>
      get {
        implicit val timeout = Timeout(5.seconds)
        complete((backend ? s"$str:$num").mapTo[String] map { response =>
          HttpEntity(ContentTypes.`text/html(UTF-8)`,
                     s"$response + [service response]")
        })
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9001)

  println(s"Service online at http://localhost:9001/\nPress enter to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
