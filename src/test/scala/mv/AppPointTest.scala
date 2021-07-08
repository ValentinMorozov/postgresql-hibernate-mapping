package mv

import mv.model.TestTable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalactic.Equality
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import java.{lang => jl}

import mv.usertype.{IdCountSum, IdName, IdWithArrayObject}
import scala.reflect.ClassTag

trait ArrayCustomEquality {
  implicit def arrayEq[T: ClassTag : Equality]: Equality[Array[T]] = new Equality[Array[T]] {
    def areEqual(left: Array[T], right: Any): Boolean = {
      right  match {
        case r: Array[_] => left.length == r.length &&
          !left.zip(r).exists(t => !implicitly[Equality[T]].areEqual(t._1, t._2))
        case _ => false
      }
    }
  }
}

object ArrayCustomEquality extends ArrayCustomEquality

class AppPointTest extends AnyFlatSpec with Matchers {

  val logger = LoggerFactory.getLogger(classOf[AppPoint])
  val applicationContext = SpringApplication.run(classOf[AppPoint])

  val arrayInt = Array[jl.Integer](1,2,3,5)
  val arrayDouble = Array[jl.Double](-1.0,222.0,35500.0,576.0)
  val arrayString = Array[jl.String]("+++++","*****", "!!!")
  val arrayIdName = Array[IdName](IdName(15,"????" ), IdName(20,"!!!!!" ))
  val idWithArrayObject = Array[IdWithArrayObject] (
    new IdWithArrayObject(0,
      Array(new IdCountSum(1, 12.toFloat, 12.0),
        new IdCountSum(2, 5.toFloat, 10.0))),
      new IdWithArrayObject(1,
        Array(new IdCountSum(1, 17.toFloat, 19.0),
          new IdCountSum(2, 100.toFloat, 120.0))),
    )

  implicit val idWithArrayObjectEq: Equality[IdWithArrayObject] = new Equality[IdWithArrayObject] {
    def areEqual(a: IdWithArrayObject, b: Any): Boolean = {
      def idCountSumEq(l: IdCountSum, r: IdCountSum): Boolean  =
        l.id == r.id && l.count == r.count && l.sum == r.sum
      b match {
        case p: IdWithArrayObject => a.id == p.id &&
          a.idCountSum.length == p.idCountSum.length &&
          !a.idCountSum.zip(p.idCountSum).exists(t => !idCountSumEq(t._1, t._2))
        case _ => false
      }
    }
  }

  implicit val idNameEq: Equality[IdName] = new Equality[IdName] {
    def areEqual(a: IdName, b: Any): Boolean =
      b match {
        case p: IdName => a.id == p.id && a.name == p.name
        case _ => false
      }
  }

  import ArrayCustomEquality._

  var idTestedRecort: Long = _
  var testData: TestTable = _

  import mv.service.TestTableService.testTableService

  it should "let us check the delete records from table" in {
    testTableService.deleteAll()
    testTableService.count() shouldBe 0
  }

  it should "let us check the insert record into table" in {
    val test = new TestTable
    test.setName("Test Name")
    test.arrayInt = arrayInt
    test.arrayDouble = arrayDouble
    test.arrayString = arrayString
    test.idName = arrayIdName.asInstanceOf[Array[IdName]]
    test.idWithArray = idWithArrayObject
    idTestedRecort = testTableService.createRecord(test)
    testTableService.count() shouldBe 1
  }

  it should "let us check the String field" in {
    testData = testTableService.getById(idTestedRecort)
    testData.name shouldBe "Test Name"
  }
  it should "let us check the Array Int field" in {
    testData.arrayInt shouldBe arrayInt
  }
  it should "let us check the Array Double field" in {
    testData.arrayDouble shouldBe arrayDouble
  }
  it should "let us check the Array String field" in {
    testData.arrayString shouldBe arrayString
  }
  it should "let us check the Array user type IdName field" in {
    testData.idName should equal(arrayIdName)
  }
  it should "let us check the Array user type IdWithArrayObject field" in {
    testData.idWithArray should equal(idWithArrayObject)
  }
}
