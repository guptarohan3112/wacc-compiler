package wacc_05.code_generation.instructions

import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class MoveInstruction(private val reg: Register, private val operand: Operand) : Instruction {

    private var conditions: String? = null

    constructor(conditions: String, reg: Register, operand: Operand) : this(reg, operand) {
        this.conditions = conditions
    }

    override fun toString(): String {
        return "MOV${conditions ?: ""} $reg, $operand"
    }

}