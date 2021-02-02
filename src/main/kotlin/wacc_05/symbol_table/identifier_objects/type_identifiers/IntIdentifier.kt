package wacc_05.symbol_table.identifier_objects.type_identifiers

class IntIdentifier(private val min : Int = Int.MIN_VALUE, private val max : Int = Int.MAX_VALUE) : TypeIdentifier() {

    public fun valid(value : Int) : Boolean {
        return value in min until max
    }

}