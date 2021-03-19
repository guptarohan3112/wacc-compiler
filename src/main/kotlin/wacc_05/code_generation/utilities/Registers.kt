package wacc_05.code_generation.utilities

import wacc_05.code_generation.instructions.Instruction
import wacc_05.code_generation.instructions.PopInstruction
import wacc_05.code_generation.instructions.PushInstruction
import java.util.*
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
    val r10 = Register(10)
    val r11 = Register(11)
    val r12 = Register(12)
    val sp = Register(13)
    val lr = Register(14)
    val pc = Register(15)

    private val allRegisters: ArrayList<Register> = arrayListOf(r3, r4, r5, r6, r7, r8, r9)

    private val available: ArrayList<Register> = allRegisters

    // Only use registers R4 - R9 (general purpose)
    private val inUse: HashSet<Register> = HashSet()

    fun allocate(): Register {
        if (available.isNotEmpty()) {
            val result: Register = available.removeAt(0)
            inUse.add(result)
            return result
        }

        // Use stack or accumulator machine approach
        throw Exception()
    }

    fun saveRegisters(): Pair<ArrayList<Instruction>, Stack<Register>> {
        val list: ArrayList<Instruction> = ArrayList()
        val saved: Stack<Register> = Stack()

        for (reg: Register in inUse) {
            list.add(PushInstruction(reg))
            saved.push(reg)
            inUse.remove(reg)
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

    fun allRegisters(): ArrayList<Register> {
        return allRegisters
    }

    fun free(reg: Register) {
        if (inUse.remove(reg)) {
            available.insert(reg)
        }
    }

    fun freeAll() {
        for (reg in allRegisters) {
            if (inUse.remove(reg)) {
                available.insert(reg)
            }
        }
        r10.reset()
        r11.reset()
        r12.reset()
    }

    private fun ArrayList<Register>.insert(reg: Register) {
        var i = 0
        while (i < size && reg > get(i)) {
            i++
        }

        add(i, reg)
    }

    fun full(): Boolean {
        return available.size == 1
    }
}