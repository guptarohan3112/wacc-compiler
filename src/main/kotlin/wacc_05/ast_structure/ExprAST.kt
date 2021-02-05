package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class ExprAST : AssignRHSAST() {

    data class UnOpAST(val expr: ExprAST) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class BinOpAST(val expr1: ExprAST,
                        val expr2: ExprAST) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

    }

}