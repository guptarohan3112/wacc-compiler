package wacc_05.symbol_table.identifier_objects

class UnaryOpIdentifier(
    private val argType: TypeIdentifier,
    private val returnType: TypeIdentifier = argType
) : IdentifierObject() {

    override fun getType(): TypeIdentifier {
        return returnType
    }
}