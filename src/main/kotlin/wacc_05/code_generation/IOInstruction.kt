package wacc_05.code_generation

import wacc_05.code_generation.instructions.*
import wacc_05.front_end.Error

sealed class IOInstruction {

    abstract fun applyIO(): ArrayList<Instruction>


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
                // TODO: Add Null reference msg and reference that below, use LDREQ
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.EQ),
                BranchInstruction("p_throw_runtime_error", Condition.LEQ),
                PopInstruction(Registers.pc)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_null_pointer
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_read_int(val register: Register, val msgName: String) : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_read_int"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, register),
                // TODO: Use msgName correctly
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, msgName)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("scanf", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_read_int && other.msgName == msgName && other.register == register
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_read_char(val register: Register, val msgName: String) : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_read_char"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, register),
                // TODO: Use msgName correctly
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, msgName)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("scanf", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_read_char && other.msgName == msgName && other.register == register
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_print_ln() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_ln"),
                PushInstruction(Registers.lr),
                // TODO: get correct message
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("puts", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
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
                LabelInstruction("p_throw_runtime_error"),
                BranchInstruction("p_print_string", Condition.L),
                MoveInstruction(Registers.r0, Immediate(Error.GENERAL_ERROR)),
                BranchInstruction("exit", Condition.L)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_throw_runtime_error
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_bool() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_bool"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                // TODO: get correct messages for true and false messages
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),

                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_bool
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_int() : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_int"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, Registers.r0),
                // TODO: get correct message
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
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
                PushInstruction(Registers.lr),
                // TODO: Not so sure about one below, we wamt LDR r1, [r0]
                LoadInstruction(Registers.r1, AddressingMode.AddressingMode2(Registers.r1, Registers.r0)),
                AddInstruction(Registers.r2, Registers.r0, Immediate(4)),
                // TODO: get correct message
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_string
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    data class p_check_divide_by_zero(val reg: Register) : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_check_divide_by_zero"),
                PushInstruction(Registers.lr),
                CompareInstruction(reg, Immediate(0)),
                // TODO: get correct message
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.EQ),
                BranchInstruction("p_throw_runtime_error", Condition.LEQ),
                PopInstruction(Registers.lr)
                // TODO: add internal representation instructions for this io instruction
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_divide_by_zero && this.reg == other.reg
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_throw_overflow_error : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_throw_overflow_error"),
                // TODO: Get correct label
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                BranchInstruction("p_throw_runtime_error", Condition.L)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_throw_overflow_error
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_check_array_bounds : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_check_array_bounds"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                // TODO: Get correct error message and use LDRLT
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.LT),
                BranchInstruction("p_throw_runtime_error", Condition.LLT),
                // TODO: Is the below correct?
                LoadInstruction(Registers.r1, AddressingMode.AddressingMode2(Registers.r1, Registers.r1)),
                CompareInstruction(Registers.r0, Registers.r1),
                // TODO: Get correct error message and use LDRCS
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.CS),
                BranchInstruction("p_throw_runtime_error", Condition.CS),
                PopInstruction(Registers.lr)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_array_bounds
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_free_pair : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_free_pair"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                // TODO: Get correct error message and use LDREQ
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.EQ),
                BranchInstruction("p_throw_runtime_error", Condition.EQ),
                PushInstruction(Registers.r0),
                // TODO: Is the below correct?
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, Registers.r0)),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.r0),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.pc),
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_free_pair
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_free_array : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_free_array"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                // TODO: Get correct error message and use LDREQ
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x"), Condition.EQ),
                BranchInstruction("p_throw_runtime_error", Condition.EQ),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.pc),
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_free_array
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_integer_overflow : IOInstruction() {

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_integer_overflow"),
                // TODO: Get correct error message and use LDR{???}
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0, "msg_x")),
                BranchInstruction("p_throw_runtime_error", Condition.L)
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is p_integer_overflow
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }
}
