package mv.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import mv.model.TestTable
import java.{lang => jl}

@Repository
trait TestTableRepository extends CrudRepository[TestTable, Long] {
  def findByName(username: String): TestTable

  def findById(id: jl.Long): TestTable
}
