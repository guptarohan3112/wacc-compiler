package wacc_05

import antlr.WaccParser
import antlr.WaccParserBaseVisitor
import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.front_end.ErrorCode
import kotlin.math.pow
import kotlin.system.exitProcess

class Visitor : WaccParserBaseVisitor<AST>() {

    /* Function: visitProg()
       ----------------------------
       Generates a ProgramAST node, calling visitFunc() and visitStat() to get the children.
    */
    override fun visitProg(ctx: WaccParser.ProgContext): ProgramAST {
        val funcs: MutableList<FunctionAST> = ArrayList()
        for (funcCtx in ctx.func()) {
            funcs.add(visitFunc(funcCtx))
        }
        return ProgramAST(funcs as ArrayList, visitStat(ctx.stat()))
    }

    /* Function: visitFunc()
        ----------------------------
        Generates a FunctionAST node, calling visitType() to get the type, using the context's IDENT token,
        calling visitParamList() if the paramList of ctx is not null, and visitStat() to get the statement child.
     */
    override fun visitFunc(ctx: WaccParser.FuncContext): FunctionAST {

        val last = getEndOfBody(ctx.stat())
        if ((last !is WaccParser.StatReturnContext) && (last !is WaccParser.StatExitContext)) {
            println(
                "Syntax Error 100:\n" +
                        "Missing Return Or Exit Statement at end of function ${ctx.IDENT()} " +
                        "on line ${ctx.start.line}:${ctx.start.charPositionInLine}"
            )
            exitProcess(ErrorCode.SYNTAX_ERROR)
        }

        val paramList: ParamListAST? = if (ctx.paramList() == null) {
            null
        } else {
            visitParamList(ctx.paramList())
        }

        return FunctionAST(
            ctx,
            visitType(ctx.type()),
            ctx.IDENT().text,
            paramList,
            visitStat(ctx.stat())
        )
    }

    /* Private Helper Method for visitFunc() which gets last statement in body of function */
    private fun getEndOfBody(stat: WaccParser.StatContext): WaccParser.StatContext {

        val statements: List<WaccParser.StatContext> = when (stat) {
            is WaccParser.StatSequentialContext -> stat.stat()
            is WaccParser.StatIfContext -> stat.stat()
            else -> emptyList()
        }

        // Case where we have a sequence of statements or an if statement
        if (statements.isNotEmpty()) {
            return getEndOfBody(statements[statements.size - 1])
        }
        // Case when we have a single statement, rather than one of the above */
        return stat
    }

    /* Function: visitParamList()
       --------------------------
       Generates a ParamListAST, using the context to create a list of ParamASTs as its children
     */
    override fun visitParamList(ctx: WaccParser.ParamListContext): ParamListAST {
        val params: MutableList<ParamAST> = ArrayList()
        for (param in ctx.param()) {
            params.add(visitParam(param))
        }

        return ParamListAST(params as ArrayList)
    }

    /* Function: visitParam()
       ----------------------
       Generates a ParamAST node, using the IDENT from the context and calling visitType() to retrieve the type.
     */
    override fun visitParam(ctx: WaccParser.ParamContext): ParamAST {
        return ParamAST(visitType(ctx.type()), ctx.IDENT().text)
    }

