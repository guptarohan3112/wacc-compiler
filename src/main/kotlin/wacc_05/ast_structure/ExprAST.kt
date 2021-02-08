package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class ExprAST : AssignRHSAST() {

    abstract fun getType(): TypeIdentifier

    data class IntLiterAST(private val sign: String, private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            // To be changed
            return TypeIdentifier.IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            TODO("Not yet implemented")
        }
    }

    data class BoolLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.BoolIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            TODO("Not yet implemented")
        }
    }

    data class CharLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.CharIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            TODO("Not yet implemented")
        }
    }

    data class StrLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            TODO("Not yet implemented")
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            TODO("Not yet implemented")
        }
    }

    data class PairLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            TODO("Not yet implemented")
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
//            TODO("Not yet implemented")
        }
    }

    data class IdentAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            TODO("Not yet implemented")
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
//            TODO("Not yet implemented")
        }
    }

    data class ArrayElemAST(
        private val ident: String,
        private val exprs: ArrayList<ExprAST>
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.ArrayIdentifier(exprs[0].getType(), exprs.size)
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            TODO("Not yet implemented")
        }

    }

    data class UnOpAST(
        private val expr: ExprAST,
        private val UnaryOp: String
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            // Will need to get unaryOpIdentifier from st (can I if it an invalid operator) and get its return type
            return when (UnaryOp) {
                "not" -> TypeIdentifier.BoolIdentifier
                "len" -> TypeIdentifier.IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
                "ord" -> TypeIdentifier.IntIdentifier(0, 256)
                else -> TypeIdentifier.CharIdentifier
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
//            TODO("Not yet implemented")
        }

    }

    data class BinOpAST(
        private val expr1: ExprAST,
        private val expr2: ExprAST,
        private val operator: String
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (operator) {
                // Need valid min and max integers to put here
                "add", "mod", "div", "mult", "sub" -> TypeIdentifier.IntIdentifier(
                    Int.MIN_VALUE,
                    Int.MAX_VALUE
                )
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
//            TODO("Not yet implemented")
        }

    }

}