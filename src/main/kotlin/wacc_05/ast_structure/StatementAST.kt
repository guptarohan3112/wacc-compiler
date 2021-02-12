package wacc_05.ast_structure

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST

sealed class StatementAST : AST {

    object SkipAST : StatementAST() {
        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            return
        }
    }

    data class DeclAST(
        private val type: TypeAST,
        private val varName: String,
        private val assignment: AssignRHSAST
    ) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val declContext = ctx as WaccParser.StatDeclarationContext

            // Check validity of type of identifier that is being declared
            type.check(declContext.type(), st, errorHandler)

            val variable: IdentifierObject? = st.lookup(varName)
            if (variable != null && variable is VariableIdentifier) {
                errorHandler.repeatVariableDeclaration(declContext, varName)
            } else {
                // Check that right hand side and type of identifier match
                val typeIdent: TypeIdentifier = type.getType(st)
                assignment.check(declContext.assignRHS(), st, errorHandler)
                val assignmentType: TypeIdentifier = assignment.getType(st)

                if (typeIdent != assignmentType && assignmentType != TypeIdentifier.GENERIC) {
                    errorHandler.typeMismatch(declContext, typeIdent, assignment.getType(st))
                }

                // Create variable identifier and add to symbol table
                val varIdent = VariableIdentifier(typeIdent)
                st.add(varName, varIdent)
            }
        }
    }

    data class AssignAST(
        private val lhs: AssignLHSAST,
        private val rhs: AssignRHSAST
    ) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val assignContext = ctx as WaccParser.StatAssignContext

            lhs.check(assignContext.assignLHS(), st, errorHandler)
            rhs.check(assignContext.assignRHS(), st, errorHandler)

            val lhsType = lhs.getType(st)
            val rhsType = rhs.getType(st)

            if (lhsType != rhsType && lhsType != TypeIdentifier.GENERIC && rhsType != TypeIdentifier.GENERIC) {
                errorHandler.typeMismatch(assignContext, lhsType, rhsType)
            }
        }
    }

    data class BeginAST(private val stat: StatementAST) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            stat.check((ctx as WaccParser.StatBeginEndContext).stat(), SymbolTable(st), errorHandler)
        }

    }

    data class ReadAST(private val lhs: AssignLHSAST) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val readContext = ctx as WaccParser.StatReadContext

            lhs.check(readContext.assignLHS(), st, errorHandler)

            val type = lhs.getType(st)

            if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
                errorHandler.invalidReadType(readContext, type)
            }
        }
    }

    data class ExitAST(private val expr: ExprAST) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val exitContext = ctx as WaccParser.StatExitContext

            expr.check(exitContext.expr(), st, errorHandler)
            // Ensure exit is only on an integer
            if (expr.getType(st) !is TypeIdentifier.IntIdentifier) {
                errorHandler.invalidExitType(exitContext, expr.getType(st))
            }
        }

    }

    data class FreeAST(private val expr: ExprAST) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val freeContext = ctx as WaccParser.StatFreeContext

            expr.check(freeContext.expr(), st, errorHandler)

            val type = expr.getType(st)

            if (!(type is TypeIdentifier.PairIdentifier || type is TypeIdentifier.ArrayIdentifier)) {
                errorHandler.invalidFreeType(freeContext, type)
            }
        }
    }

    data class IfAST(
        private val condExpr: ExprAST,
        private val thenStat: StatementAST,
        private val elseStat: StatementAST
    ) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val ifContext = ctx as WaccParser.StatIfContext

            // Check validity of conditional expression
            condExpr.check(ctx.expr(), st, errorHandler)

            // Ensure that the condition expression evaluates to a boolean
            if (condExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(ifContext, TypeIdentifier.BOOL_TYPE, condExpr.getType(st))
            } else {
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                val thenSt = SymbolTable(st)
                val elseSt = SymbolTable(st)
                // Propogate return type down in case there is a function that is nested
                if (returnTypeIdent != null) {
                    thenSt.add("returnType", returnTypeIdent)
                    elseSt.add("returnType", returnTypeIdent)
                }
                thenStat.check(ifContext.stat(0), thenSt, errorHandler)
                elseStat.check(ifContext.stat(1), elseSt, errorHandler)
            }
        }

    }

    data class PrintAST(
        private val expr: ExprAST,
        private val newLine: Boolean
    ) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            if (ctx is WaccParser.StatPrintContext) {
                expr.check(ctx.expr(), st, errorHandler)
            } else {
                expr.check((ctx as WaccParser.StatPrintlnContext).expr(), st, errorHandler)
            }
        }

    }

    data class ReturnAST(private val expr: ExprAST) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val returnContext = ctx as WaccParser.StatReturnContext

            if (st.isMain()) {
                errorHandler.invalidReturn(returnContext)
            }

            // Check validity of expression
            expr.check(returnContext.expr(), st, errorHandler)

            // Check that type of expression being returned is the same as the return type of the function that defines the current scope
            val returnType: TypeIdentifier = expr.getType(st)
            val funcReturnType: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
            if (funcReturnType != returnType) {
                errorHandler.invalidReturnType(returnContext)
            }
        }

    }

    data class SequentialAST(private val stat1: StatementAST, private val stat2: StatementAST) :
        StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val seqContext = ctx as WaccParser.StatSequentialContext

            stat1.check(seqContext.stat(0), st, errorHandler)
            stat2.check(seqContext.stat(1), st, errorHandler)
        }

    }

    data class WhileAST(
        private val loopExpr: ExprAST,
        private val body: StatementAST
    ) : StatementAST() {

        override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
            val whileContext = ctx as WaccParser.StatWhileContext

            // Check validity of looping expression
            loopExpr.check(whileContext.expr(), st, errorHandler)

            // Check that looping expression evaluates to a boolean
            if (loopExpr.getType(st) != TypeIdentifier.BOOL_TYPE) {
                errorHandler.typeMismatch(whileContext, TypeIdentifier.BOOL_TYPE, loopExpr.getType(st))
            } else {
                val bodySt = SymbolTable(st)
                val returnTypeIdent: TypeIdentifier? = st.lookup("returnType") as TypeIdentifier?
                if (returnTypeIdent != null) {
                    bodySt.add("returnType", returnTypeIdent)
                }
                body.check(whileContext.stat(), bodySt, errorHandler)
            }
        }
    }
}