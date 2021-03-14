package wacc_05.ast_structure

import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier

abstract class AST {

    private var startIndex: Int = 0
    private var endIndex: Int = 0

    private var set: Boolean = false
    var st: SymbolTable? = null
        set(table) {
            if (field == null && table != null) {
                field = table
            }
        }

    var functionST: FunctionST? = null
        set(table) {
            if (field == null && table != null) {
                field = table
            }
        }

    abstract fun <T> accept(visitor: ASTVisitor<T>): T

    /* Functions for communicating with a node's symbol table - aimed at reducing double getters and dot chains
    * in translator visitor */

    fun lookupFunction(name: String): FunctionIdentifier? {
        return functionST?.lookup(name)
    }

    fun addFunction(name: String, func: FunctionIdentifier) {
        functionST?.add(name, func)
    }

    // Getter for the symbol table, dereferencing it before returning
    fun st(): SymbolTable {
        return st!!
    }

    fun getStackPtrOffset(): Int {
        return st().getStackPtrOffset()
    }

    fun updatePtrOffset(ofs: Int) {
        st().updatePtrOffset(ofs)
    }

    // Getter and setter for the stack pointer in the symbol table
    fun getStackPtr(): Int {
        return st().getStackPtr()
    }

    fun setStackPtr(ptr: Int) {
        st().setStackPtr(ptr)
    }

    // Getter and setter for the stackSizeAllocated field in the symbol table
    fun getStackSizeAllocated(): Int {
        return st().getStackSizeAllocated()
    }

    fun setStackSizeAllocated(allocation: Int) {
        st().setStackSizeAllocated(allocation)
    }

    // Getter and setter for the parameter offset field in symbol table
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