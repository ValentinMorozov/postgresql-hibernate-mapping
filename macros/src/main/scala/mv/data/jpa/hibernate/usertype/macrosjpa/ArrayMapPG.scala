package mv.data.jpa.hibernate.usertype.macrosjpa

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class ArrayMapPG extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ArrayMapPGImpl.impl
}
object ArrayMapPGImpl {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods class $tpname extends ArrayTemplate[$tparams](..$eparams) { ..$body }" :: Nil => {
          val nameType = eparams.head
          val sqlType = eparams.tail.head
          val objName = TermName(tpname.toString)

          q"""
            class $tpname
               extends AbstractArray[Array[$tparams]]( $objName.arrayDescriptor) {
               setProperties(classOf[Array[$tparams]])
              ..$body
              override def getName = $nameType
              def getDescr = $objName.arrayDescriptor
            }
            object $objName {
              val arrayDescriptor = (ElementInterface.create[$tparams,
                                        ArrayDescriptorTemplate[$tparams],
                                        ArrayDescriptorPGTemplate[$tparams]]
                                        ($sqlType))
                                    .asInstanceOf[AbstractArrayTypeDescriptor[$tparams]]
            }
          """
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @ArrayMapJPA can be used only with classes which extends ArrayTemplate")
      }
    }
    c.Expr[Any](result)
  }
}

