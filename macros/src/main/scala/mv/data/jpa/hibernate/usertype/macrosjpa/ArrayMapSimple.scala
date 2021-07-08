package mv.data.jpa.hibernate.usertype.macrosjpa

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class ArrayMapSimple extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ArrayMapSimpleImpl.impl
}
object ArrayMapSimpleImpl {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods class $tpname extends ArrayTemplate[$tparams](..$eparams) { ..$body }" :: Nil => {
          val nameType = eparams.head
          val sqlType = eparams.tail.head
          val objName = TermName(tpname.toString)
          q"""
            object $objName
                extends ItemWrap[$tparams] {
                def itemWrap(element: AnyRef) = element.as[$tparams]
                def itemUnwrap(element: $tparams) = element.toAnyRef
                def itemFromString(value: String) = value.as[$tparams]
                def itemToString(element: $tparams) = element.toString
            }
            class $tpname
              extends AbstractArraySimple[$tparams]($objName, $sqlType) {
              ..$body
              override def getName = $nameType
            }
          """
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @ArrayMapJPAImplicit can be used only with classes which extends ArrayTemplate")
      }
    }
    c.Expr[Any](result)
  }
}


