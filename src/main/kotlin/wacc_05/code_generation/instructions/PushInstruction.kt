package wacc_05.code_generation.instructions

import wacc_05.code_generation.utilities.Register

// actually takes in a list of registers but most of the
// examples, looks like we just need to pass in one
// TODO: change to list of registers maybe?
class PushInstruction(private val reg: Register) : Instruction {

    override fun toString(): String {
        return "PUSH {$reg}"
    }

}