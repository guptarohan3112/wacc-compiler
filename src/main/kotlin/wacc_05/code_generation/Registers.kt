package wacc_05.code_generation

class Registers {

    companion object {
        // put in the registers R0-R15
        val r0 = Register(0)
        val r1 = Register(1)
        val r2 = Register(2)
        val r3 = Register(3)
        val r4 = Register(4)
        val r5 = Register(5)
        val r6 = Register(6)
        val r7 = Register(7)
        val r8 = Register(8)
        val r9 = Register(9)
        val r10 = Register(10)
        val r11 = Register(11)
        val r12 = Register(12)
        val sp = Register(13)
        val lr = Register(14)
        val pc = Register(15)
    }

    private val allRegisters: Array<Register> = arrayOf(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)

    // should only use registers R0 - R12 (general purpose)
    private val inUse: HashSet<Register> = HashSet()

    fun allocate(): Register {
        for (register in allRegisters) {
            if (!inUse.contains(register)) {
                inUse.add(register)
                return register
            }
        }

        // use stack or accumulator machine approach
        throw Exception()
    }

    fun free(reg : Register) {
        inUse.remove(reg)
    }
}