package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.code_generation.instructions.Instruction

class FunctionAST(
    val ctx: WaccParser.FuncContext,
    val returnType: TypeAST,
    val funcName: String,
    val paramList: ParamListAST?,
    val body: StatementAST
) : AST() {

    override fun translate(): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFunctionAST(this)
    }
}
