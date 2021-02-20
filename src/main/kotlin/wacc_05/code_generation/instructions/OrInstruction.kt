package wacc_05.code_generation.instructions

import wacc_05.code_generation.Operand
import wacc_05.code_generation.Register

class OrInstruction(private val dest: Register, private val reg: Register, private val operand: Operand) : Instruction {

    override fun toString(): String {
        // note: or represented by ORR in ARM11
        return "ORR $dest, $reg, $operand"
    }
}