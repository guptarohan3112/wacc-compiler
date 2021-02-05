package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.ExprAST

class PairElemAST(private val fst : ExprAST, private val snd: ExprAST) : AssignRHSAST() {
    override fun check() {
        TODO("Not yet implemented")
    }
}