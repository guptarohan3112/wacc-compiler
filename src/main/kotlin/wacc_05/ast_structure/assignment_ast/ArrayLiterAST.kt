package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable

class ArrayLiterAST(private val elems: ArrayList<ExprAST>) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        for (elem in elems) {
            elem.check(st, errorHandler)
        }
    }

}