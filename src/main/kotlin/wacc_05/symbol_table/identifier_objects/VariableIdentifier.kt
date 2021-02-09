package wacc_05.symbol_table.identifier_objects

class VariableIdentifier(private val name : String, private val type : TypeIdentifier) : IdentifierObject() {
    fun getType() : TypeIdentifier {
        return type
    }
}