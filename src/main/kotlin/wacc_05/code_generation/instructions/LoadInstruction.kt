package wacc_05.code_generation.instructions

class LoadInstruction() : Instruction {

//    LDR r0, =8
//    LDR r5, [sp, #4]
//    LDR r4, [sp]
//    LDR r4, [r4]
//
//    LDRSB r4, [sp, #1]
//    LDRSB r4, [sp]
//    LDRSB r4, [r4]

    override fun toString(): String {
        return "LDR"
    }

}