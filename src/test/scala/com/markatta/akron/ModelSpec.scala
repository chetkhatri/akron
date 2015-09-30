package com.markatta.akron

import java.time.LocalDateTime

class ModelSpec extends BaseSpec {

  "the exact model" should {

    "validate range" in {
      Exactly(5).withinRange(0, 5)
    }

    "know that a value matches" in {
      Exactly(5).matches(5) should be(true)
    }

    "know that a value does not match" in {
      Exactly(4).matches(5) should be(false)
    }

  }

  "the interval model" should {
    "validate range" in {
      Interval(5).withinRange(0, 10)
    }

    "know that a value matches" in {
      Interval(20).matches(0) should be(true)
      Interval(20).matches(20) should be(true)
      Interval(20).matches(40) should be(true)
    }

    "know when a value does not match" in {
      Interval(20).matches(1) should be(false)
    }
  }

  "the ranged model" should {
    "validate range" in {
      Ranged(10 to 20).withinRange(0, 23)
    }

    "know that a value matches" in {
      Ranged(10 to 20).matches(10) should be(true)
      Ranged(10 to 20).matches(20) should be(true)
    }

    "know when a value does not match" in {
      Ranged(10 to 20).matches(9) should be(false)
      Ranged(10 to 20).matches(21) should be(false)
    }
  }

  "the many model" should {
    "validate range" in {
      Many(10, 14, 20).withinRange(0, 23)
    }

    "know that a value matches" in {
      Many(10, 14, 20).matches(10) should be(true)
      Many(10, 14, 20).matches(14) should be(true)
      Many(10, 14, 20).matches(20) should be(true)
    }

    "know when a value does not match" in {
      Many(10, 14, 20).matches(9) should be(false)
      Many(10, 14, 20).matches(11) should be(false)
    }
  }

  "the expression" should {

    import DSL._

    "locate the next event from a given time 1" in {
      val from = LocalDateTime.of(2015, 1, 1, 14, 30)

      val expression = CronExpression(20, 30, *, *, *)

      val Some(result) = expression.nextTriggerTime(from)

      result.getYear shouldEqual 2015
      result.getMonth.getValue shouldEqual 1
      result.getDayOfMonth shouldEqual 1
      result.getHour shouldEqual 20
      result.getMinute shouldEqual 30
    }

    "locate the next event from a given time 2" in {
      val from = LocalDateTime.of(2015, 1, 1, 14, 30)

      val expression = CronExpression(* / 4, 0, *, *, *)

      val Some(result) = expression.nextTriggerTime(from)

      result.getYear shouldEqual 2015
      result.getMonth.getValue shouldEqual 1
      result.getDayOfMonth shouldEqual 1
      result.getHour shouldEqual 16
      result.getMinute shouldEqual 0
    }

    "locate the next event from a given time 3" in {
      val from = LocalDateTime.of(2015, 1, 1, 14, 30)

      val expression = CronExpression(10, many(15, 25, 35), *, *, tue)

      val Some(result) = expression.nextTriggerTime(from)

      result.getYear shouldEqual 2015
      result.getMonth.getValue shouldEqual 1
      result.getDayOfMonth shouldEqual 6
      result.getHour shouldEqual 10
      result.getMinute shouldEqual 15
    }
    "locate the next event from a given time 4" in {
      val from = LocalDateTime.of(2015, 1, 1, 20, 54)

      val expression = CronExpression(*, * / 5, *, *, *)

      val Some(result) = expression.nextTriggerTime(from)

      result.getYear shouldEqual 2015
      result.getMonth.getValue shouldEqual 1
      result.getDayOfMonth shouldEqual 1
      result.getHour shouldEqual 20
      result.getMinute shouldEqual 55
    }

  }

  "the single execution" should {

    "return the single execution time if now is before the trigger time" in {
      val triggerTime = LocalDateTime.of(2015, 1, 1, 14, 30, 0)
      val singleTrigger = SingleExecution(triggerTime)

      singleTrigger.nextTriggerTime(triggerTime.minusMinutes(1)) shouldEqual Some(triggerTime)
    }
    "return the single execution time if now is the same as the trigger time" in {
      val triggerTime = LocalDateTime.of(2015, 1, 1, 14, 30, 0)
      val singleTrigger = SingleExecution(triggerTime)

      singleTrigger.nextTriggerTime(triggerTime) shouldEqual Some(triggerTime)
    }



    "return none if the trigger time has passed" in {
      val triggerTime = LocalDateTime.of(2015, 1, 1, 14, 30, 0)
      val singleTrigger = SingleExecution(triggerTime)

      singleTrigger.nextTriggerTime(triggerTime.plusMinutes(1))
    }
  }

}
