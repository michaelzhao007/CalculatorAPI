package expression

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import scala.concurrent.Await
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import scala.concurrent.duration.Duration


final case class CalculationItem(expression: String, sessionId: String, save: Boolean)
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat3(CalculationItem)
}

object Api extends App with JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  lazy val calculationRoute: akka.http.scaladsl.server.Route = pathPrefix("calculate") { post {
    path("query") {
      entity(as[CalculationItem]) { q =>
        val evalResult = Evaluator.evalExp(q.expression, q.sessionId, q.save)
        evalResult match {
          case Left(e) => complete(StatusCodes.BadRequest, e)
          case Right(result) => complete(StatusCodes.OK, Map("result: "->result))
          }
        }
        }
      }
    }

  lazy val apiRoutes: akka.http.scaladsl.server.Route = pathPrefix("api") {
    calculationRoute
  }

  Http().bindAndHandle(apiRoutes, "localhost", 8080)
  Await.result(system.whenTerminated, Duration.Inf)
}
