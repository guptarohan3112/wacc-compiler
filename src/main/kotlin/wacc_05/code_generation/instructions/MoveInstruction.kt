package wacc_05.code_generation.instructions

import wacc_05.code_generation.Condition
import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class MoveInstruction(private val reg: Register, private val operand: Operand, private val cond: Condition? = null) :
    Instruction {

    override fun toString(): String {
        return "MOV${cond ?: ""} $reg, $operand"
    }

}