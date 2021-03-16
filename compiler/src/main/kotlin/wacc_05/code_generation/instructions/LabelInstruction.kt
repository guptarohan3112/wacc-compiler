package wacc_05.code_generation.instructions

open class LabelInstruction(private val name: String) : Instruction {

    companion object {
        private var currentLabel: Int = 0

        fun getUniqueLabel(): LabelInstruction {
            return LabelInstruction("L${currentLabel++}")
        }

        fun reset() {
            currentLabel = 0
        }
    }

    override fun toString(): String {
        return "$name:"
    }

    fun getLabel(): String {
        return name
    }
}