package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.Immediate
import wacc_05.code_generation.Register
import wacc_05.code_generation.Registers
import wacc_05.code_generation.*
import wacc_05.code_generation.instructions.*
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

sealed class ExprAST : AssignRHSAST() {

    data class IntLiterAST(val sign: String, val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.INT_TYPE
        }

        fun getValue(): Int {
            return (sign + value).toInt()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIntLiterAST(this)
        }
    }

    data class BoolLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.BOOL_TYPE
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

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitCharLiterAST(this)
        }
    }

    data class StrLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.StringIdentifier(value.length)
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitStrLiterAST(this)
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.PAIR_LIT_TYPE
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairLiterAST(this)
        }

        fun clear() {
            clearDestReg()
            clearAST()
        }
    }

    data class IdentAST(val ctx: WaccParser.IdentContext, val value: String) : ExprAST() {

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

        fun getElemType(): TypeIdentifier {
            // get the type of the array identifier achieved from getType()
            return getType().getType()
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
            return when (operator) {
                "-" -> TypeIdentifier.INT_TYPE
                "!" -> TypeIdentifier.BOOL_TYPE
                "len" -> TypeIdentifier.INT_TYPE
                "ord" -> TypeIdentifier.INT_TYPE
                "chr" -> TypeIdentifier.CHAR_TYPE
                else -> TypeIdentifier()
            }
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

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBinOpAST(this)
        }
    }
}
