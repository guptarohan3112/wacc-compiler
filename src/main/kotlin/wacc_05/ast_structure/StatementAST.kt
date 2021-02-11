package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class StatementAST : AST {

    object SkipAST : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class DeclAST(
        private val type: TypeAST,
        private val varName: String,
        private val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of type of identifier that is being declared
            type.check(st, errorHandler)

            val variable: IdentifierObject? = st.lookup(varName)
            if (variable != null && variable is VariableIdentifier) {
                errorHandler.repeatVariableDeclaration(varName)
            } else {
                // Check that right hand side and type of identifier match
                val typeIdent: TypeIdentifier = type.getType(st)
                assignment.check(st, errorHandler)
                val assignmentType: TypeIdentifier = assignment.getType(st)

                if (typeIdent != assignmentType && assignmentType != TypeIdentifier.GENERIC) {
                    errorHandler.typeMismatch(typeIdent, assignment.getType(st))
                }
                // Create variable identifier and add to symbol table
                val varIdent = VariableIdentifier(varName, typeIdent)
                st.add(varName, varIdent)
            }
        }
    }

    data class AssignAST(
        private val lhs: AssignLHSAST,
        private val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            lhs.check(st, errorHandler)
            rhs.check(st, errorHandler)

            val lhsType = lhs.getType(st)
            val rhsType = rhs.getType(st)

            if (lhsType != rhsType && lhsType != TypeIdentifier.GENERIC && rhsType != TypeIdentifier.GENERIC) {
                errorHandler.typeMismatch(lhsType, rhsType)
            }

//            if(lhsType != TypeIdentifier.GENERIC) {
//                if(rhsType == TypeIdentifier.GENERIC) {
//                    (rhs as ExprAST.IdentAST).setType(st, lhsType)
//                }
//            }
//
//            if (rhsType != TypeIdentifier.GENERIC) {
//                if (lhsType == TypeIdentifier.GENERIC) {
//                    // we've had a semantic error where lhs is not defined
//                    // set lhs to type to rhs and continue
//                    lhs.setType(st, rhsType)
//                } else {
//                    // Check that both sides match up in their types
//                    if (lhsType != rhsType) {
//                        errorHandler.typeMismatch(lhs.getType(st), rhs.getType(st))
//                    }
//                }
//            }
        }
    }

    data class BeginAST(private val stat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            stat.check(SymbolTable(st), errorHandler)
        }

    }

    data class ReadAST(private val lhs: AssignLHSAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            lhs.check(st, errorHandler)

            val type = lhs.getType(st)

            if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
                errorHandler.invalidReadType(type)
            }
        }
    }

    data class ExitAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)
            // Ensure exit is only on an integer
            if (expr.getType(st) !is TypeIdentifier.IntIdentifier) {
                errorHandler.invalidExitType(expr.getType(st))
            }
        }

    }

    data class FreeAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)

            val type = expr.getType(st)

            if (!(type is TypeIdentifier.PairIdentifier || type is TypeIdentifier.ArrayIdentifier)) {
                errorHandler.invalidFreeType(type)
            }
        }
    }

    data class IfAST(
        private val condExpr: ExprAST,
        private val thenStat: StatementAST,
        private val elseStat: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of conditional expression
            condExpr.check(st, errorHandler)

            // Ensure that the condition expression evaluates to a boolean
            if (condExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(TypeIdentifier.BOOL_TYPE, condExpr.getType(st))
            } else {
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                val thenSt = SymbolTable(st)
                val elseSt = SymbolTable(st)
                // Propogate return type down in case there is a function that is nested
                if (returnTypeIdent != null) {
                    thenSt.add("returnType", returnTypeIdent)
                    elseSt.add("returnType", returnTypeIdent)
                }
                thenStat.check(thenSt, errorHandler)
                elseStat.check(elseSt, errorHandler)
            }
        }

    }

    data class PrintAST(
        private val expr: ExprAST,
        private val newLine: Boolean
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)
        }

    }

    data class ReturnAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

            if (st.isMain()) {
                errorHandler.invalidReturn()
            }

            // Check validity of expression
            expr.check(st, errorHandler)

            // Check that type of expression being returned is the same as the return type of the function that defines the current scope
            val returnType: TypeIdentifier = expr.getType(st)
            val funcReturnType: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
            if (funcReturnType != returnType) {
                errorHandler.invalidReturnType()
            }
        }

    }

    data class SequentialAST(private val stat1: StatementAST, private val stat2: StatementAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            stat1.check(st, errorHandler)
            stat2.check(st, errorHandler)
        }

    }

    data class WhileAST(
        private val loopExpr: ExprAST,
        private val body: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of looping expression
            loopExpr.check(st, errorHandler)

            // Check that looping expression evaluates to a boolean
            if (loopExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(TypeIdentifier.BOOL_TYPE, loopExpr.getType(st))
            } else {
                val bodySt = SymbolTable(st)
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                if (returnTypeIdent != null) {
                    bodySt.add("returnType", returnTypeIdent)
                }
                body.check(bodySt, errorHandler)
            }
        }
    }
}