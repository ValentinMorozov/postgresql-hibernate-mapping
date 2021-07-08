package mv.data.jpa.hibernate

import java.lang.Boolean.FALSE
import java.{lang => jl}

/** Набор методов и значений для обработки отображения массивов.
 *
 */
package object usertype {
  val SQL_ARRAY_TYPE = "sql_array_type"
  @inline def nvl[T](p: T)(implicit valIfNull: T) : T =
    p match {
      case null => valIfNull
      case v => v
    }
  @inline def nvlAnyRef[T](p: AnyRef)(implicit valIfNull: T) : T =
    p match {
      case null => valIfNull
      case v => v.asInstanceOf[T]
    }

  implicit val booleanJlIfNull: jl.Boolean = FALSE
  implicit val byteJlIfNull: jl.Byte = 0:Byte
  implicit val shortJlIfNull: jl.Short = 0:Short
  implicit val integerJlIfNull: jl.Integer = 0
  implicit val longJlIfNull: jl.Long = 0:Long
  implicit val floatJlIfNull: jl.Float = 0:Float
  implicit val doubleJlIfNull: jl.Double = 0:Double
  implicit val characterJlIfNull: jl.Character = 0:Char
  implicit val stringJlIfNull: jl.String = ""

  implicit val booleanIfNull: Boolean = FALSE
  implicit val byteIfNull: Byte = 0:Byte
  implicit val shortIfNull: Short = 0:Short
  implicit val intIfNull: Int = 0
  implicit val longIfNull: Long = 0:Long
  implicit val floatIfNull: Float = 0:Float
  implicit val doubleIfNull: Double = 0:Double
  implicit val characterIfNull: Character = 0:Char

  val classOfArrayJlBoolean = classOf[Array[jl.Boolean]]
  val classOfArrayJlInteger = classOf[Array[jl.Integer]]
  val classOfArrayJlCharacter = classOf[Array[jl.Character]]
  val classOfArrayJlString = classOf[Array[jl.String]]
  val classOfArrayJlDouble = classOf[Array[jl.Double]]
  val classOfArrayJlShort = classOf[Array[jl.Short]]
  val classOfArrayJlLong = classOf[Array[jl.Long]]
  val classOfArrayJlFloat = classOf[Array[jl.Float]]

  def copyArray(value: AnyRef) = value.asInstanceOf[Array[Any]].clone().asInstanceOf[AnyRef]

  def isEqualsArray(o1: Any, o2: Any) : Boolean =  o1.asInstanceOf[Array[_]].sameElements(o2.asInstanceOf[Array[_]])

  import java.lang.{Integer => JlInteger, Long => JlLong, Short => JlShort,
    Double => JlDouble, Float => JlFloat, Boolean => JlBoolean, Character => JlCharacter}

  trait StringConverter[A] {
    def parse(string: String): A
  }

  object StringConverter {
    def apply[A](f: (String) => A): StringConverter[A] =
      new StringConverter[A] {
        def parse(string: String) = f(string)
      }
  }

  implicit val stringToInt = StringConverter[Int](
    (string: String) => string.toInt)
  implicit val stringToLong = StringConverter[Long](
    (string: String) => string.toLong)
  implicit val stringToShort = StringConverter[Short](
    (string: String) => string.toShort)
  implicit val stringToDouble = StringConverter[Double](
    (string: String) => string.toDouble)
  implicit val stringToFloat = StringConverter[Float](
    (string: String) => string.toFloat)
  implicit val stringToBoolean = StringConverter[Boolean](
    (string: String) => string.toBoolean)
  implicit val stringToString = StringConverter[String](
    (string: String) => string)

  implicit val stringToJlInteger = StringConverter[JlInteger](
    (string: String) => JlInteger.valueOf(string))
  implicit val stringToJlLong = StringConverter[JlLong](
    (string: String) => JlLong.valueOf(string))
  implicit val stringToJlShort = StringConverter[JlShort](
    (string: String) => JlShort.valueOf(string))
  implicit val stringToJlDouble = StringConverter[JlDouble](
    (string: String) => JlDouble.valueOf(string))
  implicit val stringToJlFloat = StringConverter[JlFloat](
    (string: String) => JlFloat.valueOf(string))
  implicit val stringToJlBoolean = StringConverter[JlBoolean](
    (string: String) => JlBoolean.valueOf(string))
  implicit val stringToJlCharacter = StringConverter[JlCharacter](
    (string: String) => string.charAt(0))

  implicit class StringConverterImplicit(val string: String) extends AnyVal {
    def as[A : StringConverter]:A = implicitly[StringConverter[A]].parse(string)
  }
  //

  trait AnyRefToType[A] {
    def convert(value: AnyRef): A
  }

  object AnyRefToType {
    def apply[A](f: (AnyRef) => A): AnyRefToType[A] =
      new AnyRefToType[A] {
        def convert(value: AnyRef) = f(value)
      }
  }
//
  implicit val anyRefToInt = AnyRefToType[Int](
    (value: AnyRef) => value.asInstanceOf[Int])
  implicit val anyRefToLong = AnyRefToType[Long](
    (value: AnyRef) => value.asInstanceOf[Long])
  implicit val anyRefToShort = AnyRefToType[Short](
    (value: AnyRef) => value.asInstanceOf[Short])
  implicit val anyRefToDouble = AnyRefToType[Double](
    (value: AnyRef) => value.asInstanceOf[Double])
  implicit val anyRefToFloat = AnyRefToType[Float](
    (value: AnyRef) => value.asInstanceOf[Float])
  implicit val anyRefToBoolean = AnyRefToType[Boolean](
    (value: AnyRef) => value.asInstanceOf[Boolean])
  implicit val anyRefToAnyRef = AnyRefToType[AnyRef](
    (value: AnyRef) => value.asInstanceOf[AnyRef])

  implicit val anyRefToJlInteger = AnyRefToType[JlInteger](
    (value: AnyRef) => value.asInstanceOf[JlInteger])
  implicit val anyRefToJlLong = AnyRefToType[JlLong](
    (value: AnyRef) => value.asInstanceOf[JlLong])
  implicit val anyRefToJlShort = AnyRefToType[JlShort](
    (value: AnyRef) => value.asInstanceOf[JlShort])
  implicit val anyRefToJlDouble = AnyRefToType[JlDouble](
    (value: AnyRef) => value.asInstanceOf[JlDouble])
  implicit val anyRefToJlFloat = AnyRefToType[JlFloat](
    (value: AnyRef) => value.asInstanceOf[JlFloat])
  implicit val anyRefToJlBoolean = AnyRefToType[JlBoolean](
    (value: AnyRef) => value.asInstanceOf[JlBoolean])
  implicit val anyRefToJlCharacter = AnyRefToType[JlCharacter](
    (value: AnyRef) => value.asInstanceOf[JlCharacter])

  implicit class AnyRefToValue(val value: AnyRef) extends AnyVal {
    def as[A : AnyRefToType]:A = implicitly[AnyRefToType[A]].convert(value)
  }
  //
  trait TypeToAnyRef[A] {
    def convert(value: A): AnyRef
  }

  object TypeToAnyRef {
    def apply[A](f: (A) => AnyRef): TypeToAnyRef[A] =
      new TypeToAnyRef[A] {
        def convert(value: A) = f(value)
      }
  }

  implicit val intToAnyRef = TypeToAnyRef[Int](
    (value: Int) => nvlAnyRef[JlInteger](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])
  implicit val longToAnyRef = TypeToAnyRef[Long](
    (value: Long) => nvlAnyRef[JlLong](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])
  implicit val shortToAnyRef = TypeToAnyRef[Short](
    (value: Short) => nvlAnyRef[JlShort](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])
  implicit val doubleToAnyRef = TypeToAnyRef[Double](
    (value: Double) => nvlAnyRef[JlDouble](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])
  implicit val floatToAnyRef = TypeToAnyRef[Float](
    (value: Float) => nvlAnyRef[JlFloat](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])
  implicit val booleanToAnyRef = TypeToAnyRef[Boolean](
    (value: Boolean) => nvlAnyRef[JlBoolean](value.asInstanceOf[AnyRef]).asInstanceOf[AnyRef])

  implicit val jlIntegerToAnyRef = TypeToAnyRef[JlInteger](
    (value: JlInteger) => value.asInstanceOf[AnyRef])
  implicit val jlLongToAnyRef = TypeToAnyRef[JlLong](
    (value: JlLong) => value.asInstanceOf[AnyRef])
  implicit val jlShortToAnyRef = TypeToAnyRef[JlShort](
    (value: JlShort) => value.asInstanceOf[AnyRef])
  implicit val jlDoubleToAnyRef = TypeToAnyRef[JlDouble](
    (value: JlDouble) => value.asInstanceOf[AnyRef])
  implicit val jlFloatToAnyRef = TypeToAnyRef[JlFloat](
    (value: JlFloat) => value.asInstanceOf[AnyRef])
  implicit val jlBooleanToAnyRef = TypeToAnyRef[JlBoolean](
    (value: JlBoolean) => value.asInstanceOf[AnyRef])
  implicit val jlCharacterToAnyRef = TypeToAnyRef[JlCharacter](
    (value: JlCharacter) => value.asInstanceOf[AnyRef])

  implicit class ValueToAnyRef[A : TypeToAnyRef](value: A) {
    def toAnyRef: AnyRef = implicitly[TypeToAnyRef[A]].convert(value)
  }
  //

  def escapedChar(ch: Char): String = ch match {
    case '\b' => "\\b"
    case '\t' => "\\t"
    case '\n' => "\\n"
    case '\f' => "\\f"
    case '\r' => "\\r"
//    case '"'  => "\\\""
    case '\'' => "\\'"
    case '\\' => "\\\\"
    case _    => ch.toString
  }

  def unescapedChar(ch: Char): String = (ch match {
    case 'b' => '\b'
    case 't' => '\t'
    case 'n' => '\n'
    case 'f' => '\f'
    case 'r' => '\r'
    case '"'  => '\"'
    case _    => ch
  }).toString

  def escapeString(input: String): String = {
    input.flatMap(escapedChar(_))
  }

  def unescapeString(input: String): String = {
    var stateBackslash: Boolean = false
    input.flatMap(ch => {
      if (ch == '\\') {
        stateBackslash = !stateBackslash;
        if(!stateBackslash) unescapedChar(ch) else ""
      }
      else unescapedChar(ch)
    })
  }

  def isNotInQuote(value: String): Boolean = !value.exists(c => !(c.isLetterOrDigit || c == '.'))

  @inline def unescapeItem(str: String, yesNo: Boolean) = if(yesNo) unescapeString(str) else str
  @inline def escapeItem(str: String, yesNo: Boolean) = if(yesNo) str else '"'+escapeString(str)+'"'

  /** Объединяет массив строк в строку
   *
   *  @param    args  массив объединяемых строк
   *  @param    leftDelimiter  левый ограничитель
   *  @param    listSeparator  разделитель элементов
   *  @param    rightDelimiter  правый ограничитель
   *  @return   Возвращает сформированную строку
   */
  def joinUserTypeStringData(args: Iterable[String], leftDelimiter: String = "(", listSeparator: String = ",", rightDelimiter: String = ")") = {
    args.map(p => escapeItem(p, isNotInQuote(p)))
      .addString(new StringBuilder() , leftDelimiter , listSeparator , rightDelimiter)
      .toString()
  }
  /** Сериализует объект
   *
   *  @param    obj  объект
   *  @param    func функция преобразования объекта в массив строк
   *  @return   Возвращает сформированную строку
   */
  def objToPGString[T](obj: T, func: (T) => Iterable[String]) = {
    joinUserTypeStringData(func(obj))
  }
  /** Сериализует массив объектов
   *
   *  @param    data  массив объектов
   *  @param    func функция преобразования объекта в массив строк
   *  @return   Возвращает сформированную строку
   */
  def arrayObjToPGString[T](data: Iterable[T], func: (T) => Iterable[String]) = {
    joinUserTypeStringData(
      data.map(o => "\""+objToPGString(o, func)+"\""),
      "{", ",", "}")
  }

}
