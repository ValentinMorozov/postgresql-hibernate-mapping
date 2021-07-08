package mv.data.jpa.hibernate.usertype

import org.hibernate.`type`.descriptor.{ValueBinder, ValueExtractor, WrapperOptions}
import org.hibernate.`type`.descriptor.java.JavaTypeDescriptor
import org.hibernate.`type`.descriptor.sql.{BasicBinder, BasicExtractor, SqlTypeDescriptor}
import java.sql.{Types, SQLException, PreparedStatement, CallableStatement, ResultSet}

class ArraySqlDescriptor extends SqlTypeDescriptor {
  override def getSqlType: Int = Types.ARRAY

  override def canBeRemapped: Boolean = true

  override def getBinder[X](javaTypeDescriptor: JavaTypeDescriptor[X]): ValueBinder[X] = {
    new BasicBinder[X](javaTypeDescriptor, this) {
      @throws[SQLException]
      override protected def doBind(st: PreparedStatement, value: X, index: Int, options: WrapperOptions): Unit = {
        val abstractArrayDescriptor: AbstractArrayDescriptor[X] = javaTypeDescriptor.asInstanceOf[AbstractArrayDescriptor[X]]
        st.setArray(index,
          st.getConnection.createArrayOf(abstractArrayDescriptor.getSqlArrayType,
            abstractArrayDescriptor.unwrap(value, classOf[Array[AnyRef]], options)))
      }

      @throws[SQLException]
      override protected def doBind(st: CallableStatement, value: X, name: String, options: WrapperOptions): Unit = {
        throw new UnsupportedOperationException("Binding by name is not supported!")
      }
    }
  }

  override def getExtractor[X](javaTypeDescriptor: JavaTypeDescriptor[X]): ValueExtractor[X] = {
    new BasicExtractor[X](javaTypeDescriptor, this) {
      @throws[SQLException]
      override protected def doExtract(rs: ResultSet, name: String, options: WrapperOptions): X =
                                javaTypeDescriptor.wrap(rs.getArray(name), options)

      @throws[SQLException]
      override protected def doExtract(statement: CallableStatement, index: Int, options: WrapperOptions): X =
                                javaTypeDescriptor.wrap(statement.getArray(index), options)

      @throws[SQLException]
      override protected def doExtract(statement: CallableStatement, name: String, options: WrapperOptions): X =
                                javaTypeDescriptor.wrap(statement.getArray(name), options)
    }
  }
}

object ArraySqlDescriptor {
  val arraySqlDescriptor: ArraySqlDescriptor = new ArraySqlDescriptor
}
