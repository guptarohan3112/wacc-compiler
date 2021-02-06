package wacc_05.ast_structure

import com.google.errorprone.annotations.Var
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
            TODO("Not yet implemented")
        }

    }

    data class InitAST(val rhs : AssignrhsAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class ExitAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class FreeAST(val expr : ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class IfAST(val condExpr: ExprAST,
                     val thenStat: StatementAST,
                     val elseStat: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class PrintAST(val expr: ExprAST,
                        val newLine: Boolean) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class ReturnAST(val expr: ExprAST) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class StatListAST(val statList: ArrayList<StatementAST>) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

    data class WhileAST(val loopExpr: ExprAST,
                   val body: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable) {
            TODO("Not yet implemented")
        }

    }

}