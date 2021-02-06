package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import java.util.*

sealed class StatementAST() : AST {

    object Skip : StatementAST(){

        override fun check(st: SymbolTable) {
            return
        }

    }

    data class DeclAST(val typeName: String,
                       val varname: String,
                       val assignment: AssignrhsAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            val typeIdent: IdentifierObject? = st.lookupAll(typeName)
            val variable: IdentifierObject? = st.lookup(varname)

            if (typeIdent == null) {
                // Error here- type of the variable is not a valid identifier of the WACC language
            } else if (typeIdent !is TypeIdentifier) {
                // Error here- type of the variable is not a valid type identifier
            }

            if (variable != null) {
                //Error here- variable has already been declared previously in the program
            } else {
                val varIdent = VariableIdentifier(varname, typeIdent as TypeIdentifier)
                st.add(varname, varIdent)
            }
        }

    }

    // Initialisation goes here

    data class BeginAST(val stat : StatementAST) : StatementAST(){

        override fun check(st: SymbolTable) {
            stat.check(st)
        }

    }

    // Not sure about this one, will implement check method later
    data class InitAST(val rhs : AssignrhsAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class ExitAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            expr.check(st)
        }

    }

    data class FreeAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            expr.check(st)
        }

    }

    data class IfAST(val condExpr: ExprAST,
                     val thenStat: StatementAST,
                     val elseStat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            condExpr.check(st)
            // Making the assumption that boolean identifiers have a key of "boolean".
            // Eventually look up in top level symbol table
            val boolType: TypeIdentifier = st.lookupAll("boolean") as TypeIdentifier
            if (condExpr.getType() != boolType) {
                //Error- not allowed to have condition expression that does not evaluate to a boolean
            } else {
                val then_st = SymbolTable(st)
                val else_st = SymbolTable(st)
                thenStat.check(then_st)
                elseStat.check(else_st)
            }
        }

    }

    data class PrintAST(val expr: ExprAST,
                        val newLine: Boolean) : StatementAST() {

        override fun check(st: SymbolTable) {
            expr.check(st)
        }

    }

    data class ReturnAST(val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            expr.check(st)
            val returnType: TypeIdentifier = expr.getType()
            // Check if the return type is the same as the return type of method you are returning from
        }

    }

    data class StatListAST(val statList: ArrayList<StatementAST>) : StatementAST() {

        override fun check(st: SymbolTable) {
            for (stat in statList) {
                stat.check(st)
            }
        }

    }

    data class WhileAST(val loopExpr: ExprAST,
                        val body: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            loopExpr.check(st)
            // Would be nice to have access to top level st
            val boolType: TypeIdentifier = st.lookupAll("boolean") as TypeIdentifier
            if (boolType != loopExpr.getType()) {
                // Error: loop expression does not evaluate to a boolean
            } else {
                val body_st = SymbolTable(st)
                body.check(body_st)
            }
        }

    }

}