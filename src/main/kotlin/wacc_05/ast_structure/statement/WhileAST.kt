package wacc_05.ast_structure.statement

import wacc_05.ast_structure.expression.ExprAST

class WhileAST(val loopExpr: ExprAST,
               val body: StatementAST) : StatementAST() {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }
}