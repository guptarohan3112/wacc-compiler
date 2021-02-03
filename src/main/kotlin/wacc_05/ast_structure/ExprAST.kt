package wacc_05.ast_structure

import wacc_05.ast_structure.AST

sealed class ExprAST : AST {

    data class UnOpAST(val expr: ExprAST) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class BinOpAST(val expr1: ExprAST,
                        val expr2: ExprAST
    ) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

    }

}