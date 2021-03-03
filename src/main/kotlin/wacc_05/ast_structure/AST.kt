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

    abstract fun <T> accept(visitor: ASTVisitor<T>): T
}