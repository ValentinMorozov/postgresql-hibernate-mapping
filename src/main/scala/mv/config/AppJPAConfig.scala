package mv.config

import javax.persistence.EntityManager
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
@Autowired
class AppJPAConfig ( val entityManager: EntityManager,
                     val dataSource: DataSource
                   ) {
  AppJPAConfig.setObjectVar(this)
}

object AppJPAConfig {
  implicit var entityManager: EntityManager = _
  implicit var dataSource: DataSource = _

  private def setObjectVar(p: AppJPAConfig) = {
    this.entityManager = p.entityManager
    this.dataSource = p.dataSource
  }
}
