package wacc_05.code_generation.instructions

class BranchInstruction(private val label: String, private val cond: Condition? = null) : Instruction {

    override fun toString(): String {
        return if(cond != null) "B$cond $label" else "B $label"
    }

    enum class Condition {
        L,
        EQ,
        NE,
        GT,
        GE,
        LT,
        LE
    }
}