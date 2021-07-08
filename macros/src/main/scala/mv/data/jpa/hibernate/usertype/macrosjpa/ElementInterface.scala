package mv.data.jpa.hibernate.usertype.macrosjpa

import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object ElementInterface {
  def create[E, T, P](sqlTypeName: String):  Any = macro ElementImpl.create[E, T, P]
}

object ElementImpl {
  def create[E: c.WeakTypeTag, T: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: whitebox.Context)
  (sqlTypeName: c.Expr[String]): c.Expr[Any] = {
    import c.universe._
    import ClassTypeGroup._
//
    def simpleExpr(tpe: c.universe.Type, nameValue: String) = {
      val valueTree = stringToTree(c)(nameValue)
      val wrapExpr = q"$valueTree.as[$tpe]"
      val unwrapExpr = q"$valueTree.toAnyRef"
      val toStringExpr = q"$valueTree.toString"
      val fromStringExpr = q"$valueTree.as[$tpe]"
      (wrapExpr, unwrapExpr, fromStringExpr, toStringExpr)
    }
//
    def funcExpressions(tpe: c.universe.Type, gpe: ClassTypeGroup.ClassType, nameParamValue: String) = {

      def fromStringItem(valueName: String, fieldType: Type, index: Int) = {
        val termName = TermName(valueName)
        val valueTree = q"$termName($index)"
        q"$valueTree.as[$fieldType]"
      }
      def offsetIndexExpr(index: Int, offsetIndexField: String) = {
        offsetIndexField match {
          case "" => q"$index"
          case e =>
            val termIndex = TermName(e);
            q"$termIndex._2+$index"
        }
      }
      def fromStringItemExpr(valueName: String, fieldType: Type, index: Int, offsetIndexField: String = "") = {
        val termName = TermName(valueName)
        val extractExpr = offsetIndexExpr(index, offsetIndexField)
        val valueTree = q"$termName($extractExpr)"
        q"$valueTree.as[$fieldType]"
      }
      //
      def toStringItem(paramTerm: TermName, fieldName: String, fieldType: Type) = {
        val nameType = fieldType.typeSymbol.name.toString
        val valueName = TermName(fieldName)
        if (nameType == "String") q"$paramTerm.$valueName"
        else q"$paramTerm.$valueName.toString"
      }
      //
      def genWrapExpr(tpeIn: c.universe.Type, nameValue: String): c.universe.Tree = {
        val nameTerm = TermName(nameValue)
        val expressionList: ListBuffer[Tree] = ListBuffer(q"val arrayStrings = elementToArrayString($nameTerm)")
        var recurseLaval = -1

        def genWrapObject(tpeObj: c.universe.Type, valueName: String, arrayIndex: Int = 0, arrayLastFlag: Boolean = false): (Tree, Boolean) = {
          recurseLaval += 1
          if(recurseLaval > 10) return (null, false)

          var arrayPrevInFieldList = false
          val offsetIndexField: PartialFunction[Boolean, String] = {
              case false => ""
              case true => s"extractArray$recurseLaval"
          }
          if (getTypeGroup(c)(tpeObj.typeSymbol) == ArrayType) {
            val typeArgs = tpeObj.typeArgs.head
            val (arrayTypeTree, _) = genWrapObject(typeArgs, "e")
            val termValue = TermName(valueName)
            val termTuple2 = TermName(s"extractArray$recurseLaval")

            val extractExpr = offsetIndexExpr(arrayIndex, offsetIndexField(arrayLastFlag))
            (if(arrayLastFlag)
              q"$termTuple2 = extractArrayFromBuffer($termValue, $extractExpr)"
            else
              q"var $termTuple2 = extractArrayFromBuffer($termValue, $extractExpr)") +=: expressionList
            recurseLaval -= 1
            (q"$termTuple2._1.toArray.map(e => $arrayTypeTree)", true)
          }
          else {
            val fieldList = getFieldDefs(c)(tpeObj)
            var index = arrayIndex - 1
            val argsList = fieldList.foldLeft(List[Tree]())({
              case (acc, (_, fieldType)) =>
                index += 1
                (getTypeGroup(c)(fieldType.typeSymbol) match {
                  case JavaType | ScalaType =>
                    fromStringItemExpr (valueName, fieldType, index, offsetIndexField(arrayPrevInFieldList))
                  case _ =>
                    val (tree, isArray) = genWrapObject(fieldType, valueName, index, arrayPrevInFieldList)
                    arrayPrevInFieldList |= isArray
                    tree
                }) :: acc
            })
            recurseLaval -= 1
            (q"new $tpeObj (..${argsList.reverse})", false)
          }
        }
        genWrapObject(tpeIn, "arrayStrings")._1 +=: expressionList
        q"""{
             import StringArraySplitter._
           ..${expressionList.reverse}
        }"""
      }
      //
      def genUnWrapExpr(tpeIn: c.universe.Type , nameValue: String): c.universe.Tree = {
        var recurseLaval = -1

        def genUnWrapObject(tpeObj: c.universe.Type, valueName: String, propName: String = "", forArray: Boolean = false): Tree = {
          recurseLaval += 1
          if(recurseLaval > 10) return null
          val paramName = s"o$recurseLaval"
          val paramTerm = TermName(paramName)
          val paramTree = q"val $paramTerm: $tpeObj"
          val valueTerm = TermName(valueName)
          if (getTypeGroup(c)(tpeObj.typeSymbol) == ArrayType) {
            val typeArgs = tpeObj.typeArgs.head
            val arrayTypeTree = genUnWrapObject(typeArgs, paramName, propName, true)
            val propTerm = TermName(propName)
            recurseLaval -= 1
            q"arrayObjToPGString[$typeArgs]($valueTerm.$propTerm, $arrayTypeTree)"
          }
          else {
            val fieldList = getFieldDefs(c)(tpeObj)
            var index = - 1
            val argsList = fieldList.foldLeft(List[Tree]())({
              case (acc, (fieldName, fieldType)) =>
                index += 1
                (getTypeGroup(c)(fieldType.typeSymbol) match {
                  case JavaType | ScalaType => toStringItem(paramTerm, fieldName, fieldType)
                  case _ => genUnWrapObject(fieldType, paramName, fieldName)
                }) :: acc
            }).reverse
            recurseLaval -= 1
            if(forArray) q"($paramTree => List(..$argsList))"
            else q"objToPGString[$tpeIn]($valueTerm, $paramTree => List(..$argsList))"
          }
        }
        val expression = genUnWrapObject(tpeIn, nameValue)
        q"""{ import StringArraySplitter._
           ..$expression
        }"""
      }
      if (gpe == JavaType || gpe == ScalaType) simpleExpr(tpe, nameParamValue)
      else (genWrapExpr(tpe, nameParamValue), genUnWrapExpr(tpe, nameParamValue), null, null)
    }
    //
    def exprToList(leftExpr: Tree, rightExpr: Tree): List[Tree] = {
      if(rightExpr == null) List[Tree]()
      else leftExpr :: Nil
    }
    //
    val tpe = weakTypeOf[E]
    val typeGroup = getTypeGroup(c)(tpe.typeSymbol)
    val extendsClassName = (
          typeGroup match {
            case JavaType | ScalaType => weakTypeOf[T]
            case _ => weakTypeOf[P]
          }).typeSymbol.name.toTypeName

    val Literal(Constant(sqlTName: String)) = sqlTypeName.tree
    val className = TypeName(extendsClassName.toString+"_"+tpe.toString+"_"+sqlTName)
    val (wrapExpr, unwrapExpr, fromStringExpr, toStringExpr) = funcExpressions(tpe, typeGroup, "element")
    val defList =
      exprToList(q"def itemWrap(element: AnyRef):$tpe = $wrapExpr", wrapExpr) :::
      exprToList(q"def itemUnwrap(element: $tpe) = $unwrapExpr", unwrapExpr) :::
      exprToList(q"def itemFromString(element: String):$tpe = $fromStringExpr", fromStringExpr) :::
      exprToList(q"def itemToString(element: $tpe) = $toStringExpr", toStringExpr)
    val obj =
      q"""
        final class $className extends $extendsClassName[$tpe] {
        ..$defList
          override def getSqlArrayType =  $sqlTypeName
        }
        (new $className).asInstanceOf[$extendsClassName[Array[$tpe]]]
       """
    c.Expr[Any](obj)
  }
}
