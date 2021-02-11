package wacc_05.symbol_table.identifier_objects

open class TypeIdentifier : IdentifierObject() {

    companion object {
        const val BOOLEAN = "bool"
        const val CHARACTER = "char"
        const val INTEGER = "int"
        const val STRING = "string"
        const val PAIR = "pair"
        const val ARRAY = "array"

        // static type definitions to use globally instead of redefining on each use
        val GENERIC = TypeIdentifier()
        val INT_TYPE = IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
        val CHAR_TYPE = CharIdentifier
        val BOOL_TYPE = BoolIdentifier
        val STRING_TYPE = StringIdentifier
        val PAIR_LIT_TYPE = PairLiterIdentifier
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

    data class IntIdentifier(private val min: Int = Int.MIN_VALUE, private val max: Int = Int.MAX_VALUE) :
        TypeIdentifier() {

        fun valid(value: Int): Boolean {
            return value in min until max
        }

        override fun toString(): String {
            return INTEGER
        }

    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier() {
        override fun toString(): String {
            return "$ARRAY[$elemType]"
        }

        fun getType(): TypeIdentifier {
            return elemType
        }

        override fun equals(other: Any?): Boolean {
            return other is ArrayIdentifier && elemType == other.elemType
        }

        override fun hashCode(): Int {
            var result = elemType.hashCode()
            result = 31 * result + size
            return result
        }
    }

    open class GenericPairType : TypeIdentifier()

    data class PairIdentifier(private val fstType: TypeIdentifier, private val sndType: TypeIdentifier) :
        GenericPairType() {
        override fun toString(): String {
            return "PAIR(${fstType}, ${sndType})"
        }

        fun getFstType(): TypeIdentifier {
            return fstType
        }

        fun getSndType(): TypeIdentifier {
            return sndType
        }

        override fun equals(other: Any?): Boolean {
            if (other is GenericPairType) {
                return if (other is PairIdentifier) {
                    fstType == other.fstType && sndType == other.sndType
                } else {
                    true
                }
            }

            return false
        }

        override fun hashCode(): Int {
            var result = fstType.hashCode()
            result = 31 * result + sndType.hashCode()
            return result
        }
    }

    object PairLiterIdentifier : GenericPairType() {
        override fun toString(): String {
            return PAIR
        }

        override fun equals(other: Any?): Boolean {
            return other is GenericPairType
        }
    }
}