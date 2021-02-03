package wacc_05.ast_structure.statement

import wacc_05.ast_structure.expression.ExprAST

class IfAST(val condExpr: ExprAST,
            val thenStat: StatementAST,
            val elseStat: StatementAST) : StatementAST() {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }

}