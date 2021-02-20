package wacc_05.code_generation

// R0-R12 general purpose
// R13 - stack pointer [sp]
// R14 - subroutine link register [lr]
// R15 - program counter [pc]

class Register(private val number: Int) : Operand() {

    override fun toString(): String {
        return "r$number"
    }
}