package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.*
import wacc_05.code_generation.instructions.*

sealed class StatementAST : AST() {

//    abstract fun accept()

    object SkipAST : StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

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

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitDeclAST(this)
        }
    }

    data class AssignAST(
        val ctx: WaccParser.StatAssignContext,
        val lhs: AssignLHSAST,
        val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitAssignAST(this)
        }
    }

    data class BeginAST(val stat: StatementAST) : StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBeginAST(this)
        }

    }

    data class ReadAST(val ctx: WaccParser.StatReadContext, val lhs: AssignLHSAST) :
        StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReadAST(this)
        }
    }

    data class ExitAST(val ctx: WaccParser.StatExitContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitExitAST(this)
        }

    }

    data class FreeAST(val ctx: WaccParser.StatFreeContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

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

        override fun translate(): ArrayList<Instruction> {
            // Instructions for evaluating the boolean instruction
            // Compare value in dest register to 0
            // Branch if equals to a label signifying else branch
            // Instructions for the then statement
            // Unconditional branch to label where program after if statement continues
            // Create label for else branch
            // Instructions for else branch
            // Label instruction, where the next code in the program will sit
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIfAST(this)
        }

    }

    data class PrintAST(
        val expr: ExprAST,
        val newLine: Boolean
    ) : StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPrintAST(this)
        }
    }

    data class ReturnAST(val ctx: WaccParser.StatReturnContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReturnAST(this)
        }
    }

    data class SequentialAST(val stat1: StatementAST, val stat2: StatementAST) :
        StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            val stat1Instrs: ArrayList<Instruction> = stat1.translate()
            val stat2Instrs: ArrayList<Instruction> = stat2.translate()
            val instrs: ArrayList<Instruction> = ArrayList()
            instrs.addAll(stat1Instrs)
            instrs.addAll(stat2Instrs)

            return instrs
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitSequentialAST(this)
        }
    }

    // TODO: Complete translation method here
    data class WhileAST(
        val ctx: WaccParser.StatWhileContext,
        val loopExpr: ExprAST,
        val body: StatementAST
    ) : StatementAST() {

        override fun translate(): ArrayList<Instruction> {
            val instrs: ArrayList<Instruction> = ArrayList()

            // TODO: Change name of label below
            instrs.add(LabelInstruction("bodyCondition"))
            instrs.addAll(loopExpr.translate())
            val dummyReg = Register(3)
            // TODO: Replace dummyReg with destination register for the evaluation of the loop expression
            instrs.add(CompareInstruction(dummyReg, Immediate(0)))
            // TODO: Need to include conditional branching as part of the instruction set
            // If equal, branch to label of next statement (again, need to know name of this
            // label and make sure it has not been used anywhere else).
            instrs.addAll(body.translate())
            instrs.add(BranchInstruction("bodyCondition"))

            // OR

            // Branch off to a label for the evaluation of the loop condition (need to make sure
            // that the label generated has not been used before in the program
            // Create label for body of loop
            // Instructions for body of loop
            // Create label for loop condition comparison (note that the label must be the same to
            // one that was referred to above)
            // Evaluation of loop expression
            // Compare value in some register (that the above put the output in to 1
            // If equal, branch to label above (again, need to know name of this label and make sure
            // it has not been used anywhere else)
            return instrs
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitWhileAST(this)
        }
    }

}
