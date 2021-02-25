package wacc_05.code_generation

import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object Registers {

    // put in the registers R0-R15
    val r0 = Register(0)
    val r1 = Register(1)
    val r2 = Register(2)
    val r3 = Register(3)
    private val r4 = Register(4)
    private val r5 = Register(5)
    private val r6 = Register(6)
    private val r7 = Register(7)
    private val r8 = Register(8)
    private val r9 = Register(9)
    private val r10 = Register(10)
    private val r11 = Register(11)
    private val r12 = Register(12)
    val sp = Register(13)
    val lr = Register(14)
    val pc = Register(15)

    private val allRegisters: ArrayList<Register> = arrayListOf(r4, r5, r6, r7, r8, r9, r10, r11, r12)

    private val available: ArrayList<Register> = allRegisters

    // should only use registers R0 - R12 (general purpose)
    private val inUse: HashSet<Register> = HashSet()

    fun allocate(): Register {
        if (available.isNotEmpty()) {
            val result: Register = available.removeAt(0)
            inUse.add(result)
            return result
        }

        // use stack or accumulator machine approach
        throw Exception()
    }

    fun free(reg: Register) {
        if (inUse.remove(reg)) {
            available.insert(reg)
        }
    }

    private fun ArrayList<Register>.insert(reg: Register) {
        var i: Int = 0;
        while (reg > get(i)) {
            i++
        }

        add(i, reg)
    }
}