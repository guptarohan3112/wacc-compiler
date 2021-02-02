package wacc_05.ast_structure.statement

import wacc_05.ast_structure.AssignrhsAST

class DeclarationAST(val rhs : AssignrhsAST) : StatementAST() {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }
}