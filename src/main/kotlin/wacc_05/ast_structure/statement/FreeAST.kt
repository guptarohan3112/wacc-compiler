package wacc_05.ast_structure.statement

class FreeAST : StatementAST() {

    val expr : ExprAST

    override fun check() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "Free ${expr.toString()}"
    }
}