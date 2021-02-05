package wacc_05.ast_structure

import java.util.*

sealed class StatementAST : AST {

    object SkipAST : StatementAST(){

        // No semantic checks need to be done for a skip statement
        override fun check() {
            return
        }

    }

    data class DeclAST(val typeName: TypeAST,
                       val varname: String,
                       val assignment: AssignRHSAST) : StatementAST() {

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

    data class DeclarationAST(val rhs : AssignRHSAST) : StatementAST() {

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

    data class StatListAST(val statList: ArrayList<StatementAST>) : StatementAST() {

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