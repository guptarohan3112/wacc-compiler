package wacc_05.front_end

import wacc_05.SemanticErrors
import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class SemanticVisitor(
    private val symbolTable: SymbolTable,
    private val errorHandler: SemanticErrors
) :
    ASTVisitor<Unit> {

    override fun visitProgramAST(prog: ProgramAST) {
        prog.st = symbolTable

        // preliminary pass through the function list to add all function
        // identifiers to the symbol table
        for (func in prog.functionList) {
            func.preliminaryCheck(prog.st(), errorHandler)
        }

        for (func in prog.functionList) {
            visitFunctionAST(func)
        }

        // Check validity of statement
        // we have checked this chain and there is not a better way to do this
        // without compromising our visitor design
        prog.stat.st = prog.st()
        visit(prog.stat)
    }

    override fun visitFunctionAST(func: FunctionAST) {
        val funcIdentifier = func.st().lookupAll(func.funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            func.body.st = func.st()
            visit(func.body)
        }
    }

    override fun visitParamListAST(list: ParamListAST) {
        for (param in list.paramList) {
            param.st = list.st()
            visitParamAST(param)
        }
    }

    override fun visitParamAST(param: ParamAST) {
        // Check validity of parameter type
        param.type.st = param.st()
        visit(param.type)

        // Create parameter identifier and add to symbol table
        val typeIdent: TypeIdentifier = param.type.getType(param.st())
        val paramIdent = ParamIdentifier(typeIdent)
        param.st().add(param.name, paramIdent)
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        return
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        // Check validity of type of identifier that is being declared
        decl.type.st = decl.st()
        visit(decl.type)

        val variable: IdentifierObject? = decl.st().lookup(decl.varName)
        if (variable != null && variable is VariableIdentifier) {
            errorHandler.repeatVariableDeclaration(decl.ctx, decl.varName)
        } else {
            // Check that right hand side and type of identifier match
            val typeIdent: TypeIdentifier = decl.type.getType(decl.st())
            visit(decl.assignment)
            val assignmentType: TypeIdentifier = decl.assignment.getType(decl.st())

            if (typeIdent != assignmentType && assignmentType != TypeIdentifier.GENERIC) {
                errorHandler.typeMismatch(
                    decl.ctx,
                    typeIdent,
                    decl.assignment.getType(decl.assignment.st())
                )
            }

            // Create variable identifier and add to symbol table
            val varIdent = VariableIdentifier(typeIdent)
            decl.st().add(decl.varName, varIdent)
        }
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        assign.lhs.st = assign.st()
        visitAssignLHSAST(assign.lhs)

        assign.rhs.st = assign.st()
        visit(assign.rhs)

        val lhsType = assign.lhs.getType(assign.st())
        val rhsType = assign.rhs.getType(assign.st())

        if (lhsType != rhsType && lhsType != TypeIdentifier.GENERIC && rhsType != TypeIdentifier.GENERIC) {
            errorHandler.typeMismatch(assign.ctx, lhsType, rhsType)
        }
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        begin.stat.st = SymbolTable(begin.st())
        visit(begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        read.lhs.st = read.st()
        visitAssignLHSAST(read.lhs)

        val type = read.lhs.getType(read.lhs.st())

        if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
            errorHandler.invalidReadType(read.ctx, type)
        }
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        exit.expr.st = exit.st()
        visit(exit.expr)

        // Ensure exit is only on an integer
        if (exit.expr.getType(exit.st()) !is TypeIdentifier.IntIdentifier) {
            errorHandler.invalidExitType(exit.ctx, exit.expr.getType(exit.st()))
        }
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        free.expr.st = free.st()
        visit(free.expr)

        val type = free.expr.getType(free.st())

        if (!(type is TypeIdentifier.PairIdentifier || type is TypeIdentifier.ArrayIdentifier)) {
            errorHandler.invalidFreeType(free.ctx, type)
        }
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Check validity of conditional expression
        ifStat.condExpr.st = ifStat.st()
        visit(ifStat.condExpr)

        // Ensure that the condition expression evaluates to a boolean
        if (ifStat.condExpr.getType(ifStat.condExpr.st()) != TypeIdentifier.BOOL_TYPE) {
            errorHandler.typeMismatch(
                ifStat.ctx,
                TypeIdentifier.BOOL_TYPE,
                ifStat.condExpr.getType(ifStat.st())
            )
            return
        }

        val returnTypeIdent: TypeIdentifier? = ifStat.st().lookup("returnType") as TypeIdentifier?
        val thenSt = SymbolTable(ifStat.st())
        val elseSt = SymbolTable(ifStat.st())
        // Propogate return type down in case there is a function that is nested
        if (returnTypeIdent != null) {
            thenSt.add("returnType", returnTypeIdent)
            elseSt.add("returnType", returnTypeIdent)
        }

        ifStat.thenStat.st = thenSt
        visit(ifStat.thenStat)
        ifStat.elseStat.st = elseSt
        visit(ifStat.elseStat)
    }

    override fun visitPrintAST(print: StatementAST.PrintAST) {
        print.expr.st = print.st()
        visit(print.expr)
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        if (ret.st().isMain()) {
            errorHandler.invalidReturn(ret.ctx)
        }

        // Check validity of expression
        ret.expr.st = ret.st()
        visit(ret.expr)

        // Check that type of expression being returned is the same as the return type of the function that defines the current scope
        val returnType: TypeIdentifier = ret.expr.getType(ret.st())
        val funcReturnType: TypeIdentifier? = ret.st().lookup("returnType") as TypeIdentifier?
        if (funcReturnType != returnType) {
            errorHandler.invalidReturnType(ret.ctx)
        }
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        seq.stat1.st = seq.st()
        visit(seq.stat1)
        seq.stat2.st = seq.st()
        visit(seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        // Check validity of looping expression
        whileStat.loopExpr.st = whileStat.st()
        visit(whileStat.loopExpr)

        // Check that looping expression evaluates to a boolean
        if (whileStat.loopExpr.getType(whileStat.st()) != TypeIdentifier.BOOL_TYPE) {
            errorHandler.typeMismatch(
                whileStat.ctx,
                TypeIdentifier.BOOL_TYPE,
                whileStat.loopExpr.getType(whileStat.st())
            )
        } else {
            val bodySt = SymbolTable(whileStat.st)
            val returnTypeIdent: TypeIdentifier? = whileStat.st().lookup("returnType") as TypeIdentifier?
            if (returnTypeIdent != null) {
                bodySt.add("returnType", returnTypeIdent)
            }

            whileStat.body.st = bodySt
            visit(whileStat.body)
        }
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        return
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        return
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        return
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        return
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        return
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        if (ident.st().lookupAll(ident.value) == null) {
            errorHandler.invalidIdentifier(ident.ctx, ident.value)
        }
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        for (expr in arrayElem.exprs) {
            expr.st = arrayElem.st()
            visit(expr)
            val type: TypeIdentifier = expr.getType(arrayElem.st())
            if (type !is TypeIdentifier.IntIdentifier) {
                errorHandler.typeMismatch(arrayElem.ctx, TypeIdentifier.INT_TYPE, type)
            }
        }

        val variable: IdentifierObject? = arrayElem.st().lookupAll(arrayElem.ident)

        if (variable == null) {
            errorHandler.invalidIdentifier(arrayElem.ctx, arrayElem.ident)
        } else {
            val variableType = variable.getType()
            if (variableType !is TypeIdentifier.ArrayIdentifier) {
                errorHandler.typeMismatch(
                    arrayElem.ctx,
                    variableType,
                    TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0)
                )
            }
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        val symTab: SymbolTable = unop.st()
        unop.expr.st = symTab
        visit(unop.expr)
        val exprType = unop.expr.getType(symTab)

        when (unop.operator) {
            "len" -> {
                if (exprType !is TypeIdentifier.ArrayIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0), exprType)
                }
            }
            "ord" -> {
                if (exprType !is TypeIdentifier.CharIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.CHAR_TYPE, exprType)
                }
            }
            "chr" -> {
                if (exprType !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.INT_TYPE, exprType)
                }
            }
            "!" -> {
                if (exprType !is TypeIdentifier.BoolIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.BOOL_TYPE, exprType)
                }
            }
            "-" -> {
                if (exprType !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.INT_TYPE, exprType)
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        // Set the symbol tables for both expressions and perform semantic checks on them
        binop.expr1.st = binop.st()
        binop.expr2.st = binop.st()

        visit(binop.expr1)
        visit(binop.expr2)

        val expr1Type = binop.expr1.getType(binop.st())
        val expr2Type = binop.expr2.getType(binop.st())

        when {
            ExprAST.BinOpAST.intIntFunctions.contains(binop.operator) -> {
                if (expr1Type !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(binop.ctx, TypeIdentifier.INT_TYPE, expr1Type)
                }

                if (expr2Type !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(binop.ctx, TypeIdentifier.INT_TYPE, expr2Type)
                }
            }
            ExprAST.BinOpAST.intCharFunctions.contains(binop.operator) -> {
                // if type1 is valid, check against type 2 and type1 dominates if not equal
                // if type2 is valid and type1 is not, type mismatch on type2
                // else type mismatch on both

                if (expr1Type is TypeIdentifier.IntIdentifier || expr1Type is TypeIdentifier.CharIdentifier) {
                    if (expr1Type != expr2Type) {
                        errorHandler.typeMismatch(binop.ctx, expr1Type, expr2Type)
                    }
                    return
                }

                if (expr2Type is TypeIdentifier.IntIdentifier || expr2Type is TypeIdentifier.CharIdentifier) {
                    // we already know type 1 isn't valid
                    errorHandler.typeMismatch(binop.ctx, expr2Type, expr1Type)
                    return
                }

                // both aren't valid
                errorHandler.typeMismatch(binop.ctx, TypeIdentifier.INT_TYPE, expr1Type)
                errorHandler.typeMismatch(binop.ctx, TypeIdentifier.INT_TYPE, expr2Type)
            }
            ExprAST.BinOpAST.boolBoolFunctions.contains(binop.operator) -> {
                if (expr1Type !is TypeIdentifier.BoolIdentifier) {
                    errorHandler.typeMismatch(binop.ctx, TypeIdentifier.BOOL_TYPE, expr1Type)
                }

                if (expr2Type !is TypeIdentifier.BoolIdentifier) {
                    errorHandler.typeMismatch(binop.ctx, TypeIdentifier.BOOL_TYPE, expr2Type)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    override fun visitBaseTypeAST(type: TypeAST.BaseTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairTypeAST(type: TypeAST.PairTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairElemTypeAST(type: TypeAST.PairElemTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        // If the array literal is empty, no semantic check need to be done
        if (arrayLiter.elems.size != 0) {
            arrayLiter.elems[0].check(symbolTable, errorHandler)
            val firstElemType = arrayLiter.elems[0].getType(symbolTable)

            // Verify that individual elements are semantically correct and that they are all the same type
            for (i in 1 until arrayLiter.elems.size) {
                arrayLiter.elems[i].check(symbolTable, errorHandler)
                if (arrayLiter.elems[i].getType(symbolTable) != firstElemType) {
                    errorHandler.typeMismatch(
                        arrayLiter.ctx,
                        firstElemType,
                        arrayLiter.elems[i].getType(symbolTable)
                    )
                }
            }
        }
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        if (lhs.arrElem != null) {
            lhs.arrElem!!.check(symbolTable, errorHandler)
        } else if (lhs.pairElem != null) {
            lhs.pairElem!!.check(symbolTable, errorHandler)
        } else {
            val type = symbolTable.lookupAll(lhs.ident!!)
            if (type == null) {
                errorHandler.invalidIdentifier(lhs.ctx, lhs.ident)
                // Add the identifier into symbol table for error recovery
                symbolTable.add(lhs.ident, VariableIdentifier(TypeIdentifier.GENERIC))
            } else if (type is FunctionIdentifier) {
                errorHandler.invalidAssignment(lhs.ctx, lhs.ident)
            }
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        when (val funcIdentifier: IdentifierObject? = symbolTable.lookupAll(funcCall.funcName)) {
            null -> {
                errorHandler.invalidIdentifier(funcCall.ctx, funcCall.funcName)
            }
            !is FunctionIdentifier -> {
                errorHandler.invalidFunction(funcCall.ctx, funcCall.funcName)
            }
            else -> {
                // Check that the number of args is as expected
                val noOfArgs: Int = funcIdentifier.getParams().size
                if (noOfArgs != funcCall.args.size) {
                    errorHandler.argNumberError(
                        funcCall.ctx,
                        funcCall.funcName,
                        noOfArgs,
                        funcCall.args.size
                    )
                }

                // Check that arg type match up with corresponding parameter type
                for (i in 0 until funcCall.args.size.coerceAtMost(noOfArgs)) {
                    funcCall.args[i].check(symbolTable, errorHandler)
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = funcCall.args[i].getType(symbolTable)
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(funcCall.ctx, expectedType, actualType)
                    }
                }
            }
        }
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        newPair.fst.check(symbolTable, errorHandler)
        newPair.snd.check(symbolTable, errorHandler)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        pairElem.elem.check(symbolTable, errorHandler)

        val elemType = pairElem.elem.getType(symbolTable)

        // The type of the element has to be a generic type (when added for error recovery), a pair or a pair literal
        if (elemType != TypeIdentifier.GENERIC
            && elemType !is TypeIdentifier.PairIdentifier
            && elemType !is TypeIdentifier.PairLiterIdentifier
        ) {
            errorHandler.typeMismatch(pairElem.ctx, TypeIdentifier.PairLiterIdentifier, elemType)
        }
    }
}