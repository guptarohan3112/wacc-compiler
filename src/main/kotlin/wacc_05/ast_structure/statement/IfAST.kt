package wacc_05.ast_structure.statement

import wacc_05.ast_structure.expression.ExprAST

class IfAST : StatementAST() {

    private var condExpr : ExprAST
    private var thenStat : StatementAST
    private var elseStat : StatementAST

    override fun check() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }


}