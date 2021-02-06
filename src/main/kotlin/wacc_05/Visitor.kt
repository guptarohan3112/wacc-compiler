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

    override fun visitIntLit(ctx: WaccParser.IntLitContext): ExprAST.IntLiterAST {
        // TODO - add sign parameter
        return ExprAST.IntLiterAST(ctx.INT_LIT().text)
    }

    override fun visitBoolLit(ctx: WaccParser.BoolLitContext): ExprAST.BoolLiterAST {
        return ExprAST.BoolLiterAST(ctx.BOOL_LIT().text)
    }

    override fun visitCharLit(ctx: WaccParser.CharLitContext): ExprAST.CharLiterAST {
        return ExprAST.CharLiterAST(ctx.CHAR_LIT().text)
    }

    override fun visitStrLit(ctx: WaccParser.StrLitContext): ExprAST.StrLiterAST {
        return ExprAST.StrLiterAST(ctx.STR_LIT().text)
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


    /* Function: VisitArrayElem()
        ------------------------
        Returns a ArrayElemAST node, by matching each of the expression children in the context and adding to internal
        ArrayList. Also matches IDENT token to get id of the ArrayElem
     */

    override fun visitArrayElem(ctx: WaccParser.ArrayElemContext): ExprAST.ArrayElemAST {
        val ctxs: List<WaccParser.ExprContext> = ctx.expr()
        val exprs: ArrayList<ExprAST> = ArrayList()
        for (context in ctxs) {
            exprs.add(visitExpr(context))
        }
        return ExprAST.ArrayElemAST(ctx.IDENT().text, exprs)
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

    /* Function: visitExpr()
    -----------------------
    Generates an ExprAST node depending on the type of the context. Calls respective visitor of each type to
    produce AST node, except for in simple cases like IDENT, where the AST node can be generated directly.
     */
    override fun visitExpr(ctx: WaccParser.ExprContext): ExprAST {
        // we use the when statement to check which type of expr we have and generate a child AST
        // node accordingly
        return when {
            ctx.intLit() != null -> {
                return visitIntLit(ctx.intLit())
            }
            ctx.boolLit() != null -> {
                return visitBoolLit(ctx.boolLit())
            }
            ctx.charLit() != null -> {
                return visitCharLit(ctx.charLit())
            }
            ctx.strLit() != null -> {
                return visitStrLit(ctx.strLit())
            }
            ctx.PAIR_LIT() != null -> {
                return ExprAST.PairLiterAST(value = ctx.PAIR_LIT().text)
            }
            ctx.IDENT() != null -> {
                return ExprAST.IdentAST(value = ctx.IDENT().text)
            }
            ctx.arrayElem() != null -> {
                return visitArrayElem(ctx.arrayElem())
            }
            ctx.unaryOper() != null -> {
                // TODO : this is a hacky solution, why is ctx.expr a list in the first place?
                return ExprAST.UnOpAST(visitExpr(ctx.expr()[0]), ctx.unaryOper().text)
            }
            ctx.OPEN_PARENTHESES() != null -> {
                // TODO: See above
                return visitExpr(ctx.expr()[0])
            }
            // TODO - throw suitable error
            else -> throw Exception()
        }
    }
}