package wacc_05.code_generation

class Immediate(private val value: Int) : Operand() {
    override fun toString(): String {
        return "#$value"
    }
}

class ImmediateChar(private val value: String) : Operand() {
    override fun toString(): String {
        return "#'${value[0]}'"
    }
}