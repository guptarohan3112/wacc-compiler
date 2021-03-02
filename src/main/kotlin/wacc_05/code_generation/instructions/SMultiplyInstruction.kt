package wacc_05.code_generation.instructions

import wacc_05.code_generation.Register

class SMultiplyInstruction(private val reg1: Register, private val reg2: Register) :
    Instruction {

    override fun toString(): String {
        return "SMUL $reg1, $reg2, $reg1, $reg2"
    }
}