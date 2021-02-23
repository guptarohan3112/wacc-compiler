package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable

interface AST {

    // Function that applies semantic checks
    fun check(st: SymbolTable, errorHandler: SemanticErrors)

    fun translate(regs: Registers) : ArrayList<Instruction>

//    fun <T> accept(visitor: ASTVisitor<T>): T
}