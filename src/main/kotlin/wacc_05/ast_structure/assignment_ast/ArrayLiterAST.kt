package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.ExprAST

class ArrayLiterAST(private val elems : ArrayList<ExprAST>) : AssignRHSAST() {
    override fun check() {
        TODO("Not yet implemented")
    }
}