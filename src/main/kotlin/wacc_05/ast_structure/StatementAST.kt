package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class StatementAST : AST() {

    object SkipAST : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitSkipAST(this)
        }
    }

    data class DeclAST(
        val ctx: WaccParser.StatDeclarationContext,
        val type: TypeAST,
        val varName: String,
        val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitDeclAST(this)
        }
    }

    data class AssignAST(
        val ctx: WaccParser.StatAssignContext,
        val lhs: AssignLHSAST,
        val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitAssignAST(this)
        }
    }

    data class BeginAST(val stat: StatementAST) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBeginAST(this)
        }

    }

    data class ReadAST(val ctx: WaccParser.StatReadContext, val lhs: AssignLHSAST) :
        StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReadAST(this)
        }
    }

    data class ExitAST(val ctx: WaccParser.StatExitContext, val expr: ExprAST) :
        StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitExitAST(this)
        }

    }

    data class FreeAST(val ctx: WaccParser.StatFreeContext, val expr: ExprAST) :
        StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitFreeAST(this)
        }
    }

    data class IfAST(
        val ctx: WaccParser.StatIfContext,
        val condExpr: ExprAST,
        val thenStat: StatementAST,
        val elseStat: StatementAST
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIfAST(this)
        }

    }

    data class PrintAST(
        val expr: ExprAST,
        val newLine: Boolean
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPrintAST(this)
        }
    }

    data class ReturnAST(val ctx: WaccParser.StatReturnContext, val expr: ExprAST) :
        StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReturnAST(this)
        }
    }

    data class SequentialAST(val stat1: StatementAST, val stat2: StatementAST) :
        StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitSequentialAST(this)
        }
    }

    data class WhileAST(
        val ctx: WaccParser.StatWhileContext,
        val loopExpr: ExprAST,
        val body: StatementAST
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitWhileAST(this)
        }
    }

    data class ForAST(
        val ctx: WaccParser.StatForContext,
        val decl: StatementAST,
        val loopExpr: ExprAST,
        val update: StatementAST,
        val body: StatementAST
    ) : StatementAST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitForAST(this)
        }

    }
}
