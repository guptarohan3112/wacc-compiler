package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction

sealed class IOInstruction {

    abstract fun applyIO(): ArrayList<Instruction>

    //TODO: Remaining p_classes to be defined


    class p_check_null_pointer : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_null_pointer
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_read_int : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_read_int
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_print_ln() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_ln
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_throw_runtime_error() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_throw_runtime_error
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_print_int() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_int
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_print_string : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_string
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    data class p_divide_by_zero(val reg: Register) : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_divide_by_zero && this.reg.compareTo(other.reg) == 0
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

}
