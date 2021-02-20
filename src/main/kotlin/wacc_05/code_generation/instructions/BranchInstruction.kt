package wacc_05.code_generation.instructions

class BranchInstruction(private val label: String) : Instruction {
    override fun toString(): String {
        return "B $label"
    }
}