package mv.usertype

//import mv.data.jpa.hibernate.usertype.{AbstractArray, AbstractArrayPGUserTypeDescriptor,  StringArraySplitter}
import mv.data.jpa.hibernate.usertype._
import java.{ lang => jl}
import scala.reflect.ClassTag
import org.postgresql.util.PGobject

import scala.collection.mutable.ArrayBuffer
/*
class IdWithArrayObjectJPA()
  extends AbstractArray[Array[IdWithArrayObject]](IdWithArrayObjectJPA) {
  setProperties(classOf[Array[IdWithArrayObject]])
  override def getName = "id-with-array-object"
}

object IdWithArrayObjectJPA extends AbstractArrayPGUserTypeDescriptor[IdWithArrayObject] {
  import StringArraySplitter._
  def itemWrap(element: AnyRef) : IdWithArrayObject = {
    val arrayStrings = elementToArrayString(element);
    val (arrayStringsInner, index) = extractArrayFromBuffer(arrayStrings, 1)
    IdWithArrayObject(arrayStrings(0).as[jl.Integer],
      arrayStringsInner.toArray.map(e => IdCountSum(e(0).as[jl.Integer], e(1).as[jl.Float], e(2).as[jl.Double]))
    )
  }

  def itemUnwrap(element: IdWithArrayObject) : AnyRef = {
    val inn =
      joinUserTypeStringData(
        element.idCountSum.map(e => joinUserTypeStringData(
          Array(e.id.toString, e.count.toString, e.sum.toString))), "\"{\"", "\",\"", "\"}\"")
    Array(element.id.toString, inn)
      .addString(new StringBuilder() , "(" , "," , ")")
      .toString()
  }
  override def getSqlArrayType = "idwitharrayobject"

}
*/
