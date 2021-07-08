package mv.data.jpa.hibernate.usertype

trait ItemWrap[E] {

  def itemWrap(element: AnyRef): E

  def itemUnwrap(element: E): AnyRef

  def itemFromString(element: String): E

  def itemToString(element: E): String

}
