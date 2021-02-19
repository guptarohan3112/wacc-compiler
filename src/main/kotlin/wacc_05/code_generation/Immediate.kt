package wacc_05.code_generation

// ARM only allows immediates to be integers from what I have seen
class Immediate(private val value: Int) : Operand() {
    override fun toString(): String {
        return "#$value"
    }
}