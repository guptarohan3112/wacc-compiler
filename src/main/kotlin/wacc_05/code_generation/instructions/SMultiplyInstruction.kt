package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Register

class SMultiplyInstruction(private val dest1: Register, private val dest2: Register, private val reg1: Register, private val reg2: Register) :
    Instruction {

    override fun toString(): String {
        return "SMULL $dest1, $dest2, $reg1, $reg2"
    }
}