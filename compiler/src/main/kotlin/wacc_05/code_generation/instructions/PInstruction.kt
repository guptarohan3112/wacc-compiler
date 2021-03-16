package wacc_05.code_generation.instructions

import wacc_05.code_generation.*
import wacc_05.code_generation.utilities.AddressingMode
import wacc_05.code_generation.utilities.Condition
import wacc_05.code_generation.utilities.Immediate
import wacc_05.code_generation.utilities.Registers
import wacc_05.front_end.ErrorCode

sealed class PInstruction(private val representation: AssemblyRepresentation) {

    abstract fun applyIO(): ArrayList<Instruction>
    abstract fun addMessageLabel()
    abstract fun checkRuntimeErr()

    class p_check_null_pointer(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_check_null_pointer"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(label),
                    Condition.EQ
                ),
                BranchInstruction("p_throw_runtime_error", Condition.LEQ),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val nullLabel = MessageLabelInstruction.getUniqueLabel(
                "NullReferenceError: dereference a null reference\\n\\0"
            )
            representation.addDataInstr(nullLabel)
            this.label = nullLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_null_pointer
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_read_int(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_read_int"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, Registers.r0),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("scanf", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val readLabel = MessageLabelInstruction.getUniqueLabel("%d\\0")
            representation.addDataInstr(readLabel)
            this.label = readLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_read_int
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_read_char(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_read_char"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, Registers.r0),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("scanf", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val readLabel = MessageLabelInstruction.getUniqueLabel(" %c\\0")
            representation.addDataInstr(readLabel)
            this.label = readLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_read_char
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_print_ln(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_ln"),
                PushInstruction(Registers.lr),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("puts", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val terminalLabel = MessageLabelInstruction.getUniqueLabel("\\0")
            representation.addDataInstr(terminalLabel)
            label = terminalLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_ln
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_throw_runtime_error(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_throw_runtime_error"),
                BranchInstruction("p_print_string", Condition.L),
                MoveInstruction(Registers.r0, Immediate(ErrorCode.GENERAL_ERROR)),
                BranchInstruction("exit", Condition.L)
            )
        }

        override fun addMessageLabel() {
            return
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_throw_runtime_error
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_bool(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var labelTrue: String = ""
        private var labelFalse: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_bool"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(labelTrue),
                    Condition.NE
                ),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(labelFalse),
                    Condition.EQ
                ),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val trueLabel = MessageLabelInstruction.getUniqueLabel("true\\0")
            val falseLabel = MessageLabelInstruction.getUniqueLabel("false\\0")
            representation.addDataInstr(trueLabel)
            representation.addDataInstr(falseLabel)
            labelTrue = trueLabel.getLabel()
            labelFalse = falseLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_bool
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_int(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_int"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, Registers.r0),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val printLabel = MessageLabelInstruction.getUniqueLabel("%d\\0")
            representation.addDataInstr(printLabel)
            label = printLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_int
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_string(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_string"),
                PushInstruction(Registers.lr),
                LoadInstruction(Registers.r1, AddressingMode.AddressingMode2(Registers.r0)),
                AddInstruction(Registers.r2, Registers.r0, Immediate(4)),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val printLabel = MessageLabelInstruction.getUniqueLabel("%.*s\\0")
            representation.addDataInstr(printLabel)
            label = printLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_string
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_print_reference(private val representation: AssemblyRepresentation) : PInstruction(representation) {
        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_print_reference"),
                PushInstruction(Registers.lr),
                MoveInstruction(Registers.r1, Registers.r0),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                AddInstruction(Registers.r0, Registers.r0, Immediate(4)),
                BranchInstruction("printf", Condition.L),
                MoveInstruction(Registers.r0, Immediate(0)),
                BranchInstruction("fflush", Condition.L),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val printLabel = MessageLabelInstruction.getUniqueLabel("%p\\0")
            representation.addDataInstr(printLabel)
            label = printLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            return
        }

        override fun equals(other: Any?): Boolean {
            return other is p_print_reference
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class p_check_divide_by_zero(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_check_divide_by_zero"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r1, Immediate(0)),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(label),
                    Condition.EQ
                ),
                BranchInstruction("p_throw_runtime_error", Condition.LEQ),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val divLabel =
                MessageLabelInstruction.getUniqueLabel("DivideByZeroError: divide or modulo by zero\\n\\0")
            representation.addDataInstr(divLabel)
            label = divLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_divide_by_zero
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_throw_overflow_error(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_throw_overflow_error"),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label)),
                BranchInstruction("p_throw_runtime_error", Condition.L)
            )
        }

        override fun addMessageLabel() {
            val overflowLabel = MessageLabelInstruction.getUniqueLabel(
                "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0"
            )
            representation.addDataInstr(overflowLabel)
            label = overflowLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_throw_overflow_error
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_check_array_bounds(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var labelNeg: String = ""
        private var labelLarge: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_check_array_bounds"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(labelNeg),
                    Condition.LT
                ),
                BranchInstruction("p_throw_runtime_error", Condition.LLT),
                LoadInstruction(Registers.r1, AddressingMode.AddressingMode2(Registers.r1)),
                CompareInstruction(Registers.r0, Registers.r1),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(labelLarge),
                    Condition.CS
                ),
                BranchInstruction("p_throw_runtime_error", Condition.LCS),
                PopInstruction(Registers.pc)
            )
        }

        override fun addMessageLabel() {
            val labelNegIndex =
                MessageLabelInstruction.getUniqueLabel("ArrayIndexOutOfBoundsError: negative index\\n\\0")
            val labelTooLargeIndex =
                MessageLabelInstruction.getUniqueLabel("ArrayIndexOutOfBoundsError: index too large\\n\\0")
            representation.addDataInstr(labelNegIndex)
            representation.addDataInstr(labelTooLargeIndex)
            labelNeg = labelNegIndex.getLabel()
            labelLarge = labelTooLargeIndex.getLabel()

        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_check_array_bounds
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_free_pair(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            return arrayListOf(
                LabelInstruction("p_free_pair"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                LoadInstruction(
                    Registers.r0,
                    AddressingMode.AddressingLabel(label),
                    Condition.EQ
                ),
                BranchInstruction("p_throw_runtime_error", Condition.EQ),
                PushInstruction(Registers.r0),
                LoadInstruction(Registers.r0, AddressingMode.AddressingMode2(Registers.r0)),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.r0),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.pc),
            )
        }

        override fun addMessageLabel() {
            val freeLabel = MessageLabelInstruction.getUniqueLabel(
                "NullReferenceError: dereference a null reference\\n\\0"
            )
            representation.addDataInstr(freeLabel)
            label = freeLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_free_pair
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }

    class p_free_array(private val representation: AssemblyRepresentation) : PInstruction(representation) {

        private var label: String = ""

        override fun applyIO(): ArrayList<Instruction> {
            representation.runtimeErr()
            return arrayListOf(
                LabelInstruction("p_free_array"),
                PushInstruction(Registers.lr),
                CompareInstruction(Registers.r0, Immediate(0)),
                LoadInstruction(Registers.r0, AddressingMode.AddressingLabel(label), Condition.EQ),
                BranchInstruction("p_throw_runtime_error", Condition.EQ),
                BranchInstruction("free", Condition.L),
                PopInstruction(Registers.pc),
            )
        }

        override fun addMessageLabel() {
            val freeLabel =
                MessageLabelInstruction.getUniqueLabel("NullReferenceError: dereference a null reference\\n\\0")
            representation.addDataInstr(freeLabel)
            label = freeLabel.getLabel()
        }

        override fun checkRuntimeErr() {
            representation.runtimeErr()
        }

        override fun equals(other: Any?): Boolean {
            return other is p_free_array
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

    }
}
