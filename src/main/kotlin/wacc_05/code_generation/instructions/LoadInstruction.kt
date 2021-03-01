package wacc_05.code_generation.instructions

import wacc_05.code_generation.AddressingMode
import wacc_05.code_generation.AddressingMode.AddressingMode2
import wacc_05.code_generation.AddressingMode.AddressingMode3
import wacc_05.code_generation.Condition
import wacc_05.code_generation.Register

class LoadInstruction(private val reg: Register, private val addrMode: AddressingMode, private val cond: Condition? = null) : Instruction {

//    LDR r0, =8
//    LDR r5, [sp, #4]
//    LDR r4, [r4]
//
//    LDRSB r4, [sp, #1]
//    LDRSB r4, [sp]
//    LDRSB r4, [r4]

    override fun toString(): String {
        return if (addrMode is AddressingMode2 || addrMode is AddressingMode.AddressingLabel) {
            "LDR${cond ?: ""} $reg, $addrMode"
        } else if (addrMode is AddressingMode3) {
            "LDRSB $reg, $addrMode"
        } else throw Exception()
    }
}