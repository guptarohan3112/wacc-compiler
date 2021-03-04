package wacc_05.symbol_table.identifier_objects

class FunctionIdentifier(
    private val returnType: TypeIdentifier,
    private val params: ArrayList<ParamIdentifier>
) : IdentifierObject() {

    // Amount of stack space (bytes) allocated for local variables
    private var stackSize: Int = 0

    fun getReturnType(): TypeIdentifier {
        return returnType
    }

    fun getParams(): ArrayList<ParamIdentifier> {
        return params
    }

    override fun getType(): TypeIdentifier {
        return getReturnType()
    }

    fun getStackSize(): Int {
        return stackSize
    }

    fun setStackSize(stackSize: Int) {
        this.stackSize = stackSize
    }
}