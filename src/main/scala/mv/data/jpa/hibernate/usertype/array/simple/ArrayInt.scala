package mv.data.jpa.hibernate.usertype.array.simple

import java.{lang => jl}

import mv.data.jpa.hibernate.usertype.{AbstractArraySimple, ItemWrap}
import mv.data.jpa.hibernate.usertype._

class ArrayInt()
  extends AbstractArraySimple[jl.Integer](IntWraper, "integer") {
  override def getName = "array-int"
}

object IntWraper
  extends ItemWrap[jl.Integer] {

  def itemWrap(element: AnyRef) = element.asInstanceOf[jl.Integer]

  def itemUnwrap(element: jl.Integer) = nvlAnyRef[jl.Integer](element.asInstanceOf[AnyRef]).asInstanceOf[AnyRef]

  def itemFromString(value: String) = jl.Integer.valueOf(value)

  def itemToString(element: jl.Integer) = element.toString

}

