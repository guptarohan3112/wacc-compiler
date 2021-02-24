package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(val ctx: WaccParser.FuncCallContext, val funcName: String, val args: ArrayList<ExprAST>) :
    AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return (st.lookupAll(funcName) as FunctionIdentifier).getReturnType()
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFuncCallAST(this)
    }

}