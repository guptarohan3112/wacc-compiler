package wacc_05.code_generation.instructions

import wacc_05.code_generation.AddressingMode.AddressingMode2
import wacc_05.code_generation.Register

class StoreInstruction(private val reg: Register, private val addrMode: AddressingMode2) : Instruction {

    override fun toString(): String {
        return "STR $reg, $addrMode"
    }

}