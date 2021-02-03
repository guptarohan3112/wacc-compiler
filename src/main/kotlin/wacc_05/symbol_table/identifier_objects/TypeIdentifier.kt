package wacc_05.symbol_table.identifier_objects

sealed class TypeIdentifier : IdentifierObject() {
    object BoolIdentifier : TypeIdentifier()

    object CharIdentifier : TypeIdentifier()

    data class IntIdentifier(private val min : Int = Int.MIN_VALUE, private val max : Int = Int.MAX_VALUE) : TypeIdentifier() {
        public fun valid(value : Int) : Boolean {
            return value in min until max
        }
    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier()

    data class PairIdentifier(private val fstType : TypeIdentifier, private val sndType : TypeIdentifier) : TypeIdentifier()
}