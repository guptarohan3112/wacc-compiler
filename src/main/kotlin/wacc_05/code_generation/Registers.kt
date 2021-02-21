package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction
import wacc_05.code_generation.instructions.PopInstruction
import wacc_05.code_generation.instructions.PushInstruction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

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

        val allRegisters: ArrayList<Register> = arrayListOf(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
    }

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

    fun saveRegisters(exceptions: HashSet<Register>): Pair<ArrayList<Instruction>, Stack<Register>> {
        val list: ArrayList<Instruction> = ArrayList()
        val saved: Stack<Register> = Stack()

        for (reg: Register in inUse) {
            if (!exceptions.contains(reg)) {
                list.add(PushInstruction(reg))
                saved.push(reg)
                inUse.remove(reg)
            }
        }

        return Pair(list, saved)
    }

    fun restoreRegisters(saved: Stack<Register>): ArrayList<Instruction> {
        val list: ArrayList<Instruction> = ArrayList()

        while (saved.isNotEmpty()) {
            val reg: Register = saved.pop()
            inUse.add(reg)
            list.add(PopInstruction(reg))
        }

        return list
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