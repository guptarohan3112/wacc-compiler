package wacc_05.symbol_table.identifier_objects

class ParamIdentifier(private val type: TypeIdentifier) : IdentifierObject() {

    override fun getType(): TypeIdentifier {
        return type
    }

}