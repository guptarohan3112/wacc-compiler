package wacc_05.ast_structure

import kotlin.collections.ArrayList

class ProgramAST(
    val functionList: ArrayList<FunctionAST>,
    val stat: StatementAST
) : AST() {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitProgramAST(this)
    }
}