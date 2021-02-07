package wacc_05.symbol_table.identifier_objects

sealed class TypeIdentifier : IdentifierObject() {

    companion object {
        val BOOLEAN = "bool"
        val CHARACTER = "char"
        val INTEGER = "int"
    }

    object BoolIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return BOOLEAN
        }
    }

    object CharIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return CHARACTER
        }
    }

    data class IntIdentifier(private val min : Int = Int.MIN_VALUE, private val max : Int = Int.MAX_VALUE) : TypeIdentifier() {

        fun valid(value : Int) : Boolean {
            return value in min until max
        }

        override fun toString(): String {
            return INTEGER
        }

    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier()

    data class PairIdentifier(private val fstType : TypeIdentifier, private val sndType : TypeIdentifier) : TypeIdentifier()
}