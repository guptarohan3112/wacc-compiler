package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.Immediate
import wacc_05.code_generation.Register
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.BranchInstruction
import wacc_05.code_generation.instructions.CompareInstruction
import wacc_05.code_generation.instructions.Instruction
import wacc_05.code_generation.instructions.LabelInstruction
import wacc_05.front_end.ASTVisitor

sealed class StatementAST : AST() {

//    abstract fun accept()

    object SkipAST : StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
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

        override fun translate(regs: Registers): ArrayList<Instruction> {
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

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitAssignAST(this)
        }
    }

    data class BeginAST(val stat: StatementAST) : StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBeginAST(this)
        }

    }

    data class ReadAST(val ctx: WaccParser.StatReadContext, val lhs: AssignLHSAST) :
        StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReadAST(this)
        }
    }

    data class ExitAST(val ctx: WaccParser.StatExitContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitExitAST(this)
        }

    }

    data class FreeAST(val ctx: WaccParser.StatFreeContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
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

        override fun translate(regs: Registers): ArrayList<Instruction> {
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

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPrintAST(this)
        }
    }

    data class ReturnAST(val ctx: WaccParser.StatReturnContext, val expr: ExprAST) :
        StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitReturnAST(this)
        }
    }

    data class SequentialAST(val stat1: StatementAST, val stat2: StatementAST) :
        StatementAST() {

        override fun translate(regs: Registers): ArrayList<Instruction> {
            val stat1Instrs: ArrayList<Instruction> = stat1.translate(regs)
            val stat2Instrs: ArrayList<Instruction> = stat2.translate(regs)
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

        override fun translate(regs: Registers): ArrayList<Instruction> {
            val instrs: ArrayList<Instruction> = ArrayList()

            // TODO: Change name of label below
            instrs.add(LabelInstruction("bodyCondition"))
//            instrs.add(loopExpr.translate(regs))
            val dummyReg = Register(3)
            // TODO: Replace dummyReg with destination register for the evaluation of the loop expression
            instrs.add(CompareInstruction(dummyReg, Immediate(0)))
            // TODO: Need to include conditional branching as part of the instruction set
            // If equal, branch to label of next statement (again, need to know name of this
            // label and make sure it has not been used anywhere else).
//            instrs.addAll(body.translate(regs))
            return instrs
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitWhileAST(this)
        }
    }
}