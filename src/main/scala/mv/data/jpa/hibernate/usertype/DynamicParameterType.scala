package mv.data.jpa.hibernate.usertype

import java.lang.annotation.Annotation
import org.hibernate.usertype.DynamicParameterizedType

class DynamicParameterType(val clazz: Class[_])
  extends DynamicParameterizedType.ParameterType {

  override def getReturnedClass: Class[_] = clazz

  override def getAnnotationsMethod = new Array[Annotation](0)

  override def getCatalog = throw new UnsupportedOperationException

  override def getSchema = throw new UnsupportedOperationException

  override def getTable = throw new UnsupportedOperationException

  override def isPrimaryKey = throw new UnsupportedOperationException

  override def getColumns = throw new UnsupportedOperationException
}
