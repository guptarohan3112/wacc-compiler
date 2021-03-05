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

    abstract fun <T> accept(visitor: ASTVisitor<T>): T


    /* Functions for communicating with a node's symbol table - aimed at reducing double getters and dot chains
    * in translator visitor */

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

    fun clearAST() {
        set = false
        st = null
    }
}