package wacc_05.ast_structure

sealed class ExprAST : AST {

    data class UnOpAST(val expr: ExprAST,
                       val op: uOperator) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

        enum class uOperator {
            NOT, LEN, ORD, CHR // MINUS '-'
        }
    }

    data class BinOpAST(val expr1: ExprAST,
                        val expr2: ExprAST,
                        val op: bOperator) : ExprAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

        enum class bOperator {
            MULT, DIV, MOD, ADD, SUB, GT, GTE, LT, LTE, EQ, NEQ, AND, OR
        }

    }

}