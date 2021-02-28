package wacc_05.symbol_table.identifier_objects

import wacc_05.symbol_table.SymbolTable

class FunctionIdentifier(
    private val returnType: TypeIdentifier,
    private val params: ArrayList<ParamIdentifier>,
    private val st: SymbolTable
) : IdentifierObject() {

    private var stackSize: Int = 0

    fun getSymbolTable(): SymbolTable {
        return st
    }

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