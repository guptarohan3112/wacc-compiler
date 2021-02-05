package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST

class ArrayElemAST(private val ident : String, private val exprs : ArrayList<ExprAST>) : AST {
    override fun check() {
        TODO("Not yet implemented")
    }

}
