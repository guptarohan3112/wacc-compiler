package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.ArgListAST

class FuncCallAST(private val function: String, private val args : ArgListAST?) : AssignRHSAST() {
    override fun check() {
        TODO("Not yet implemented")
    }
}