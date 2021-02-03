package wacc_05.ast_structure.statement

import wacc_05.ast_structure.AST
import wacc_05.ast_structure.AssignrhsAST
import wacc_05.ast_structure.expression.ExprAST
import java.util.*

sealed class StatementAST() : AST {

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
                val elseStat: StatementAST) : StatementAST() {

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
                   val body: StatementAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

}