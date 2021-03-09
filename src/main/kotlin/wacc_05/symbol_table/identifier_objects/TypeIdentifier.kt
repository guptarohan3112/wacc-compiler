package wacc_05.symbol_table.identifier_objects

open class TypeIdentifier : IdentifierObject() {

    open fun getStackSize(): Int {
        return 0
    }

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
        val STRING_TYPE = StringIdentifier(0)
        val PAIR_LIT_TYPE = PairLiterIdentifier
        val GENERIC_PAIR_TYPE = GenericPairType()

        const val INT_SIZE: Int = 4
        const val CHAR_SIZE: Int = 1
        const val BOOL_SIZE: Int = 1
        const val ADDR_SIZE: Int = 4
        const val ARR_SIZE: Int = ADDR_SIZE
        const val STRING_SIZE: Int = ADDR_SIZE
        const val PAIR_SIZE: Int = ADDR_SIZE
    }

    object BoolIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return BOOLEAN
        }

        override fun getStackSize(): Int {
            return BOOL_SIZE
        }

    }

    object CharIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return CHARACTER
        }

        override fun getStackSize(): Int {
            return CHAR_SIZE
        }

    }

    class StringIdentifier(private val length: Int) : TypeIdentifier() {
        override fun toString(): String {
            return STRING
        }

        override fun getStackSize(): Int {
            return STRING_SIZE
        }

        override fun equals(other: Any?): Boolean {
            return other is StringIdentifier
        }

        override fun hashCode(): Int {
            return length
        }
    }

    data class IntIdentifier(
        private val min: Int = Int.MIN_VALUE,
        private val max: Int = Int.MAX_VALUE
    ) :
        TypeIdentifier() {

        override fun toString(): String {
            return INTEGER
        }

        override fun getStackSize(): Int {
            return INT_SIZE
        }

    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) :
        TypeIdentifier() {
        override fun toString(): String {
            return "$ARRAY[$elemType]"
        }

        override fun getType(): TypeIdentifier {
            return elemType
        }

        fun size(): Int {
            return size
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

        override fun getStackSize(): Int {
            return ARR_SIZE
        }

    }

    // A generic pair type to capture the overall pair type. Used to represent the loss
    // of type with nested pairs.
    open class GenericPairType : TypeIdentifier() {
        override fun equals(other: Any?): Boolean {
            return other is GenericPairType
        }

        override fun getStackSize(): Int {
            return PAIR_SIZE
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    data class PairIdentifier(
        private val fstType: TypeIdentifier,
        private val sndType: TypeIdentifier
    ) :
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