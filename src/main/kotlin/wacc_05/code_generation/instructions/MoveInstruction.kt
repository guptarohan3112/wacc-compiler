package wacc_05.code_generation.instructions

import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class MoveInstruction(private val reg: Register, private val operand: Operand) : Instruction {

    override fun toString(): String {
        return "MOV $reg, $operand"
    }

}