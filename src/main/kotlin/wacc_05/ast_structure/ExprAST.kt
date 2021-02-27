package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.Immediate
import wacc_05.code_generation.Register
import wacc_05.code_generation.Registers
import wacc_05.code_generation.*
import wacc_05.code_generation.instructions.*
import wacc_05.symbol_table.identifier_objects.*

sealed class ExprAST : AssignRHSAST() {

    var dest: Register? = null

    data class IntLiterAST(val sign: String, val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.INT_TYPE
        }

        fun getValue(): Int {
            return (sign + value).toInt()
        }


        override fun translate(): ArrayList<Instruction> {
            val intValue = Integer.parseInt(sign+value)
            val register = Registers.allocate()
            val mode: AddressingMode = AddressingMode.AddressingMode2(register, Immediate(intValue))
//            this.dest = register
            return arrayListOf(LoadInstruction(register, mode))
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIntLiterAST(this)
        }
    }

    data class BoolLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.BOOL_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            val intValue = if (value == "true") 1 else 0
            return arrayListOf(MoveInstruction(Registers.allocate(), Immediate(intValue)))
        }

        fun getValue(): Int {
            return when (value) {
                "true" -> 1
                else -> 0
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBoolLiterAST(this)
        }
    }

    data class CharLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.CHAR_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            return if (value == "'\\0'") {
                arrayListOf(MoveInstruction(Registers.allocate(), Immediate(0)))
            } else {
                arrayListOf(MoveInstruction(Registers.allocate(), ImmediateChar(value)))
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitCharLiterAST(this)
        }
    }

    data class StrLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.STRING_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            // TODO: ADD LABEL FOR MSG
