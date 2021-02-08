package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable

class NewPairAST(private val fst: ExprAST, private val snd: ExprAST) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        fst.check(st, errorHandler)
        snd.check(st, errorHandler)
    }

}