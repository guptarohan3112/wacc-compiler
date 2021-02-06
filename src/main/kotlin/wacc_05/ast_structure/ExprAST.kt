package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class ExprAST() : AST {

    abstract fun getType(): TypeIdentifier

    data class UnOpAST(val expr: ExprAST,
                       val op: uOp) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (op) {
                uOp.NOT -> TypeIdentifier.BoolIdentifier
                uOp.CHR -> TypeIdentifier.CharIdentifier
                // This should be changed to have valid min and max values
                uOp.LEN -> TypeIdentifier.IntIdentifier(-1, 1)
                uOp.ORD -> TypeIdentifier.IntIdentifier(0, 256)
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
//            TODO("Not yet implemented")
        }

        enum class uOp {
            NOT, LEN, ORD, CHR // MINUS '-'
        }
    }

    data class BinOpAST(val expr1: ExprAST,
                        val expr2: ExprAST,
                        val op: bOp) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (op) {
                // Need valid min and max integers to put here
                bOp.ADD, bOp.MOD, bOp.DIV, bOp.MULT, bOp.SUB -> TypeIdentifier.IntIdentifier(-1, 1)
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
//            TODO("Not yet implemented")
        }

        enum class bOp {
            MULT, DIV, MOD, ADD, SUB, GT, GTE, LT, LTE, EQ, NEQ, AND, OR
        }

    }

}