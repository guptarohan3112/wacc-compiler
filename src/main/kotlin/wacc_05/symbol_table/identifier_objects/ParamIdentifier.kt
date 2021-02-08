package wacc_05.symbol_table.identifier_objects

class ParamIdentifier(private val type: TypeIdentifier, private val ident: String) : IdentifierObject() {

    fun getType() : TypeIdentifier {
        return type
    }

}