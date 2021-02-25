package wacc_05.code_generation.instructions

class LabelInstruction(private val name: String) : Instruction {

    companion object {
        private var currentLabel: Int = 0

        public fun getUniqueLabel(): LabelInstruction {
            return LabelInstruction("L${currentLabel++}")
        }
    }

    override fun toString(): String {
        return "$name:"
    }

    fun getLabel(): String {
        return name
    }

}