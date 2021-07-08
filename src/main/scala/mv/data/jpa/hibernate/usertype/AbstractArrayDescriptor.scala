package mv.data.jpa.hibernate.usertype

import java.util.Properties

import org.hibernate.`type`.descriptor.java.{AbstractTypeDescriptor, MutabilityPlan, MutableMutabilityPlan}
import org.hibernate.usertype.DynamicParameterizedType
import org.hibernate.usertype.DynamicParameterizedType.{PARAMETER_TYPE}

abstract class AbstractArrayDescriptor[T] (var arrayObjectClass: Class[T],
                                            val mutableMutabilityPlan: MutabilityPlan[T])
  extends AbstractTypeDescriptor[T](arrayObjectClass, mutableMutabilityPlan)
    with DynamicParameterizedType {
  private var sqlArrayType: String = null

  def this(arrayObjectClass: Class[T]) {
    this(arrayObjectClass, new MutableMutabilityPlan[AnyRef]() {

      override protected def deepCopyNotNull(value: AnyRef): AnyRef = copyArray(value)

    }.asInstanceOf[MutabilityPlan[T]])
  }

  def getArrayObjectClass: Class[T] = arrayObjectClass

  def setArrayObjectClass(arrayObjectClass: Class[T]): Unit = this.arrayObjectClass = arrayObjectClass

  override def setParameterValues(parameters: Properties): Unit = {
    if (parameters.containsKey(PARAMETER_TYPE)) {
      arrayObjectClass = (parameters.get(PARAMETER_TYPE).asInstanceOf[DynamicParameterizedType.ParameterType])
        .getReturnedClass.asInstanceOf[Class[T]]
    }
    sqlArrayType = parameters.getProperty(SQL_ARRAY_TYPE)
  }

  override def areEqual(one: T, another: T): Boolean = {
    if (one == another) true
    else if (one == null || another == null) false
    else isEqualsArray(one, another)
  }

  override def extractLoggableRepresentation(value: T): String = if ((value == null)) "null" else toString(value)

  def getSqlArrayType: String = sqlArrayType

}