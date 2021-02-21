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

sealed class StatementAST : AST {

    object SkipAST : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    data class DeclAST(
        private val ctx: WaccParser.StatDeclarationContext,
        private val type: TypeAST,
        private val varName: String,
        private val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of type of identifier that is being declared
            type.check(st, errorHandler)

            val variable: IdentifierObject? = st.lookup(varName)
            if (variable != null && variable is VariableIdentifier) {
                errorHandler.repeatVariableDeclaration(ctx, varName)
            } else {
                // Check that right hand side and type of identifier match
                val typeIdent: TypeIdentifier = type.getType(st)
                assignment.check(st, errorHandler)
                val assignmentType: TypeIdentifier = assignment.getType(st)

                if (typeIdent != assignmentType && assignmentType != TypeIdentifier.GENERIC) {
                    errorHandler.typeMismatch(ctx, typeIdent, assignment.getType(st))
                }

                // Create variable identifier and add to symbol table
                val varIdent = VariableIdentifier(typeIdent)
                st.add(varName, varIdent)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    data class AssignAST(
        private val ctx: WaccParser.StatAssignContext,
        private val lhs: AssignLHSAST,
        private val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            lhs.check(st, errorHandler)
            rhs.check(st, errorHandler)

            val lhsType = lhs.getType(st)
            val rhsType = rhs.getType(st)

            if (lhsType != rhsType && lhsType != TypeIdentifier.GENERIC && rhsType != TypeIdentifier.GENERIC) {
                errorHandler.typeMismatch(ctx, lhsType, rhsType)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    data class BeginAST(private val stat: StatementAST) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            stat.check(SymbolTable(st), errorHandler)
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

    }

    data class ReadAST(private val ctx: WaccParser.StatReadContext, private val lhs: AssignLHSAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            lhs.check(st, errorHandler)

            val type = lhs.getType(st)

            if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
                errorHandler.invalidReadType(ctx, type)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    data class ExitAST(private val ctx: WaccParser.StatExitContext, private val expr: ExprAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)
            // Ensure exit is only on an integer
            if (expr.getType(st) !is TypeIdentifier.IntIdentifier) {
                errorHandler.invalidExitType(ctx, expr.getType(st))
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

    }

    data class FreeAST(private val ctx: WaccParser.StatFreeContext, private val expr: ExprAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)

            val type = expr.getType(st)

            if (!(type is TypeIdentifier.PairIdentifier || type is TypeIdentifier.ArrayIdentifier)) {
                errorHandler.invalidFreeType(ctx, type)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    // TODO: Complete translation method here
    data class IfAST(
        private val ctx: WaccParser.StatIfContext,
        private val condExpr: ExprAST,
        private val thenStat: StatementAST,
        private val elseStat: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of conditional expression
            condExpr.check(st, errorHandler)

            // Ensure that the condition expression evaluates to a boolean
            if (condExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(ctx, TypeIdentifier.BOOL_TYPE, condExpr.getType(st))
            } else {
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                val thenSt = SymbolTable(st)
                val elseSt = SymbolTable(st)
                // Propogate return type down in case there is a function that is nested
                if (returnTypeIdent != null) {
                    thenSt.add("returnType", returnTypeIdent)
                    elseSt.add("returnType", returnTypeIdent)
                }
                thenStat.check(thenSt, errorHandler)
                elseStat.check(elseSt, errorHandler)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            // Instructions for evaluating the boolean instruction
            // Compare value in dest register (of condition evaluation- how do we know that) to 0
            // Branch if equals to a label signifying else branch (which we will create later and
            // need to make sure we are not reusing a label- how do we know this)
            // Instructions for the then statement
            // Create label for else branch
            // Instructions for else branch
            return ArrayList()
        }

    }

    data class PrintAST(
        private val expr: ExprAST,
        private val newLine: Boolean
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            expr.check(st, errorHandler)
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }
    }

    data class ReturnAST(private val ctx: WaccParser.StatReturnContext, private val expr: ExprAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

            if (st.isMain()) {
                errorHandler.invalidReturn(ctx)
            }

            // Check validity of expression
            expr.check(st, errorHandler)

            // Check that type of expression being returned is the same as the return type of the function that defines the current scope
            val returnType: TypeIdentifier = expr.getType(st)
            val funcReturnType: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
            if (funcReturnType != returnType) {
                errorHandler.invalidReturnType(ctx)
            }
        }

        override fun translate(regs: Registers): ArrayList<Instruction> {
            return ArrayList()
        }

    }

    data class SequentialAST(private val stat1: StatementAST, private val stat2: StatementAST) :
        StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            stat1.check(st, errorHandler)
            stat2.check(st, errorHandler)
        }

        override fun translate(regs: Registers) : ArrayList<Instruction> {
//            val stat1Instrs: ArrayList<Instruction> = stat1.translate(regs)
//            val stat2Instrs: ArrayList<Instruction> = stat2.translate(regs)
//            val instrs: ArrayList<Instruction> = ArrayList()
//            instrs.addAll(stat1Instrs)
//            instrs.addAll(stat2Instrs)
//
//            return instrs

            return ArrayList()
        }

    }

    // TODO: Complete translation method here
    data class WhileAST(
        private val ctx: WaccParser.StatWhileContext,
        private val loopExpr: ExprAST,
        private val body: StatementAST
    ) : StatementAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            // Check validity of looping expression
            loopExpr.check(st, errorHandler)

            // Check that looping expression evaluates to a boolean
            if (loopExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(ctx, TypeIdentifier.BOOL_TYPE, loopExpr.getType(st))
            } else {
                val bodySt = SymbolTable(st)
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                if (returnTypeIdent != null) {
                    bodySt.add("returnType", returnTypeIdent)
                }
                body.check(bodySt, errorHandler)
            }
        }

        override fun translate(regs: Registers) : ArrayList<Instruction> {
            val instrs: ArrayList<Instruction> = ArrayList()

            // TODO: Change name of label below
            instrs.add(LabelInstruction("bodyCondition"))
            instrs.addAll(loopExpr.translate(regs))
            val dummyReg = Register(3)
            // TODO: Replace dummyReg with destination register for the evaluation of the loop expression
            instrs.add(CompareInstruction(dummyReg, Immediate(0)))
            // TODO: Need to include conditional branching as part of the instruction set
            // If equal, branch to label of next statement (again, need to know name of this
            // label and make sure it has not been used anywhere else).
            instrs.addAll(body.translate(regs))
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

            return ArrayList()
        }
    }
}

// Things to think about:
// Need to make sure that the label we create is not a label that has been used before
// Need a way of knowing the register where the result of some evaluation or calculation is being held
// What do we do in the cases where we branch to a label before creating that label? When we create
// the instructions underneath that label, they need to know to create that specific label instead
// of any other label.
// Thoughts:
// Need to have some way of storing meta information, like what labels have been used and where the
// destination registers are (which have values that we need)