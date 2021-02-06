package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import java.util.*

sealed class StatementAST() : AST {

    object Skip : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            return
        }

    }

    data class DeclAST(val typeName: String,
                       val varname: String,
                       val assignment: AssignrhsAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            val typeIdent: IdentifierObject? = st.lookupAll(typeName)
            val variable: IdentifierObject? = st.lookup(varname)

            if (typeIdent == null) {
                errorHandler.invalidIdentifier(typeName)
            } else if (typeIdent !is TypeIdentifier) {
                errorHandler.invalidType(typeName)
            }

            if (variable != null) {
                errorHandler.repeatVariableDeclaration(varname)
            } else {
                val varIdent = VariableIdentifier(varname, typeIdent as TypeIdentifier)
                st.add(varname, varIdent)
            }
        }

    }

    // Initialisation goes here

    data class BeginAST(val stat : StatementAST) : StatementAST(){

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            stat.check(st, errorHandler)
        }

    }

    // Not sure about this one, will implement check method later
    data class InitAST(val rhs : AssignrhsAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            TODO("Not yet implemented")
        }

    }

    data class ExitAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class FreeAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class IfAST(val condExpr: ExprAST,
                     val thenStat: StatementAST,
                     val elseStat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            condExpr.check(st, errorHandler)
            // Making the assumption that boolean identifiers have a key of "boolean".
            // Eventually look up in top level symbol table
            val boolType: TypeIdentifier = st.lookupAll("boolean") as TypeIdentifier
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

    data class PrintAST(val expr: ExprAST,
                        val newLine: Boolean) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
        }

    }

    data class ReturnAST(val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            expr.check(st, errorHandler)
            val returnType: TypeIdentifier = expr.getType()
            // Check if the return type is the same as the return type of method you are returning from
        }

    }

    data class StatListAST(val statList: ArrayList<StatementAST>) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            for (stat in statList) {
                stat.check(st, errorHandler)
            }
        }

    }

    data class WhileAST(val loopExpr: ExprAST,
                        val body: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            loopExpr.check(st, errorHandler)
            // Would be nice to have access to top level st
            val boolType: TypeIdentifier = st.lookupAll("boolean") as TypeIdentifier
            if (boolType != loopExpr.getType()) {
                errorHandler.typeMismatch(boolType, loopExpr.getType())
            } else {
                val body_st = SymbolTable(st)
                body.check(body_st, errorHandler)
            }
        }

    }

}