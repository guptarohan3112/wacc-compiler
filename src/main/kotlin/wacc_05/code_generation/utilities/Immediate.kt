package wacc_05.code_generation.utilities

class Immediate(private val value: Int) : Operand() {
    override fun toString(): String {
        return "#$value"
    }

    fun getValue(): Int {
        return value
    }
}

class ImmediateChar(private val value: String) : Operand() {
    override fun toString(): String {
        return "#'${value[0]}'"
    }
}