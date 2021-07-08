package mv.data.jpa.hibernate.usertype.array.simple

import java.{lang => jl}
import mv.data.jpa.hibernate.usertype.{AbstractArraySimple, ItemWrap}
import mv.data.jpa.hibernate.usertype._


class ArrayString()
  extends AbstractArraySimple[jl.String](StringElementWraper, "varchar") {
  override def getName = "array-string"
}

object StringElementWraper
  extends ItemWrap[jl.String] {

  def itemWrap(element: AnyRef) = element.asInstanceOf[jl.String]

  def itemUnwrap(element: jl.String) = nvlAnyRef[jl.String](element.asInstanceOf[AnyRef]).asInstanceOf[AnyRef]

  def itemFromString(value: String) = jl.String.valueOf(value)

  def itemToString(element: jl.String) = element

}
