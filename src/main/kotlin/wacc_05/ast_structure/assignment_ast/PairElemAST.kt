package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.ExprAST

class PairElemAST(private val elem : ExprAST) : AssignRHSAST() {
    override fun check() {
        TODO("Not yet implemented")
    }
}