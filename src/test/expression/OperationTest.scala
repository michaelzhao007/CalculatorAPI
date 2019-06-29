package expression

import org.scalatest.{FlatSpec, Matchers}


class OperationTest extends FlatSpec with Matchers {
  "evalExp" should "return correct result" in {
    Evaluator.evalExp("3*3-6+3", "def", true) shouldBe Right(6)
    Evaluator.evalExp("-25.5+13.3*2.5+(1.0-2.0)", "abc", true) shouldBe Right(6.75)
    Evaluator.evalExp("(-25.5+13.3)*2.5+(1.0-2.0)", "abc", true) shouldBe Right(-31.5)
    Evaluator.evalExp("-25.5+13*(2.5+(1.0-2.0))", "abc", true) shouldBe Right(-6.0)
    Evaluator.evalExp("-25.5+13.3*2.5+(1.0/0)", "abc", true) shouldBe Left("Error (/ by 0)")
  }


  "evalExp with saving to memory" should "return correct result" in {
    Evaluator.evalExp("-25.5+13.3*2.5+(1.0-2.0)", "abc", true) shouldBe Right(6.75)
    Evaluator.evalExp("(-25.5+13.3)*2.5+(1.0-MR)", "abc", true) shouldBe Right(-36.25)
    Evaluator.evalExp("MR * (-3.0)", "abc", true) shouldBe Right(108.75)
    Evaluator.evalExp("MR*(-3.0)", "abc", true) shouldBe Right(-326.25)
    Evaluator.evalExp("MR", "abcd", true) shouldBe Left("Error (null mem)")
  }

}