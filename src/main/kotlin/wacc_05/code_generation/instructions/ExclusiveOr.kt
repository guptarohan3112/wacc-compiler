package wacc_05.code_generation.instructions

import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class ExclusiveOr(private val dest: Register, private val op1: Operand, private val op2: Operand) : Instruction {

    override fun toString(): String {
        return "EOR $dest, $op1, $op2"
    }

}
