package wacc_05.symbol_table.identifier_objects

class ParamIdentifier(private val type: TypeIdentifier) : IdentifierObject() {

    private var offset: Int = -1

    override fun getType(): TypeIdentifier {
        return type
    }

    fun getOffset(): Int {
        return offset
    }

    fun setOffset(offset: Int) {
        this.offset = offset
    }

}