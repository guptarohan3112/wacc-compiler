package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.symbol_table.SymbolTable
import kotlin.collections.ArrayList

class ProgramAST(
    val functionList: ArrayList<FunctionAST>,
    val stat: StatementAST
) : AST() {

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitProgramAST(this)
    }

}