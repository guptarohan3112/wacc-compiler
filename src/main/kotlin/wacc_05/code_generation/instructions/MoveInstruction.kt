package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Condition
import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register

class MoveInstruction(
    private val reg: Register,
    private val operand: Operand,
    private val cond: Condition? = null
) :
    Instruction {

    override fun toString(): String {
        return "MOV${cond ?: ""} $reg, $operand"
    }

}