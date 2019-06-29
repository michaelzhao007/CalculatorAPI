package expression

import expression.Parser.LibraryParser

import scala.util.{Failure, Success, Try}
import scala.util.parsing.combinator.RegexParsers

trait Exp
case class BinOp(op: String, elem1: Exp, elem2: Exp) extends Exp
case class Num(v: Double) extends Exp
case object MR extends Exp

trait Parser {
  def parse(input: String): Exp
}

object Parser {
  implicit object LibraryParser extends Parser with RegexParsers {
    def expr: Parser[Exp] = (term ~ rep(("+" | "-") ~ term)) ^^ {
      case a => a._2.foldLeft(a._1) {
        case (x, z) => BinOp(z._1, x, z._2)
      }
    }

    def term: Parser[Exp] = factor ~ rep("*" ~ factor | "/" ~ factor) ^^ {
      case a => a._2.foldLeft(a._1) {
        case (x, z) => BinOp(z._1, x, z._2)
      }
    }

    def factor: Parser[Exp] =  '(' ~> expr <~ ')' | factorNum | factorMr

    def factorNum: Parser[Exp] = opt("-" | "+") ~ number ^^ {
      case None ~ n => n
      case Some("+") ~ n => Num(n.v)
      case Some("-") ~ n => Num(-n.v)
    }

    def factorMr: Parser[Exp] = opt("-" | "+") ~ mr ^^ {
      case None ~ n => n
      case Some("+") ~ n => n
      case Some("-") ~ n => BinOp("-", Num(0), n)
    }

    def number = """\d+(\.\d*)?""".r map { elem => Num(elem.toDouble) }

    def mr = """MR""".r map { mr => MR }

    def parse(input: String) = parseAll(expr, input) match {
      case Success(result, _) => result
      case failure: NoSuccess => scala.sys.error(failure.msg)
    }
  }
}

object Evaluator  {
  import java.util.concurrent.ConcurrentHashMap
  import scala.collection._
  import collection.JavaConverters._

  lazy val map: concurrent.Map[String, Double] = new ConcurrentHashMap[String, Double]().asScala
  private def eval(exp: Exp, sessionId: String): Either[String, Double] = {
    exp match {
      case Num(v) => Right(v)
      case MR =>
        map.get(sessionId) match {
          case None => Left("Error (null mem)")
          case Some(v) => Right(v)
        }
      case BinOp("/", l, Num(0)) => Left("Error (/ by 0)")
      case BinOp("+", l, r) => eval(l, sessionId).flatMap(lv => eval(r, sessionId).map(rv => lv + rv))
      case BinOp("-", l, r) => eval(l, sessionId).flatMap(lv => eval(r, sessionId).map(rv => lv - rv))
      case BinOp("*", l, r) => eval(l, sessionId).flatMap(lv => eval(r, sessionId).map(rv => lv * rv))
      case BinOp("/", l, r) => eval(l, sessionId).flatMap(lv => eval(r, sessionId).map(rv => lv / rv))
    }
  }

  def evalExp(str: String, sessionId: String, save: Boolean)(implicit parser: Parser = LibraryParser): Either[String, Double] = {
    Try(parser.parse(str.replaceAll("\\s", ""))) match {
      case Success(v) => {
        val result = eval(v, sessionId)
        if(save) result.foreach(d => map.put(sessionId, d))
        result
      }
      case Failure(e) => Left(e.getMessage)
    }
  }

}


