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
                type = (st.lookupAll(value) as VariableIdentifier).getType()
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
        private val op: String
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            // Will need to get unaryOpIdentifier from st (can I if it an invalid operator) and get its return type
            return when (op) {
                "!" -> TypeIdentifier.BoolIdentifier
//                "len" -> TypeIdentifier.IntIdentifier()
                "ord" -> TypeIdentifier.IntIdentifier(0, 256)
                "chr" -> TypeIdentifier.CharIdentifier
                else -> TypeIdentifier.IntIdentifier()
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

            expr.check(st, errorHandler)

            val exprType = expr.getType()
            val returnType = getType()

            if (op == "len") {
                if (exprType !is TypeIdentifier.ArrayIdentifier) {
                    val mockArrayObject = TypeIdentifier.ArrayIdentifier(TypeIdentifier.IntIdentifier(), 0)
                    return errorHandler.typeMismatch(mockArrayObject, exprType)
                }
            }

            else if (op == "ord") {
                if (exprType !is TypeIdentifier.CharIdentifier) {
                    return errorHandler.typeMismatch(TypeIdentifier.CharIdentifier, exprType)
                }
            }

            else if (op == "chr") {
                if (exprType !is TypeIdentifier.IntIdentifier) {
                    return errorHandler.typeMismatch(TypeIdentifier.IntIdentifier(), exprType)
                }
            }

            // For all other expressions, both expressions must have the same type
            else if (exprType != returnType) {
                return errorHandler.typeMismatch(returnType, exprType)
            }

        }
    }

    data class BinOpAST(
        private val expr1: ExprAST,
        private val expr2: ExprAST,
        private val op: String
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (op) {
                // Need valid min and max integers to put here
                "+", "%", "/", "*", "-" -> TypeIdentifier.IntIdentifier(
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

            // If equality expression, no checks
            if (op == "==" || op == "!=") {
                return
            }

            // For all other expressions, both expressions must have the same type
            if (expr1Type != expr2Type) {
                return errorHandler.typeMismatch(expr1Type, expr2Type)
            }

            // If arithmetic expression, check both expressions have int types
            if (expectedType is TypeIdentifier.IntIdentifier) {
                if (expr1Type !is TypeIdentifier.IntIdentifier) {
                    return errorHandler.typeMismatch(expr1Type, expr2Type)
                }
            }

            // If comparison expression, check both expressions have int or char types
            if (op == ">" || op == ">=" || op == "<" || op == "<=") {
                if (expr1Type !is TypeIdentifier.IntIdentifier && expr1Type !is TypeIdentifier.CharIdentifier){
                    return errorHandler.typeMismatch(expr1Type, expr2Type)
                }
            }

            // If logical expression, check both expressions have bool types
            if (op == "&&" || op == "||"){
                if (expr1Type !is TypeIdentifier.BoolIdentifier){
                    return errorHandler.typeMismatch(expr1Type, expr2Type)
                }
            }

        }
    }
}
