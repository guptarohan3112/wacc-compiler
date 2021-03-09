package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Register

class PushInstruction(private val reg: Register) : Instruction {

    override fun toString(): String {
        return "PUSH {$reg}"
    }

}