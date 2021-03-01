package wacc_05.code_generation

// R0-R12 general purpose
// R13 - stack pointer [sp]
// R14 - subroutine link register [lr]
// R15 - program counter [pc]

class Register(private val number: Int) : Operand(), Comparable<Register> {

    override fun toString(): String {
        return when(number) {
            13 -> "sp"
            14 -> "lr"
            15 -> "pc"
            else -> "r$number"
        }
    }


    override fun compareTo(other: Register): Int {
        return number - other.number
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Register

        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        return number
    }


}