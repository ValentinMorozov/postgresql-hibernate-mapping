package mv.data.jpa.hibernate.usertype

import scala.reflect.ClassTag
import StringArraySplitter._

abstract class AbstractArraySimple[E: ClassTag](elementInterface: ItemWrap[E], sqlArrayType: String)
    extends AbstractArray[Array[E]](new ArraySimpleDescriptor[E](elementInterface, sqlArrayType)) {
  setProperties(classOf[Array[E]])
}

final class ArraySimpleDescriptor[E: ClassTag](val elementInterface: ItemWrap[E], sqlArrayType: String)
  extends AbstractArrayTypeDescriptor[E] {

  override def fromString(string: String): T = {
    val arrayString = splitArrayString(string)
    arrayString(0).map(itemFromString(_)).asInstanceOf[T]
  }

  def itemWrap(element:AnyRef) = elementInterface.itemWrap(element)

  def itemUnwrap(element:E) = elementInterface.itemUnwrap(element)

  def itemFromString(value: String) = elementInterface.itemFromString(value)

  def itemToString(value: E) = elementInterface.itemToString(value)

  override def getSqlArrayType = sqlArrayType

}

