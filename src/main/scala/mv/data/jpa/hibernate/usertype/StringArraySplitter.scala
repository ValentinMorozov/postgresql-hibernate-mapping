package mv.data.jpa.hibernate.usertype

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/** Парсинг/упаковка из/в формат(а) представления объектов драйвера postgresql
 *
 */
final class StringArraySplitter(var value: String, var resultBuffer: ArrayBuffer[String]) {
  import StringArraySplitter._
  var indexfrom: Int = 0

  /** Разбивает строку с элементами, разделенными запятыми, в массив строк.
   *  Внутренние объекты обернутые круглыми или квадратными скобками обрабатывает как единый элемент.
   *
   *  @param    from     индекс начала обрабатываемой подстроки
   *  @param    endChar  символ конца подстроки
   */
  @tailrec
  def extractItems(from: Int, endChar: Char) {
    val indexTo = endStringItem(value, from, endChar)
    if(indexTo > 0) {
      val indexFrom = testNextFrom(value, '"', from)
      resultBuffer += unescapeItem(value.substring(indexFrom, indexTo), indexFrom != indexfrom)
      indexfrom = testNextFrom(value, ',',testNextFrom(value, '"', indexTo))
      if(value.charAt(indexfrom) != endChar) extractItems(indexfrom, endChar)
    }
    else indexfrom = -1
  }
  /** Парсит данные из последовательности, содержащей сериализованный массив  .
   *
   *  @param    result     объект-приемник данных
   *  @return   двуменный массив с элементами в строковом формате
   */
  @tailrec
  def extractArray(result: ArrayBuffer[ArrayBuffer[String]]) {
    result += resultBuffer
    if(charCurrent != ']') {
      val (i, c) = if(charNext == '(') (indexNext, ')') else (indexCurrent,']')
      clearResult
      extractItems(i, c)
      extractArray(result)
    }
  }

  @inline def charCurrent() = value.charAt(indexfrom)

  @inline def charAt(index: Int) = value.charAt(index)

  @inline def charNext = value.charAt(indexfrom + 1)

  @inline def indexNext = indexfrom + 1

  @inline def indexCurrent = indexfrom

  @inline def clearResult = resultBuffer.clear()

}
//
object StringArraySplitter {
  /** Парсит данные из последовательности в массив строк  .
   *
   *  @param    value  входная строка
   *  @return   Возвращает массив с элементами в строковом формате
   */
  def splitUserTypeStringData(value: String): ArrayBuffer[String] = {
    val splitter = new StringArraySplitter(value, ArrayBuffer.empty[String])
    splitter.extractItems(1, value.charAt(0) match { case '[' => ']' case _  => ')'})
    splitter.resultBuffer
  }
  /** Парсит массив последовательностей
   *
   *  @param    value  входная строка
   *  @return   Возвращает двуменный массив с элементами в строковом формате
   */
  def splitArrayString(value: String): ArrayBuffer[ArrayBuffer[String]] = {
    val splitter = new StringArraySplitter(value, ArrayBuffer.empty[String])
    val result = ArrayBuffer.empty[ArrayBuffer[String]]
    splitter.extractArray(result)
    result
  }
  //
  def extractArrayFromBuffer(strings: ArrayBuffer[String], from: Int) = {
    val length = strings.length
    def skipInnerArray(skipFrom: Int): Int = {
      var j = skipFrom + 1
      while(j < length && strings(j) != "}") {
        if(strings(j) == "{") j = skipInnerArray(j)
        j += 1
      }
      j
    }
    //
    val result = ArrayBuffer[ArrayBuffer[String]]()
    if(strings(from) == "{") {
      var i = from + 1
      while(i < length && strings(i) != "}") {
        strings(i) match {
          case "{" => i = skipInnerArray (i)
          case "," =>
          case e => result += splitUserTypeStringData (e)
        }
        i += 1
      }
      (result, i)
    }
    else (result, from)
  }

  /** Ищет в обрабатываемой подстроке конец элемента  .
   *
   *  @param    str     обрабатываемая строка
   *  @param    from    начальная позиция в строке
   *  @return   индекс символа конца элемента
   */
  def endStringItem(str: String, from: Int, charEnd: Char): Int = {
    val length = str.length
    if(length > from) {
      if(str.charAt(from) != '"') {
        str.charAt(from) match {
          case '(' => endStringItemObj(str, from + 1, ')')
          case '[' => endStringItemObj(str, from + 1, ']')
          case _ => str.indexWhere(ch => ch == ',' || ch == charEnd, from)
        }
      }
      else endStringItemQuote(str, from + 1)
    } else length - 1
  }
  /** Ищет конец элемента, помещенного в кавычки
   *
   *  @param    str     обрабатываемая строка
   *  @param    from    начальная позиция в строке
   *  @return   индекс символа конца элемента
   */
  def endStringItemQuote(str: String, from: Int): Int = {
    var stateBackslash: Boolean = false
    str.indexWhere(ch => {
      ch match {
        case '"' if(!stateBackslash) => true
        case '\\' => stateBackslash = !stateBackslash; false
        case _ => stateBackslash = false; false
      }
    }, from)
  }
  /** Ищет в обрабатываемой подстроке конец элемента, для объекта  .
   *
   *  @param    str     обрабатываемая строка
   *  @param    from    начальная позиция в строке
   *  @param    charEnd    символ конца подстроки
   *  @return   индекс символа конца элемента
   */
  def endStringItemObj(str: String, from: Int, charEnd: Char): Int = {
    /** Ищет в обрабатываемой подстроке конец вложенного объекта  .
     *
     *  @param    beg    начальная позиция в строке
     *  @param    chEnd    символ конца вложенного объекта
     *  @return   индекс символа конца элемента
     */
    def skipObj(beg: Int, chEnd: Char) = {
      endStringItemObj(str,
        endStringItemObj(str, beg + 1, chEnd) + 1,
        charEnd)
    }
//
    val index = str.indexWhere({
      case '(' | '[' | '"' | ']' | ')' => true
      case _ => false
    } , from)

    str.charAt(index) match {
      case '(' => skipObj(index, ')')
      case '[' => skipObj(index, ']')
      case '"' => endStringItemQuote(str, index + 1) + 1
      case a if(a == charEnd) => index + 1
    }
  }
  /** Пропускает разделители в обрабатываемой строке
   *
   *  @param    str     обрабатываемая строка
   *  @param    ch      пропускаемый символ
   *  @param    index   проверяемая позиция
   *  @return   индекс  символа конца элемента
   */
  def testNextFrom(str: String, ch: Char, index: Int) = {
    if(str.charAt(index) == ch) index + 1
    else index
  }
}

