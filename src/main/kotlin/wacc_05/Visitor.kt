package wacc_05

import antlr.WaccParser
import antlr.WaccParserBaseVisitor
import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*

class Visitor : WaccParserBaseVisitor<AST>() {

    /* Function: visitStatSkip()
    ----------------------------
        Generates a SkipAST node without referencing the context as the context is not required.
     */
    override fun visitStatSkip(ctx: WaccParser.StatSkipContext): StatementAST {
        return StatementAST.SkipAST
    }

    /* Function: visitStatDeclaration()
        -------------------------------
        Generates a DeclAST node using the type, IDENT and assignRHS from the context and relevant visitX() calls
        where necessary
     */
    override fun visitStatDeclaration(ctx: WaccParser.StatDeclarationContext): StatementAST {
        return StatementAST.DeclAST(visitType(ctx.type()), ctx.IDENT().text, visitAssignRHS(ctx.assignRHS()))
    }

    /* Function: visitStatAssign()
       ----------------------------
       Generates an AssignAST node using the assignLHS and assignRHS from the context and relevant visitX() calls
       where necessary.
     */
    override fun visitStatAssign(ctx: WaccParser.StatAssignContext): StatementAST {
        return StatementAST.AssignAST(visitAssignLHS(ctx.assignLHS()), visitAssignRHS(ctx.assignRHS()))
    }

    /* Function: visitStatRead()
       -------------------------
       Generates a ReadAST node with the child being the AssignLHSAST resulting from calling visitAssignLHS on the
       context's assignLHS child.
     */
    override fun visitStatRead(ctx: WaccParser.StatReadContext): StatementAST {
        return StatementAST.ReadAST(visitAssignLHS(ctx.assignLHS()))
    }

    /* Function: visitStatFree()
       -------------------------
       Generates a FreeAST node with the child being the ExprAST resulting from calling visitExpr() on the
       context's expr child.
     */
    override fun visitStatFree(ctx: WaccParser.StatFreeContext): StatementAST {
        return StatementAST.FreeAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatReturn()
       -------------------------
       Generates a ReturnAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatReturn(ctx: WaccParser.StatReturnContext): StatementAST {
        return StatementAST.ReturnAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatExit()
       -------------------------
       Generates an ExitAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatExit(ctx: WaccParser.StatExitContext): StatementAST {
        return StatementAST.ExitAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatPrint()
       -------------------------
       Generates a PrintAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatPrint(ctx: WaccParser.StatPrintContext): StatementAST {
        return StatementAST.PrintAST(visitExpr(ctx.expr()), false)
    }

    /* Function: visitStatPrintln()
       -------------------------
       Generates a PrintAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatPrintln(ctx: WaccParser.StatPrintlnContext): StatementAST {
        return StatementAST.PrintAST(visitExpr(ctx.expr()), true)
    }

    /* Function: visitAssignRHS()
       --------------------------
       Returns an AssignRHSAST node, by matching the context with each possible type of assignRHS
       or throws an error if this fails
     */
    override fun visitAssignRHS(ctx: WaccParser.AssignRHSContext): AssignRHSAST {
        // we use the when statement to check which type of assignRHS we have and generate a child AST
        // node accordingly
        return when {
            ctx.expr() != null -> {
                visitExpr(ctx.expr())
            }
            ctx.arrayLit() != null -> {
                visitArrayLit(ctx.arrayLit())
            }
            ctx.newPair() != null -> {
                visitNewPair(ctx.newPair())
            }
            ctx.pairElem() != null -> {
                visitPairElem(ctx.pairElem())
            }
            ctx.funcCall() != null -> {
                visitFuncCall(ctx.funcCall())
            }

            // TODO - throw suitable error
            else -> throw Exception()
        }
    }

    /* Function: visitAssignLHS()
        -------------------------
        Generates an AssignLHSAST node by matching the context with each possible type of assignLHS, or
        throws an error if this fails.
     */
    override fun visitAssignLHS(ctx: WaccParser.AssignLHSContext): AssignLHSAST {
        // we use the when statement to check which type of assignLHS we have and generate a child AST
        // node accordingly
        return when {
            ctx.IDENT() != null -> {
                AssignLHSAST(ident = ctx.IDENT().text)
            }
            ctx.pairElem() != null -> {
                AssignLHSAST(pairElem = visitPairElem(ctx.pairElem()))
            }
            ctx.arrayElem() != null -> {
                AssignLHSAST(arrElem = visitArrayElem(ctx.arrayElem()))
            }

            // TODO - throw suitable error
            else -> throw Exception()
        }

    }

    override fun visitType(ctx: WaccParser.TypeContext): TypeAST {
        // TODO - return purely for compilation purposes
        return TypeAST.BaseTypeAST("")
    }

    override fun visitArrayLit(ctx: WaccParser.ArrayLitContext): ArrayLiterAST {
        // TODO - return purely for compilation purposes
        return ArrayLiterAST(ArrayList())
    }

    /* Function: visitNewPair()
       ------------------------
       Returns a NewPairAST node, by matching the context with each of the two expr children. It assumes these
       two children exist.
     */
    override fun visitNewPair(ctx: WaccParser.NewPairContext): NewPairAST {
        return NewPairAST(visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)))
    }

    /* Function: visitPairElem()
        -------------------------
        Generates a PairElemAST, which has expr child generated by calling visitExpr on the context's expr child.
     */
    override fun visitPairElem(ctx: WaccParser.PairElemContext): PairElemAST {
        return PairElemAST(visitExpr(ctx.expr()))
    }

    override fun visitArrayElem(ctx: WaccParser.ArrayElemContext): ArrayElemAST {
        // TODO - return purely for compilation purposes
        return ArrayElemAST("", ArrayList())
    }

    /* Function: VisitFuncCall()
        ------------------------
        Generated a FuncCallAST with function name following ctx's IDENT token, and the args are either null (ctx's
        argsList is null) or generated by calling visitArgList.
     */
    override fun visitFuncCall(ctx: WaccParser.FuncCallContext): FuncCallAST {
        val argList = if (ctx.argList() == null) {
            null
        } else {
            visitArgList(ctx.argList())
        }
        return FuncCallAST(ctx.IDENT().text, argList)
    }

    /* Function: visitArgList()
        -----------------------
        Generates an ArgListAST for the 1(+) args (expressions) in the context by calling visitExpr() on these.
     */
    override fun visitArgList(ctx: WaccParser.ArgListContext): ArgListAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ArgListAST(exprs as ArrayList<ExprAST>)
    }

    override fun visitExpr(ctx: WaccParser.ExprContext?): ExprAST {
        // TODO - return purely for compilation purposes
        return ExprAST.IntLiterAST(0)
    }
}