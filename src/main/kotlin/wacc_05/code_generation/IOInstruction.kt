package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction

sealed class IOInstruction {

    abstract fun applyIO() : ArrayList<Instruction>

}
