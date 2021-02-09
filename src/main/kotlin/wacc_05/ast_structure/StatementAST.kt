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
            if (variable != null) {
                errorHandler.repeatVariableDeclaration(varName)
            } else {
                // Check that right hand side and type of identifier match
                val typeIdent: TypeIdentifier = st.lookupAll(type.toString()) as TypeIdentifier
                assignment.check(st, errorHandler)
                if (typeIdent != assignment.getType()) {
                    errorHandler.typeMismatch(typeIdent, assignment.getType())
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
            // Check that both sides match up in their types
            if (lhs.getType() != rhs.getType()) {
                errorHandler.typeMismatch(lhs.getType(), rhs.getType())
            }
        }

    }

    data class BeginAST(private val stat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            stat.check(st, errorHandler)
        }

    }

    data class ReadAST(private val lhs: AssignLHSAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            lhs.check(st, errorHandler)

            val type = lhs.getType()

            if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
                errorHandler.invalidReadType(type)
            }
        }
    }

    data class ExitAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)
            // Ensure exit is only on an integer
            if (expr.getType() !is TypeIdentifier.IntIdentifier) {
                errorHandler.invalidExitType(expr.getType())
            }
        }

    }

    data class FreeAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)

            val type = expr.getType()

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
            val boolType: TypeIdentifier = TypeIdentifier.BoolIdentifier
            if (condExpr.getType() != boolType) {
                errorHandler.typeMismatch(boolType, condExpr.getType())
            } else {
                val then_st = SymbolTable(st)
                val else_st = SymbolTable(st)
                thenStat.check(then_st, errorHandler)
                elseStat.check(else_st, errorHandler)
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
            // Check validity of expression
            expr.check(st, errorHandler)

            // Check that type of expression being returned is the same as the return type of the function that defines the current scope
            val returnType: TypeIdentifier = expr.getType()
            val funcReturnType: TypeIdentifier? =
                st.lookup(returnType.toString()) as TypeIdentifier?
            if (funcReturnType == null) {
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
            val boolType: TypeIdentifier = TypeIdentifier.BoolIdentifier
            if (boolType != loopExpr.getType()) {
                errorHandler.typeMismatch(boolType, loopExpr.getType())
            } else {
                val body_st = SymbolTable(st)
                body.check(body_st, errorHandler)
            }
        }
    }
}