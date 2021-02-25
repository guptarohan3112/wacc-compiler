package wacc_05.code_generation

import wacc_05.code_generation.instructions.*

sealed class IOInstruction {

    abstract fun applyIO(): ArrayList<Instruction>

    //TODO: Remaining p_classes to be defined


    class p_check_null_pointer : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
//                320	p_check_null_pointer:
//                321		PUSH {lr}
//                322		CMP r0, #0
//                323		LDREQ r0, =msg_7
//                324		BLEQ p_throw_runtime_error
//                325		POP {pc}
                LabelInstruction("p_check_null_pointer"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                PopInstruction(Registers.pc)
                // TODO: add remaining internal representation instructions for this io instruction
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
                LabelInstruction("p_read_int"),
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
                LabelInstruction("p_print_ln"),
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
                LabelInstruction("p_throw_runtime_error")
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
                LabelInstruction("p_check_null_pointer"),
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
                LabelInstruction("p_print_string"),
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
                LabelInstruction("p_divide_by_zero"),
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
