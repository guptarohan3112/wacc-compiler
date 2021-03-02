package wacc_05.code_generation.instructions

import wacc_05.code_generation.AddressingMode.AddressingMode2
import wacc_05.code_generation.Condition
import wacc_05.code_generation.Register

class StoreInstruction(
    private val reg: Register,
    private val addrMode: AddressingMode2,
    private val cond: Condition? = null
) : Instruction {

    /*
    STR <Rd>, [<Rn>, <Rm>]   Word
    STRH <Rd>, [<Rn>, <Rm>]  Halfword
    STRB <Rd>, [<Rn>, <Rm>]  Byte
     */

    override fun toString(): String {
        return "STR${cond ?: ""} $reg, $addrMode"
    }

}