package wacc_05.code_generation.instructions

import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class SubtractInstruction(private val dest: Register, private val reg: Register, private val operand: Operand) :
    Instruction {

    override fun toString(): String {
        return "SUB $dest, $reg, $operand"
    }
}