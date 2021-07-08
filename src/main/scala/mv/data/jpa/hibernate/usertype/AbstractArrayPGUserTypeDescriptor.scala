package mv.data.jpa.hibernate.usertype

import scala.reflect.ClassTag
import org.postgresql.util.PGobject
import scala.collection.mutable.ArrayBuffer
import StringArraySplitter._

abstract class AbstractArrayPGUserTypeDescriptor[E: ClassTag]
  extends AbstractArrayTypeDescriptor[E]
{
  def elementToArrayString(element: AnyRef): ArrayBuffer[String] =
    splitUserTypeStringData(element.asInstanceOf[PGobject].getValue)

  override def fromString(string: String): T = {
    val arrayString = splitArrayString(string)
    arrayString.map(itemWrap(_)).asInstanceOf[T]
  }

  def itemFromString(element: String): E = itemWrap(element.asInstanceOf[AnyRef])

  def itemToString(element: E): String = itemUnwrap(element).asInstanceOf[String]

}
