package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import java.util.*

sealed class StatementAST : AST {

    object SkipAST : StatementAST(){

        // No semantic checks need to be done for a skip statement
        override fun check() {
            return
        }

    }

    data class DeclAST(private val typeName: TypeAST,
                       private val varname: String,
                       private val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun check() {
//            TODO("Not yet implemented")
        }
    }

    data class AssignAST(private val lhs: AssignLHSAST, private val rhs : AssignRHSAST) : StatementAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    // Initialisation goes here

    data class ReadAST(private val lhs : AssignLHSAST) : StatementAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class BeginAST(private val stat : StatementAST) : StatementAST(){

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class ExitAST(private val expr : ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class FreeAST(private val expr : ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class IfAST(private val condExpr: ExprAST,
                     private val thenStat: StatementAST,
                     private val elseStat: StatementAST
    ) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class PrintAST(private val expr: ExprAST,
                        private val newLine: Boolean) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class ReturnAST(private val expr: ExprAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    data class SequentialAST(private val stat1: StatementAST, private val stat2: StatementAST) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

    class WhileAST(private val loopExpr: ExprAST,
                   private val body: StatementAST
    ) : StatementAST() {

        override fun check() {
//        TODO("Not yet implemented")
        }

    }

}