package mv.repo

class RepositoryService[T] (implicit val repository: T) {
}

object RepositoryService {

  implicit def service2Repository[T](p: RepositoryService[T]) = p.repository

}

