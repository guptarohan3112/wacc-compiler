package wacc_05.code_generation

// R0-R12 general purpose
// R13 - stack pointer [sp]
// R14 - subroutine link register [lr]
// R15 - program counter [pc]

class Register(private val number: Int) : Operand(), Comparable<Register> {

    override fun toString(): String {
        return "r$number"
    }


    override fun compareTo(other: Register): Int {
        return number - other.number
    }
}