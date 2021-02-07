package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ArgListAST
import wacc_05.symbol_table.SymbolTable

class FuncCallAST(private val function: String, private val args: ArgListAST?) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        TODO("Not yet implemented")
    }

}