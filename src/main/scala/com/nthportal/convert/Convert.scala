package com.nthportal.convert

import scala.language.higherKinds
import scala.reflect.ClassTag
import scala.util.control.ControlThrowable

sealed trait Convert {
  type Result[T]

  def conversion[T](res: => T): Result[T]

  def fail[T](ex: => Exception): Nothing

  def unwrap[T](result: Result[T]): T

  @inline
  final def require(requirement: Boolean): Unit = {
    if (!requirement) fail(new IllegalArgumentException("requirement failed"))
  }

  @inline
  final def require(requirement: Boolean, message: => Any): Unit = {
    if (!requirement) fail(new IllegalArgumentException("requirement failed: " + message))
  }

  final def wrapException[E <: Exception : ClassTag, T](res: => T): T = {
    wrapException(implicitly[ClassTag[E]].runtimeClass.isInstance(_))(res)
  }

  final def wrapException[T](matches: Exception => Boolean)(res: => T): T = {
    try {
      res
    } catch {
      case e: Exception if matches(e) => fail(e)
    }
  }
}

object Convert {

  object Valid extends Convert {
    override type Result[T] = T

    override def conversion[T](res: => T): T = res

    override def fail[T](ex: => Exception): Nothing = throw ex

    override def unwrap[T](result: T): T = result
  }

  object Any extends Convert {
    override type Result[T] = Option[T]

    override def conversion[T](res: => T): Option[T] = {
      try {
        Some(res)
      } catch {
        case FailControl => None
      }
    }

    override def fail[T](ex: => Exception): Nothing = throw FailControl

    override def unwrap[T](result: Option[T]): T = result match {
      case Some(t) => t
      case None => throw FailControl
    }
  }

  private case object FailControl extends ControlThrowable

}
