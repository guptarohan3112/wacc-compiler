package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register

class AndInstruction(
    private val dest: Register,
    private val reg: Register,
    private val operand: Operand
) : Instruction {

    override fun toString(): String {
        return "AND $dest, $reg, $operand"
    }
}