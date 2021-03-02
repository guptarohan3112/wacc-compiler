package wacc_05.code_generation

abstract class Operand {
    abstract override fun toString(): String
}

class ShiftOperand(private val reg: Register, private val shift: Shift, private val shiftAmount: Int) : Operand() {
    enum class Shift {
        LSL,
        LSR,
        ASR,
        ROR
    }

    override fun toString(): String {
        return "$reg, $shift #$shiftAmount"
    }
}