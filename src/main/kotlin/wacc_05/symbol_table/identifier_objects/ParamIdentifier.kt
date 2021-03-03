package wacc_05.symbol_table.identifier_objects

class ParamIdentifier(private val type: TypeIdentifier) : VariableIdentifier(type) {

    override fun getType(): TypeIdentifier {
        return type
    }
}