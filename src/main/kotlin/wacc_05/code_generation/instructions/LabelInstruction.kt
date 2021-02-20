package wacc_05.code_generation.instructions

//TODO: maybe add an option to just assign next label
// e.g. if L0, L1 have been assigned, return "L2:"
class LabelInstruction(private val name: String):Instruction {

    override fun toString(): String {
        return "$name:"
    }

}