    /* Function: visitStat()
       ---------------------
       A function to be used when we have a StatContext but do not know its subtype (StatSkip, ... etc.)
       Param ctx - the context we want to visit but do not know its specific type
       Returns the StatementAST resulting from visiting the given ctx
     */
    private fun visitStat(ctx: WaccParser.StatContext): StatementAST {
        // visit() would return AST! but since we override all stat methods here we can safely cast to StatementAST.
        return visit(ctx) as StatementAST
    }

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
        return StatementAST.DeclAST(
            ctx,
            visitType(ctx.type()),
            ctx.IDENT().text,
            visitAssignRHS(ctx.assignRHS())
        )
    }

    /* Function: visitStatAssign()
       ----------------------------
       Generates an AssignAST node using the assignLHS and assignRHS from the context and relevant visitX() calls
       where necessary.
     */
    override fun visitStatAssign(ctx: WaccParser.StatAssignContext): StatementAST {
        return StatementAST.AssignAST(
            ctx,
            visitAssignLHS(ctx.assignLHS()),
            visitAssignRHS(ctx.assignRHS())
        )
    }

    /* Function: visitStatRead()
       -------------------------
       Generates a ReadAST node with the child being the AssignLHSAST resulting from calling visitAssignLHS on the
       context's assignLHS child.
     */
    override fun visitStatRead(ctx: WaccParser.StatReadContext): StatementAST {
        return StatementAST.ReadAST(ctx, visitAssignLHS(ctx.assignLHS()))
    }

    /* Function: visitStatFree()
       -------------------------
       Generates a FreeAST node with the child being the ExprAST resulting from calling visitExpr() on the
       context's expr child.
     */
    override fun visitStatFree(ctx: WaccParser.StatFreeContext): StatementAST {
        return StatementAST.FreeAST(ctx, visitExpr(ctx.expr()))
    }

    /* Function: visitStatReturn()
       -------------------------
       Generates a ReturnAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatReturn(ctx: WaccParser.StatReturnContext): StatementAST {
        return StatementAST.ReturnAST(ctx, visitExpr(ctx.expr()))
    }

    /* Function: visitStatExit()
       -------------------------
       Generates an ExitAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatExit(ctx: WaccParser.StatExitContext): StatementAST {
        return StatementAST.ExitAST(ctx, visitExpr(ctx.expr()))
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

    /* Function: visitStatIf()
        ----------------------
        Returns an IfAST node with children corresponding to calls to visitExpr() and visitStat(). Assumes there are
        2 stat children (one for the if branch and one for the else branch.)
     */
    override fun visitStatIf(ctx: WaccParser.StatIfContext): StatementAST {
        return StatementAST.IfAST(
            ctx,
            visitExpr(ctx.expr()),
            visitStat(ctx.stat(0)),
            visitStat(ctx.stat(1))
        )
    }

    /* Function: visitStatWhile()
       --------------------------
       Returns a WhileAST node with children corresponding to calls to visitExpr() and visitStat().
     */
    override fun visitStatWhile(ctx: WaccParser.StatWhileContext): StatementAST {
        return StatementAST.WhileAST(ctx, visitExpr(ctx.expr()), visitStat(ctx.stat()))
    }

    /* Function: visitStatFor()
       ------------------------
       Returns a ForAST node with children corresponding to calls to visitExpr() and visitStat().
     */
    override fun visitStatFor(ctx: WaccParser.StatForContext): AST {
        val decl: WaccParser.StatContext = ctx.stat(0)
        val update: WaccParser.StatContext = ctx.stat(1)
        val body: WaccParser.StatContext = ctx.stat(2)
        return StatementAST.ForAST(
            ctx,
            visitStat(decl),
            visitExpr(ctx.expr()),
            visitStat(update),
            visitStat(body)
        )
    }

    /* Function: visitStatBeginEnd()
       -----------------------------
       Returns a BeginAST node which has one child, corresponding to a call to visitStat().
     */
    override fun visitStatBeginEnd(ctx: WaccParser.StatBeginEndContext): StatementAST {
        return StatementAST.BeginAST(visitStat(ctx.stat()))
    }

    /* Function: visitStatSequential()
       -------------------------------
       Returns a SequentialAST node with children corresponding to two calls to visitStat(). Assumes that ctx has
       two stat children.
     */
    override fun visitStatSequential(ctx: WaccParser.StatSequentialContext): StatementAST {
        return StatementAST.SequentialAST(visitStat(ctx.stat(0)), visitStat(ctx.stat(1)))
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
            else -> throw Exception("Error : Cannot match context with type")
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
            ctx.ident() != null -> {
//                AssignLHSAST(ctx = ctx, ident = ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(), 0), value = ctx.IDENT().text))
                AssignLHSAST(ctx = ctx, ident = visitIdent(ctx.ident()))
            }
            ctx.pairElem() != null -> {
                AssignLHSAST(ctx = ctx, pairElem = visitPairElem(ctx.pairElem()))
            }
            ctx.arrayElem() != null -> {
                AssignLHSAST(ctx = ctx, arrElem = visitArrayElem(ctx.arrayElem()))
            }
            else -> throw Exception("Error : Cannot match context with type")
        }

    }

    override fun visitIdent(ctx: WaccParser.IdentContext?): ExprAST.IdentAST {
        return ExprAST.IdentAST(ctx!!, ctx.IDENT().text)
    }

    /* Function: visitType()
        ------------------------
        Returns a TypeAST node, by matching the context with the relevant type and then calling its
        respective visit() function
     */
    override fun visitType(ctx: WaccParser.TypeContext): TypeAST {

        return when {
            ctx.baseType() != null -> {
                visitBaseType(ctx.baseType())
            }
            ctx.pairType() != null -> {
                visitPairType(ctx.pairType())
            }
            ctx.type() != null -> {
                TypeAST.ArrayTypeAST(visitType(ctx.type()))
            }
            else -> throw Exception("Error : Cannot match context with type")
        }
    }

    /* Function: visitBaseType()
        ------------------------
        Returns a BaseTypeAST node with the actual type as a string field
     */
    override fun visitBaseType(ctx: WaccParser.BaseTypeContext): TypeAST.BaseTypeAST {
        return TypeAST.BaseTypeAST(ctx, ctx.text)
    }

    /* Function: visitPairType()
        ------------------------
        Returns a PairTypeAST node with children corresponding to two calls to visitPairElemType().
        Assumes that ctx has two PairElemType children.
     */
    override fun visitPairType(ctx: WaccParser.PairTypeContext): TypeAST.PairTypeAST {
        return TypeAST.PairTypeAST(
            visitPairElemType(ctx.pairElemType(0)),
            visitPairElemType(ctx.pairElemType(1))
        )
    }

    /* Function: visitPairElemType()
        ------------------------
        Returns a PairElemTypeAST node, by matching the context with the relevant type and then calling its
        respective visit() function, or in the case of a PAIR token, creating the node directly
     */
    override fun visitPairElemType(ctx: WaccParser.PairElemTypeContext): TypeAST.PairElemTypeAST {
        return when {
            ctx.type() != null -> {
                TypeAST.PairElemTypeAST(type = TypeAST.ArrayTypeAST(visitType(ctx.type())))
            }
            ctx.baseType() != null -> {
                TypeAST.PairElemTypeAST(type = visitBaseType(ctx.baseType()))
            }
            ctx.PAIR() != null -> {
                TypeAST.PairElemTypeAST(pair = ctx.PAIR().text, type = null)
            }
            else -> throw Exception("Error : Cannot match context with type")
        }
    }

    /* Function: visitIntLit()
        ------------------------
        Returns a IntLiterAST node, by matching the sign token and the value of the literal as a string
     */
    override fun visitIntLit(ctx: WaccParser.IntLitContext): ExprAST.IntLiterAST {
        var sign = ""
        var limit = 2.0.pow(31.0)

        if (ctx.PLUS() != null) {
            sign = ctx.PLUS().text
            limit -= 1
        }
        if (ctx.MINUS() != null) {
            sign = ctx.MINUS().text
        }

        if (ctx.INT_LIT().text.toDouble() > limit) {
            println(
                "Syntax Error (Error 100)\n : " +
                        "Integer value ${ctx.INT_LIT().text} on line ${ctx.start.line} is badly formatted " +
                        "(either it has a badly defined sign or it is too large for a 32-bit signed integer)"
            )
            exitProcess(ErrorCode.SYNTAX_ERROR)
        }
        return ExprAST.IntLiterAST(sign, ctx.INT_LIT().text)
    }

    /* Function: visitBoolLit()
        ------------------------
        Returns a BoolLiterAST node with the value of the literal as a string
     */
    override fun visitBoolLit(ctx: WaccParser.BoolLitContext): ExprAST.BoolLiterAST {
        return ExprAST.BoolLiterAST(ctx.BOOL_LIT().text)
    }

    /* Function: visitCharLit()
        ------------------------
        Returns a CharLiterAST node with the value of the literal as a string
     */
    override fun visitCharLit(ctx: WaccParser.CharLitContext): ExprAST.CharLiterAST {
        val string: String = ctx.CHAR_LIT().text
        if (string.contains("\\")) {
            return ExprAST.CharLiterAST(string.substring(2, string.length))
        }
        return ExprAST.CharLiterAST(string.substring(1, string.length))
    }

    /* Function: visitStrLot()
        ------------------------
        Returns a StrLiterAST node with the value of the literal as a string
     */
    override fun visitStrLit(ctx: WaccParser.StrLitContext): ExprAST.StrLiterAST {
        val string: String = ctx.STR_LIT().text
        return ExprAST.StrLiterAST(string.substring(1, string.length - 1))
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
        return PairElemAST(ctx, visitExpr(ctx.expr()), ctx.FST() != null)
    }


    /* Function: visitArrayLit()
        ------------------------
        Returns a ArrayLiterAST node, by matching each of the expression children in the context and adding to internal
        ArrayList.
     */
    override fun visitArrayLit(ctx: WaccParser.ArrayLitContext): ArrayLiterAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ArrayLiterAST(ctx, exprs as ArrayList<ExprAST>)
    }

