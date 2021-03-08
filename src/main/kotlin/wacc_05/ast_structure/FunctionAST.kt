package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier

class FunctionAST(
    val ctx: WaccParser.FuncContext,
    val returnType: TypeAST,
    val funcName: String,
    val paramList: ParamListAST?,
    val body: StatementAST
) : AST() {

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFunctionAST(this)
    }
}
