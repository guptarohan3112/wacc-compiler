package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable

class ArgListAST(private val args: ArrayList<ExprAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        TODO("Not yet implemented")
    }

}