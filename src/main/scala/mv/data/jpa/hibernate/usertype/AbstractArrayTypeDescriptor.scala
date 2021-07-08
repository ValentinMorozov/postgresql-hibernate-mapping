package mv.data.jpa.hibernate.usertype

import org.hibernate.`type`.descriptor.WrapperOptions
import java.{sql => jsql}
import scala.reflect.ClassTag

abstract class AbstractArrayTypeDescriptor[E: ClassTag]
  extends AbstractArrayDescriptor[Array[E]](classOf[Array[E]]) with ItemWrap[E] {
  type T = Array[E]
  override def toString(value: T): String = {
    value
      .map(itemToString(_))
      .map(p => escapeItem(p, isNotInQuote(p)))
      .addString(new StringBuilder() , "[" , "," , "]")
      .toString()
  }

  override def unwrap[X](value: T, type2Class: Class[X], options: WrapperOptions): X = {
    if (value == null) null.asInstanceOf[X]
    else {
      if (getArrayObjectClass.isAssignableFrom (type2Class)) value.asInstanceOf[X]
      else if(value.getClass.isArray) value.map(p => itemUnwrap(p)).asInstanceOf[X]
      else throw unknownUnwrap(type2Class)
    }
  }

  override def wrap[X] (value: X, options: WrapperOptions): T = {
    if (value == null) null.asInstanceOf[T]
    else {
      if(!value.isInstanceOf[jsql.Array]) throw unknownWrap(value.getClass)
      val array = value.asInstanceOf[jsql.Array].getArray
      if (getArrayObjectClass.isAssignableFrom(array.getClass)) array.asInstanceOf[T]
      else array.asInstanceOf[T].map(p => itemWrap(p.asInstanceOf[AnyRef])).asInstanceOf[T]
    }
  }
}
