package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class ExprAST : AssignRHSAST() {

    data class IntLiterAST(private val sign: String, private val value: String) : ExprAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class BoolLiterAST(private val value: String) : ExprAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class CharLiterAST(private val value: String) : ExprAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class StrLiterAST(private val value: String) : ExprAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class PairLiterAST(private val value: String) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class IdentAST(private val value: String) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class ArrayElemAST(
        private val ident : String,
        private val exprs : ArrayList<ExprAST>
        ) : ExprAST() {
            override fun check() {
                TODO("Not yet implemented")
            }
    }

    data class UnOpAST(
        private val expr: ExprAST,
        private val UnaryOp: String
        ) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class BinOpAST(
        val expr1: ExprAST,
        val expr2: ExprAST
        ) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

    }

}