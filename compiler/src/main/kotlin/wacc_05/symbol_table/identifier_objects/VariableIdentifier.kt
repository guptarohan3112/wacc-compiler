package wacc_05.symbol_table.identifier_objects

open class VariableIdentifier(private val type: TypeIdentifier) : IdentifierObject() {

    // Absolute stack address of a variable
    private var addr: Int = -1

    // Field indicating whether a variable has been allocated on the stack
    private var stackAllocated: Boolean = false

    override fun getType(): TypeIdentifier {
        return type
    }

    fun getAddr(): Int {
        return addr
    }

    fun setAddr(addr: Int) {
        this.addr = addr
    }

    fun isAllocated(): Boolean {
        return stackAllocated
    }

    fun allocatedNow() {
        this.stackAllocated = true
    }

}