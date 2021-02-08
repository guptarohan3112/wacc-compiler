package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable

class ArrayLiterAST(private val elems: ArrayList<ExprAST>) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        elems[0].check(st, errorHandler)
        val firstElemType = elems[0].getType()

        for (i in 1 until elems.size) {
            elems[i].check(st, errorHandler)
            if (elems[i].getType() != firstElemType) {
                errorHandler.typeMismatch(firstElemType, elems[i].getType())
            }
        }
    }

}