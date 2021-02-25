package wacc_05.code_generation

sealed class AddressingMode : Operand() {


    /*
    Addressing Mode 2:

    Immediate offset [<Rn>, #+/<immed_12>]
    Zero offset [<Rn>]
    Register offset [<Rn>, +/-<Rm>]
    */
    data class AddressingMode2(private val regN: Register, private val operand: Operand? = null) : AddressingMode() {
        private var labelName: String? = null
        constructor(regN: Register, labelName: String) : this(regN) {
            this.labelName = labelName
        }

        override fun toString(): String {
            if (labelName != null) {
                return "[$regN, $labelName]"
            } else if (operand != null) {
                return "[$regN, $operand]"
            }
            return "[$regN]"
        }
    }

    /*
    Addressing Mode 3:

    Immediate offset [<Rn>, #+/-<immed_8>]
    Register offset [<Rn>, +/- <Rm>]
     */
    data class AddressingMode3(private val regN: Register, private val operand: Operand) : AddressingMode() {

        override fun toString(): String {
            return "[$regN, $operand]"
        }

    }

}