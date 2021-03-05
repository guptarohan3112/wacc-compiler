package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Register

class MultiplyInstruction(
    private val dest: Register,
    private val regM: Register,
    private val regS: Register
) :
    Instruction {

    override fun toString(): String {
        return "MUL $dest, $regM, $regS"
    }
}