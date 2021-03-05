package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.AST
import wacc_05.code_generation.Register
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

abstract class AssignRHSAST : AST() {

    private var dest: Register? = null

    abstract fun getType(): TypeIdentifier

    fun getStackSize(): Int {
        return getType().getStackSize()
    }

    fun getDestReg(): Register {
        return dest!!
    }

    fun setDestReg(reg: Register) {
        this.dest = reg
    }

}