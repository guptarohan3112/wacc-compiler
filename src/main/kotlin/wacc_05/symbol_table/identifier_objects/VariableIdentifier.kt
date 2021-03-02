package wacc_05.symbol_table.identifier_objects

open class VariableIdentifier(private val type: TypeIdentifier) : IdentifierObject() {

    private var addr: Int = -1

    override fun getType(): TypeIdentifier {
        return type
    }

    fun getAddr(): Int {
        return addr
    }

    fun setAddr(addr: Int) {
        this.addr = addr
    }

}