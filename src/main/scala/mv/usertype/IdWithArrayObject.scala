package mv.usertype

import java.{lang => jl}

class IdWithArrayObject (var id: jl.Integer, var idCountSum: Array[IdCountSum]) {

}

object IdWithArrayObject {
  def apply(id: jl.Integer, idCountSum: Array[IdCountSum]): IdWithArrayObject =
    new IdWithArrayObject(id, idCountSum)
}

class IdCountSum  (var id: jl.Integer, var count: jl.Float, var sum: jl.Double) {

}

object IdCountSum {
  def apply(id: jl.Integer, count: jl.Float, sum: jl.Double): IdCountSum =
    new IdCountSum(id, count, sum)
}