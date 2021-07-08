package mv.data.jpa.hibernate.usertype

import scala.reflect.ClassTag
import org.postgresql.util.PGobject
import StringArraySplitter._

class ArrayTemplate[E : ClassTag](name: String, nameType: String)
  extends AbstractArray[Array[E]](null) {
  setProperties(classOf[Array[E]])
  override def getName = name
}

abstract class ArrayDescriptorTemplate[E: ClassTag]
  extends AbstractArrayTypeDescriptor[E] {

  override def fromString(string: String): T = {
    val arrayString = splitArrayString(string)
    arrayString(0).map(fromString(_)).asInstanceOf[T]
  }
}

abstract class ArrayDescriptorPGTemplate[E: ClassTag]
  extends  AbstractArrayPGUserTypeDescriptor[E] {

  override def fromString(string: String): T = {
    val arrayString = splitArrayString(string)
    arrayString(0).map(fromString(_)).asInstanceOf[T]
  }

}