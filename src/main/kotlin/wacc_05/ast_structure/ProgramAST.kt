package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.symbol_table.SymbolTable
import java.util.*
import kotlin.collections.ArrayList

class ProgramAST(
    private val functionList: ArrayList<FunctionAST>,
    private val stat: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

        // preliminary pass through the function list to add all function
        // identifiers to the symbol table
        for (func in functionList) {
            func.preliminaryCheck(st, errorHandler)
        }

        for (func in functionList) {
            func.check(st, errorHandler)
        }

        // Check validity of statement
        stat.check(st, errorHandler)
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

}