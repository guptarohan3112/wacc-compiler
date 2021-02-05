package wacc_05.ast_structure

sealed class AssignrhsAST() : AST {

    // Expression?

    data class NewPairAST(val expr1: ExprAST, val expr2: ExprAST) : AssignrhsAST() {

        override fun check() {
            // Not implemented
        }

    }

}
