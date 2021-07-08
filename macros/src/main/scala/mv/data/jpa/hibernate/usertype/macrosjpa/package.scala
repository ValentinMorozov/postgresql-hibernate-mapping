package mv.data.jpa.hibernate.usertype

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

package object macrosjpa {

  object ClassTypeGroup extends Enumeration {
    type ClassType = Value
    val JavaType, ScalaType, ArrayType, UserType = Value
    def getTypeGroup(c: whitebox.Context)(tps: c.universe.Symbol) = {
      if (tps.isJava) JavaType
      else if (tps.asType.toTypeConstructor.toString == "Array") ArrayType
      else if (tps.fullName.startsWith("scala.")) ScalaType
      else UserType
    }
  }
  //
  def getFieldDefs(c: whitebox.Context)(tpe: c.universe.Type) = {
    import c.universe._
    val typeSignature = tpe
      .members
      .collect({ case c: MethodSymbol if (c.isConstructor) => c.typeSignature })
      .head
    typeSignature.paramLists
      .flatten
      .map(f => (f.name.toString, f.typeSignature))
  }
  def stringToTree(c: whitebox.Context)(namevalue: String) = {
    import c.universe._
    val name = TermName(namevalue)
    q"$name"
  }
}


