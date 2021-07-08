package mv.model

import javax.persistence._
import scala.beans.BeanProperty
import org.hibernate.{annotations => ha}
import java.{lang => jl}
import mv.usertype.{IdName, IdWithArrayObject}

@Entity
@Table (name = "test_table")
class TestTable extends BaseEntity {

  @BeanProperty var name: String = _

  @ha.Type(`type` = "array-int")
  @BeanProperty var arrayInt: Array[jl.Integer] = _

  @ha.Type(`type` = "array-double")
  @BeanProperty var arrayDouble: Array[jl.Double] = _

  @ha.Type(`type` = "array-string")
  @BeanProperty var arrayString: Array[jl.String] = _

  @ha.Type(`type` = "array-id-name")
  @BeanProperty var idName: Array[IdName] = _

  @ha.Type(`type` = "id-with-array-object")
  @BeanProperty var idWithArray: Array[IdWithArrayObject] = _
}