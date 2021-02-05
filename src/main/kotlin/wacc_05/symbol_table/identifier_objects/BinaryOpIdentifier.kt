package wacc_05.symbol_table.identifier_objects

class BinaryOpIdentifier(
    private val operator: BinOp,
    private val argType1: TypeIdentifier,
    private val argType2: TypeIdentifier,
    private val returnType: TypeIdentifier
) : IdentifierObject() {
    enum class BinOp {
        MULT,
        DIVIDE,
        MODULO,
        PLUS,
        MINUS,
        GT,
        GTE,
        LT,
        LTE,
        EQUALITY,
        INEQUALITY,
        LAND,
        LOR
    }
}