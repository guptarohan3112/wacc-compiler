package wacc_05.ast_structure.statement

import wacc_05.ast_structure.expression.ExprAST

class FreeAST(val expr : ExprAST) : StatementAST() {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "Free ${expr.toString()}"
    }
}