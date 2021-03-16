package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Condition

class BranchInstruction(private val label: String, private val cond: Condition? = null) :
    Instruction {
    override fun toString(): String {
        return "B${cond ?: ""} $label"
    }
}