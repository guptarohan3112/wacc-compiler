package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.symbol_table.SymbolTable

interface AST {
    // Function that applies semantic checks
    fun check(st: SymbolTable, errorHandler: SemanticErrors)

    fun translate() : ArrayList<Instruction>
}