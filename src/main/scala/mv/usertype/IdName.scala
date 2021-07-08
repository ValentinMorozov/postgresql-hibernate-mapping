package mv.usertype

import java.{lang => jl}

class IdName  (var id: jl.Integer, var name: jl.String) {

}

object IdName {
  def apply(id: Integer, name: String): IdName = new IdName(id, name)
}