//            return arrayListOf(LoadInstruction(regs.allocate(), //load instruction)
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitStrLiterAST(this)
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.PAIR_LIT_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairLiterAST(this)
        }
    }

    data class IdentAST(val ctx: WaccParser.ExprContext, val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (val type = st().lookupAll(value)) {
                null -> {
                    TypeIdentifier.GENERIC
                }
                else -> {
                    type.getType()
                }
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIdentAST(this)
        }
    }

    data class ArrayElemAST(
        val ctx: WaccParser.ArrayElemContext,
        val ident: String,
        val exprs: ArrayList<ExprAST>
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            val type = st().lookupAll(ident)
            return if (type == null) {
                TypeIdentifier.GENERIC
            } else {
                val typeIdent = type.getType()
                if (typeIdent !is TypeIdentifier.ArrayIdentifier) {
                    return TypeIdentifier.GENERIC
                }
                return typeIdent.getType()
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitArrayElemAST(this)
        }
    }

    data class UnOpAST(
        val ctx: WaccParser.UnaryOperContext,
        val expr: ExprAST,
        val operator: String
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            // Will need to get unaryOpIdentifier from st (can I if it an invalid operator) and get its return type
            return when (operator) {
                "-" -> TypeIdentifier.INT_TYPE
                "!" -> TypeIdentifier.BOOL_TYPE
                "len" -> TypeIdentifier.INT_TYPE
                "ord" -> TypeIdentifier.INT_TYPE
                "chr" -> TypeIdentifier.CHAR_TYPE
                else -> TypeIdentifier()
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return when (operator) {
                "-" -> translateNeg()
                else -> ArrayList()
            }
        }

        private fun translateNeg(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            results.addAll(expr.translate())
            val dest: Register = expr.dest!!
            results.add(LoadInstruction(dest, AddressingMode.AddressingMode2(Registers.sp, null)))
            results.add(ReverseSubtractInstruction(dest, dest, Immediate(0)))
            return results
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitUnOpAST(this)
        }
    }

    data class BinOpAST(
        val ctx: WaccParser.ExprContext,
        val expr1: ExprAST,
        val expr2: ExprAST,
        val operator: String
    ) : ExprAST() {

        companion object {
            val intIntFunctions = hashSetOf("*", "/", "+", "-", "%")

            val intCharFunctions = hashSetOf(">", ">=", "<", "<=")

            val boolBoolFunctions = hashSetOf("&&", "||")
        }

        override fun getType(): TypeIdentifier {
            return when (operator) {
                // Need valid min and max integers to put here
                "+", "%", "/", "*", "-" -> TypeIdentifier.INT_TYPE
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return when (operator) {
//                "+" -> translateAdd(regs)
//                "-" -> translateSub(regs)
//                "*" -> translateMultiply(regs)
//                "/" -> translateDivide(regs)
//                "%" -> translateModulo(regs)
//                "&&" -> translateAnd(regs)
//                "||" -> translateOr(regs)
                else -> ArrayList()
            }
        }

        private fun translateMultiply(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            /* here we can't do any optimisation on registers since ARM
             * requires MULT rd, rm, rs -> rd = rm * rs
             */

            results.addAll(expr1.translate())
            results.addAll(expr2.translate())

            val dest1: Register = expr1.dest!!
            val dest2: Register = expr2.dest!!

            results.add(MultiplyInstruction(dest1, dest1, dest2))

            Registers.free(dest2)
            this.dest = dest1

            return results
        }

        // NOTE: use caller save when calling translateDivide() ??
        private fun translateDivide(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            results.addAll(expr1.translate())
            results.addAll(expr2.translate())

            val dest1: Register = expr1.dest!!
            val dest2: Register = expr2.dest!!

            if (dest1 != Registers.r0) {
                results.add(MoveInstruction(Registers.r0, dest1))
                Registers.free(dest1)
            }

            if (dest2 != Registers.r1) {
                results.add(MoveInstruction(Registers.r1, dest2))
                Registers.free(dest2)
            }

            results.add(BranchInstruction("__aeabi_idiv", Condition.L))

            // not sure about this - we need to move R3 into a destination register
            // but I am concerned about overwriting it when restoring, or overwriting
            // a register we are restoring

            return results
        }

        private fun translateModulo(): ArrayList<Instruction> {
            // to follow translateDivide()
            return ArrayList()
        }

        private fun translateAnd(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            when {
                expr1 is BoolLiterAST -> {
                    results.addAll(expr2.translate())
                    val dest: Register = expr2.dest!!
                    results.add(AndInstruction(dest, dest, Immediate(expr1.getValue())))

                    this.dest = dest
                }
                expr2 is BoolLiterAST -> {
                    results.addAll(expr1.translate())
                    val dest: Register = expr1.dest!!
                    results.add(AndInstruction(dest, dest, Immediate(expr2.getValue())))

                    this.dest = dest
                }
                else -> {
                    results.addAll(expr1.translate())
                    results.addAll(expr2.translate())
                    val dest1: Register = expr1.dest!!
                    val dest2: Register = expr2.dest!!

                    results.add(AndInstruction(dest1, dest1, dest2))
                    Registers.free(dest2)

                    this.dest = dest1
                }
            }

            return results
        }

        private fun translateOr(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            when {
                expr1 is BoolLiterAST -> {
                    results.addAll(expr2.translate())
                    val dest: Register = expr2.dest!!
                    results.add(OrInstruction(dest, dest, Immediate(expr1.getValue())))

                    this.dest = dest
                }
                expr2 is BoolLiterAST -> {
                    results.addAll(expr1.translate())
                    val dest: Register = expr1.dest!!
                    results.add(OrInstruction(dest, dest, Immediate(expr2.getValue())))

                    this.dest = dest
                }
                else -> {
                    results.addAll(expr1.translate())
                    results.addAll(expr2.translate())
                    val dest1: Register = expr1.dest!!
                    val dest2: Register = expr2.dest!!

                    results.add(OrInstruction(dest1, dest1, dest2))
                    Registers.free(dest2)

                    this.dest = dest1
                }
            }

            return results
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBinOpAST(this)
        }
    }
}
