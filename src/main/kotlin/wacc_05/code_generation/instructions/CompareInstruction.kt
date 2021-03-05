package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register

class CompareInstruction(private val reg: Register, private val operand: Operand) : Instruction {

    override fun toString(): String {
        return "CMP $reg, $operand"
    }
}