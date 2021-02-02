package wacc_05.symbol_table.identifier_objects.type_identifiers

class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) {

    public fun valid(index: Int): Boolean {
        return index in 0 until size
    }

}