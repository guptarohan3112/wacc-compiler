package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register

class EorInstruction(
    private val dest: Register,
    private val op1: Operand,
    private val op2: Operand
) : Instruction {

    override fun toString(): String {
        return "EOR $dest, $op1, $op2"
    }

}
