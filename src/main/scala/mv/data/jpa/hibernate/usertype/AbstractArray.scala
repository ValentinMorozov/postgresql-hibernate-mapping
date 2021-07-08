package mv.data.jpa.hibernate.usertype

import java.util.Properties
import org.hibernate.`type`.AbstractSingleColumnStandardBasicType
import org.hibernate.`type`.descriptor.java.JavaTypeDescriptor
import org.hibernate.usertype.DynamicParameterizedType
import ArraySqlDescriptor.arraySqlDescriptor

abstract class AbstractArray[T](javaTypeDescriptor: JavaTypeDescriptor[T])
  extends AbstractSingleColumnStandardBasicType[T](arraySqlDescriptor, javaTypeDescriptor)
    with DynamicParameterizedType {

  var arrayClass: Class[_] = _

  override protected def registerUnderJavaType = true

  override def setParameterValues(parameters: Properties): Unit =
    getJavaTypeDescriptor.asInstanceOf[AbstractArrayDescriptor[_]].setParameterValues(parameters)

  def setProperties(arrayClass: Class[_]) {
    this.arrayClass = arrayClass
    val parameters = new Properties
    parameters.put(DynamicParameterizedType.PARAMETER_TYPE, new DynamicParameterType(arrayClass))
    setParameterValues(parameters)
  }

}


