package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.symbol_table.identifier_objects.*
import java.sql.ParameterMetaData

sealed class ExprAST : AssignRHSAST() {

    data class IntLiterAST(private val sign: String, private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.INT_TYPE
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class BoolLiterAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.BOOL_TYPE
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class CharLiterAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.CHAR_TYPE
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class StrLiterAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.STRING_TYPE
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.PAIR_LIT_TYPE
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class IdentAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            val type = st.lookupAll(value)
            return when (type) {
                null -> {
                    TypeIdentifier.GENERIC
                }
                is ParamIdentifier -> {
                    type.getType()
                }
                else -> {
                    (type as VariableIdentifier).getType()
                }
            }
        }


//        fun setType(st: SymbolTable, type: TypeIdentifier) {
//            st.add(value, VariableIdentifier(value, type))
//        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            if (st.lookupAll(value) == null) {
                errorHandler.invalidIdentifier(value)
            }
        }
    }

    data class ArrayElemAST(
        private val ident: String,
        private val exprs: ArrayList<ExprAST>
    ) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            val type = st.lookupAll(ident)
            return if (type == null) {
                TypeIdentifier.GENERIC
            } else {
                val typeIdent = (type as VariableIdentifier).getType()
                if (typeIdent !is TypeIdentifier.ArrayIdentifier) {
                    return TypeIdentifier.GENERIC
                }
                typeIdent.getType()
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            for (expr in exprs) {
                expr.check(st, errorHandler)
                if (expr.getType(st) !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, expr.getType(st))
                }
            }

            val variable: IdentifierObject? = st.lookupAll(ident)

            if (variable == null) {
                errorHandler.invalidIdentifier(ident)
            }
            else{
                val variableType = (variable as VariableIdentifier).getType()
                if (variableType !is TypeIdentifier.ArrayIdentifier) {
                    errorHandler.typeMismatch(variableType, TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0))
                }
            }
        }

    }

    data class UnOpAST(
        private val expr: ExprAST,
        private val operator: String
    ) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
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

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

            expr.check(st, errorHandler)
            val exprType = expr.getType(st)

            when (operator) {
                "len" -> {
                    if (exprType !is TypeIdentifier.ArrayIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0), exprType)
                    }
                }
                "ord" -> {
                    if (exprType !is TypeIdentifier.CharIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.CHAR_TYPE, exprType)
                    }
                }
                "chr" -> {
                    if (exprType !is TypeIdentifier.IntIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, exprType)
                    }
                }
                "!" -> {
                    if (exprType !is TypeIdentifier.BoolIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.BOOL_TYPE, exprType)
                    }
                }
                "-" -> {
                    if (exprType !is TypeIdentifier.IntIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, exprType)
                    }
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    data class BinOpAST(
        private val expr1: ExprAST,
        private val expr2: ExprAST,
        private val operator: String
    ) : ExprAST() {

        companion object {
            val intIntFunctions = hashSetOf("*", "/", "+", "-", "%")

            val intCharFunctions = hashSetOf(">", ">=", "<", "<=")

            val anyTypeFunctions = hashSetOf("==", "!=")

            val boolBoolFunctions = hashSetOf("&&", "||")
        }

        override fun getType(st: SymbolTable): TypeIdentifier {
            return when (operator) {
                // Need valid min and max integers to put here
                "+", "%", "/", "*", "-" -> TypeIdentifier.INT_TYPE
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

            expr1.check(st, errorHandler)
            expr2.check(st, errorHandler)

            val expr1Type = expr1.getType(st)
            val expr2Type = expr2.getType(st)

            when {
                intIntFunctions.contains(operator) -> {
                    if (expr1Type !is TypeIdentifier.IntIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, expr1Type)
                    }

                    if (expr2Type !is TypeIdentifier.IntIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, expr2Type)
                    }
                }
                intCharFunctions.contains(operator) -> {
                    // if type1 is valid, check against type 2 and type1 dominates if not equal
                    // if type2 is valid and type1 is not, type mismatch on type2
                    // else type mismatch on both

                    if (expr1Type is TypeIdentifier.IntIdentifier || expr1Type is TypeIdentifier.CharIdentifier) {
                        if (expr1Type != expr2Type) {
                            errorHandler.typeMismatch(expr1Type, expr2Type)
                        }
                        return
                    }

                    if (expr2Type is TypeIdentifier.IntIdentifier || expr2Type is TypeIdentifier.CharIdentifier) {
                        // we already know type 1 isn't valid
                        errorHandler.typeMismatch(expr2Type, expr1Type)
                        return
                    }

                    // both aren't valid
                    errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, expr1Type)
                    errorHandler.typeMismatch(TypeIdentifier.INT_TYPE, expr2Type)
                }
                boolBoolFunctions.contains(operator) -> {
                    if (expr1Type !is TypeIdentifier.BoolIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.BOOL_TYPE, expr1Type)
                    }

                    if (expr2Type !is TypeIdentifier.BoolIdentifier) {
                        errorHandler.typeMismatch(TypeIdentifier.BOOL_TYPE, expr2Type)
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }
    }
}
