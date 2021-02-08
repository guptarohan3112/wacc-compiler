package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class StatementAST : AST {

    object SkipAST : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            return
        }

    }

    data class DeclAST(
        private val typeName: TypeAST,
        private val varName: String,
        private val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            val typeIdent: IdentifierObject? = st.lookupAll(typeName.toString())
            val variable: IdentifierObject? = st.lookup(varName)

            if (typeIdent == null) {
                errorHandler.invalidIdentifier(typeName.toString())
            } else if (typeIdent !is TypeIdentifier) {
                errorHandler.invalidType(typeName.toString())
            }

            if (variable != null) {
                errorHandler.repeatVariableDeclaration(varName)
            } else {
                val varIdent = VariableIdentifier(varName, typeIdent as TypeIdentifier)
                st.add(varName, varIdent)
            }
        }
    }

    data class AssignAST(
        private val lhs: AssignLHSAST,
        private val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            lhs.check(st, errorHandler)
            rhs.check(st, errorHandler)
        }

    }

    data class BeginAST(private val stat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            stat.check(st, errorHandler)
        }

    }

    data class ReadAST(private val lhs: AssignLHSAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            lhs.check(st, errorHandler)
        }

    }

    data class ExitAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class FreeAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class IfAST(
        private val condExpr: ExprAST,
        private val thenStat: StatementAST,
        private val elseStat: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            condExpr.check(st, errorHandler)
            // Making the assumption that boolean identifiers have a key of "boolean".
            // Eventually look up in top level symbol table
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

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class ReturnAST(private val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
            val returnType: TypeIdentifier = expr.getType()
            // the value below is guaranteed to not be null due to the nature of returnType.toString()
            val funcReturnType: TypeIdentifier? =
                st.lookup(returnType.toString()) as TypeIdentifier?
            if (funcReturnType == null) {
                errorHandler.invalidReturnType()
            }
        }

    }

    data class SequentialAST(private val stat1: StatementAST, private val stat2: StatementAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            stat1.check(st, errorHandler)
            stat2.check(st, errorHandler)
        }

    }

    data class WhileAST(
        private val loopExpr: ExprAST,
        private val body: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            loopExpr.check(st, errorHandler)
            // Would be nice to have access to top level st
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