/* Function: VisitArrayElem()
    ------------------------
    Returns a ArrayElemAST node, by matching each of the expression children in the context and adding to internal
    ArrayList. Also matches IDENT token to get id of the ArrayElem
 */

    override fun visitArrayElem(ctx: WaccParser.ArrayElemContext): ExprAST.ArrayElemAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ExprAST.ArrayElemAST(ctx, ctx.IDENT().text, exprs as ArrayList<ExprAST>)
    }

    /* Function: VisitFuncCall()
        ------------------------
        Generated a FuncCallAST with function name following ctx's IDENT token, and the args are either null (ctx's
        argsList is null) or generated by calling visitArgList.
     */
    override fun visitFuncCall(ctx: WaccParser.FuncCallContext): FuncCallAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        if (ctx.argList() != null) {
            val args: WaccParser.ArgListContext = ctx.argList()

            for (exprCtx in args.expr()) {
                exprs.add(visitExpr(exprCtx))
            }
        }

        return FuncCallAST(ctx, ctx.IDENT().text, exprs as ArrayList<ExprAST>)
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
                visitIntLit(ctx.intLit())
            }
            ctx.boolLit() != null -> {
                visitBoolLit(ctx.boolLit())
            }
            ctx.charLit() != null -> {
                visitCharLit(ctx.charLit())
            }
            ctx.strLit() != null -> {
                visitStrLit(ctx.strLit())
            }
            ctx.PAIR_LIT() != null -> {
                ExprAST.PairLiterAST
            }
            ctx.ident() != null -> {
                visitIdent(ctx.ident())
            }
            ctx.arrayElem() != null -> {
                visitArrayElem(ctx.arrayElem())
            }
            ctx.unaryOper() != null -> {
                ExprAST.UnOpAST(ctx.unaryOper(), visitExpr(ctx.expr(0)), ctx.unaryOper().text)
            }
            ctx.OPEN_PARENTHESES() != null -> {
                visitExpr(ctx.expr(0))
            }
            ctx.MULT() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.MULT().text
                )
            }
            ctx.DIV() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.DIV().text
                )
            }
            ctx.MOD() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.MOD().text
                )
            }
            ctx.PLUS() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.PLUS().text
                )
            }
            ctx.MINUS() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.MINUS().text
                )
            }
            ctx.PLUS() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.PLUS().text
                )
            }
            ctx.GT() != null -> {
                ExprAST.BinOpAST(ctx, visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)), ctx.GT().text)
            }
            ctx.GTE() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.GTE().text
                )
            }
            ctx.LT() != null -> {
                ExprAST.BinOpAST(ctx, visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)), ctx.LT().text)
            }
            ctx.LTE() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.LTE().text
                )
            }
            ctx.EQ() != null -> {
                ExprAST.BinOpAST(ctx, visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)), ctx.EQ().text)
            }
            ctx.NOTEQ() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.NOTEQ().text
                )
            }
            ctx.AND() != null -> {
                ExprAST.BinOpAST(
                    ctx,
                    visitExpr(ctx.expr(0)),
                    visitExpr(ctx.expr(1)),
                    ctx.AND().text
                )
            }
            ctx.OR() != null -> {
                ExprAST.BinOpAST(ctx, visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)), ctx.OR().text)
            }
            ctx.MAP() != null -> {
                val unOps: ArrayList<ExprAST.UnOpAST> = ArrayList()
                val arrAST = visitArrayLit(ctx.arrayLit())
                val unOp = ctx.unaryOper()

                for (expr in arrAST.elems){
                    unOps.add(ExprAST.UnOpAST(unOp, expr, unOp.text))
                }

                ExprAST.MapAST(ctx, unOps)
            }
            else -> throw Exception("Error : Cannot match context with type")
        }
    }
}
