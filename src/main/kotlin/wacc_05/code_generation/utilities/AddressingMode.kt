package wacc_05.code_generation.utilities

sealed class AddressingMode : Operand() {

    abstract fun getDestReg(): Register

    abstract fun getOperand(): Operand?

    data class AddressingLabel(private val label: String) : AddressingMode() {
        override fun getDestReg(): Register {
            return Register(-1)
        }

        override fun getOperand(): Operand? {
            return null
        }

        override fun toString(): String {
            return "=$label"
        }
    }

    /*
    Addressing Mode 2:

    Immediate offset [<Rn>, #+/<immed_12>]
    Zero offset [<Rn>]
    Register offset [<Rn>, +/-<Rm>]
    */
    data class AddressingMode2(
        private val regN: Register,
        private val operand: Operand? = null,
        private val decrement: Boolean = false
    ) : AddressingMode() {
        override fun getDestReg(): Register {
            return regN
        }

        override fun getOperand(): Operand? {
            return operand
        }

        override fun toString(): String {
            val dec = if (decrement) {
                "!"
            } else {
                ""
            }
            if (operand == null || (operand is Immediate && operand.getValue() == 0)) {
                return "[$regN]$dec"
            }
            return "[$regN, $operand]$dec"
        }
    }

    /*
    Addressing Mode 3:

    Immediate offset [<Rn>, #+/-<immed_8>]
    Register offset [<Rn>, +/- <Rm>]
     */
    data class AddressingMode3(private val regN: Register, private val operand: Operand) :
        AddressingMode() {
        override fun getDestReg(): Register {
            return regN
        }

        override fun getOperand(): Operand? {
            return operand
        }

        override fun toString(): String {
            if (operand is Immediate && operand.getValue() == 0) {
                return "[$regN]"
            }
            return "[$regN, $operand]"
        }

    }

}