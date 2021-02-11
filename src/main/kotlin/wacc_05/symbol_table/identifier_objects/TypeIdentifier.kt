package wacc_05.symbol_table.identifier_objects

open class TypeIdentifier : IdentifierObject() {

    companion object {
        // static string definitions of the types
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
        val GENERIC_PAIR_TYPE = GenericPairType()
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

        override fun toString(): String {
            return INTEGER
        }

    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier() {
        override fun toString(): String {
            return "$ARRAY[$elemType]"
        }

        override fun getType(): TypeIdentifier {
            return elemType
        }


        // we override equality for array types to capture that only the element types need
        // to match for two array types to be equal, regardless of length.
        override fun equals(other: Any?): Boolean {
            return other is ArrayIdentifier && elemType == other.elemType
        }

        override fun hashCode(): Int {
            var result = elemType.hashCode()
            result = 31 * result + size
            return result
        }
    }

    // a generic pair type to capture the overall pair type. Used to represent the loss
    // of type with nested pairs.
    open class GenericPairType : TypeIdentifier() {
        override fun equals(other: Any?): Boolean {
            return other is GenericPairType
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

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

        // we override equals here to capture that two pair types are equal if they have the same
        // inner types, or if the other is null.
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

    override fun getType(): TypeIdentifier {
        return this
    }
}