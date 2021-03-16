package wacc_05.ast_structure

import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier

class ProgramAST(
    val functionList: ArrayList<FunctionAST>,
    val stat: StatementAST,
) : AST() {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitProgramAST(this)
    }
}