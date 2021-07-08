package mv.model

import java.io.Serializable

import org.hibernate.annotations.TypeDef
import javax.persistence.{GeneratedValue, GenerationType, Id, MappedSuperclass, Version}
import mv.data.jpa.hibernate.usertype.ArrayTemplate

import scala.beans.BeanProperty
import mv.data.jpa.hibernate.usertype.array.simple.{ArrayInt, ArrayString}
import mv.data.jpa.hibernate.usertype.macrosjpa.ArrayMapSimple
import mv.usertype.IdName
import java.{lang => jl}

import mv.data.jpa.hibernate.usertype._
import mv.data.jpa.hibernate.usertype.macrosjpa._

@ArrayMapSimple
class ArrayLong extends ArrayTemplate[jl.Integer]("array-int","integer")

@ArrayMapSimple
class ArrayIntegerJL extends ArrayTemplate[jl.Integer]("array-int","integer")

@ArrayMapSimple
class ArrayDoubleJL extends ArrayTemplate[jl.Double]("array-double","float8")

@ArrayMapPG
class ArrayIdName extends ArrayTemplate[IdName]("array-id-name","idname")

import mv.usertype.IdWithArrayObject

@ArrayMapPG
class ArrayIdWithArrayObject extends ArrayTemplate[IdWithArrayObject]("id-with-array-object","idwitharrayobject")

@TypeDef(name = "array-int", typeClass = classOf[ArrayLong])
@TypeDef(name = "array-double", typeClass = classOf[ArrayDoubleJL])
@TypeDef(name = "array-string", typeClass = classOf[ArrayString])
@TypeDef(name = "array-id-name", typeClass = classOf[ArrayIdName])
@TypeDef(name = "id-with-array-object", typeClass = classOf[ArrayIdWithArrayObject])
@MappedSuperclass
class BaseEntity extends Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @BeanProperty var id: Long = _

}
