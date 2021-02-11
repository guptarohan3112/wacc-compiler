package wacc_05.symbol_table.identifier_objects

class UnaryOpIdentifier(
    private val operator: UnaryOp,
    private val argType: TypeIdentifier,
    private val returnType: TypeIdentifier = argType
) : IdentifierObject() {

    override fun getType() : TypeIdentifier {
        return returnType
    }

    enum class UnaryOp {
        NOT,
        NEGATIVE,
        LEN,
        ORD,
        CHR
    }
}