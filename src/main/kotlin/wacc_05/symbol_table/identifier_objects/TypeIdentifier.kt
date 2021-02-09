package wacc_05.symbol_table.identifier_objects

sealed class TypeIdentifier : IdentifierObject() {

    companion object {
        const val BOOLEAN = "bool"
        const val CHARACTER = "char"
        const val INTEGER = "int"
        const val STRING = "string"
        const val PAIR = "pair"
        const val ARRAY = "array"
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

    object StringIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return STRING
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

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier() {
        override fun toString(): String {
            return ARRAY
        }
    }

    data class PairIdentifier(private val fstType : TypeIdentifier, private val sndType : TypeIdentifier) : TypeIdentifier() {
        override fun toString(): String {
            return PAIR
        }
    }

    object PairLiterIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return PAIR
        }
    }
}