package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable

abstract class AST {

    private var set: Boolean = false
    var st: SymbolTable? = null
        set(table) {
            if(field == null && table != null) {
                field = table
            }
        }

    // Function that applies semantic checks
    abstract fun check(st: SymbolTable, errorHandler: SemanticErrors)

    abstract fun translate(regs: Registers) : ArrayList<Instruction>

    abstract fun <T> accept(visitor: ASTVisitor<T>): T
}