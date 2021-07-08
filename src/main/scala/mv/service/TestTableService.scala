package mv.service

import mv.model.TestTable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import mv.repo.{RepositoryService, TestTableRepository}
import org.springframework.transaction.annotation.Transactional

@Service
class TestTableService(implicit @Autowired testTableRepository: TestTableRepository)
  extends RepositoryService[TestTableRepository] {
  TestTableService.setService(this)

  def listAll(): Iterable[TestTable] = this.findAll.asScala

  def getById(id: Long): TestTable = this.findById(id)//.get()

 // @Transactional(noRollbackFor = Array(classOf[java.lang.NullPointerException]))
  def createRecord(testTable: TestTable): Long = {
    this.save(testTable)
    testTable.id
  }

  def getByName(name: String): TestTable = this.findByName(name)

}

object TestTableService {
  implicit var testTableService: TestTableService = _

  private def setService(p: TestTableService) = this.testTableService = p
}
