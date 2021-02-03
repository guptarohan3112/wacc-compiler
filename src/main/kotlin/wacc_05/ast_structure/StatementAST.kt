package wacc_05.ast_structure

import wacc_05.ast_structure.expression.ExprAST
import java.util.*

sealed class StatementAST() : AST {

    object Skip : StatementAST(){

        // No semantic checks need to be done for a skip statement
        override fun check() {
            return
        }

    }

    data class DeclAST(val typeName: String,
                       val varname: String,
                       val assignment: AssignrhsAST) : StatementAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }

    }

    // Initialisation goes here

    data class BeginAST(val stat : StatementAST) : StatementAST(){

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class DeclarationAST(val rhs : AssignrhsAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class ExitAST(val expr : ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class FreeAST(val expr : ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class IfAST(val condExpr: ExprAST,
                     val thenStat: StatementAST,
                     val elseStat: StatementAST
    ) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class PrintAST(val expr: ExprAST,
                   val newLine: Boolean) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class ReturnAST(val expr: ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class StatementList(val statList: ArrayList<StatementAST>) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    class WhileAST(val loopExpr: ExprAST,
                   val body: StatementAST
    ) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

}