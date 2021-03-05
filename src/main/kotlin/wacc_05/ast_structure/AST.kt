package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable

abstract class AST {

    private var set: Boolean = false
    var st: SymbolTable? = null
        set(table) {
            if (field == null && table != null) {
                field = table
            }
        }

    fun st(): SymbolTable {
        return st!!
    }

    fun getStackPtr(): Int {
        return st().getStackPtr()
    }

    fun setStackPtr(ptr: Int) {
        st().setStackPtr(ptr)
    }

    fun getStackSizeAllocated(): Int {
        return st().getStackSizeAllocated()
    }

    fun setStackSizeAllocated(allocation: Int) {
        st().setStackSizeAllocated(allocation)
    }

    fun getParamOffset(): Int {
        return st().getParamOffset()
    }

    fun setParamOffset(offset: Int) {
        st().setParamOffset(offset)
    }

    abstract fun <T> accept(visitor: ASTVisitor<T>): T
}