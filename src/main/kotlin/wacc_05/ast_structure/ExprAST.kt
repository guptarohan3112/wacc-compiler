package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

sealed class ExprAST : AssignRHSAST() {

    data class IntLiterAST(private val sign: String, private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            // To be changed
            return TypeIdentifier.IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class BoolLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.BoolIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class CharLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.CharIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class StrLiterAST(private val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.StringIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.PairLiterIdentifier
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class IdentAST(private val value: String) : ExprAST() {

        private lateinit var type: TypeIdentifier

        override fun getType(): TypeIdentifier {
            return type
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            if (st.lookupAll(value) == null) {
                errorHandler.invalidIdentifier(value)
            } else {
                type = st.lookupAll(value) as TypeIdentifier
            }
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

            for (expr in exprs) {
                expr.check(st, errorHandler)
                if (expr.getType() !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(TypeIdentifier.IntIdentifier(), expr.getType())
                }
            }

            val variable: IdentifierObject? = st.lookupAll(ident)

            if (variable == null) {
                errorHandler.invalidIdentifier(ident)

            }
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

            expr.check(st, errorHandler)

            val exprType = expr.getType()
            val expectedType = getType()

            if (exprType != expectedType) {
                return errorHandler.typeMismatch(exprType, expectedType)
            }
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

            expr1.check(st, errorHandler)
            expr2.check(st, errorHandler)

            val expr1Type = expr1.getType()
            val expr2Type = expr2.getType()
            val expectedType = getType()

            // Check type of expressions match expected type for operator
            if (expr1Type != expectedType) {
                return errorHandler.typeMismatch(expr1Type, expectedType)
            }
            // Check type of expressions are both the same
            if (expr1Type != expr2Type) {
                return errorHandler.typeMismatch(expr1Type, expr2Type)
            }
        }
    }
}
