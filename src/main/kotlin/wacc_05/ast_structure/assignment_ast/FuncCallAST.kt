package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ASTVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(
    ctx: WaccParser.FuncCallContext,
    val funcName: String,
    val args: ArrayList<ExprAST>
) : AssignRHSAST(ctx) {

    override fun getType(): TypeIdentifier {
        return functionST?.lookupAll(funcName)!!.getReturnType()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFuncCallAST(this)
    }
}