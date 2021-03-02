package wacc_05.front_end

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*
import java.lang.reflect.Type

class SemanticVisitor(
    private val symbolTable: SymbolTable,
    private val errorHandler: SemanticErrors
) : ASTVisitor<Unit> {

    override fun visitProgramAST(prog: ProgramAST) {
        prog.st = symbolTable

        // preliminary pass through the function list to add all function
        // identifiers to the symbol table
        for (func in prog.functionList) {
            preliminaryCheck(prog.st(), func)
        }

        for (func in prog.functionList) {
            visitFunctionAST(func)
        }

        // Check validity of statement
        // we have checked this chain and there is not a better way to do this
        // without compromising our visitor design
        visitChild(prog.st(), prog.stat)
    }

    private fun preliminaryCheck(symTab: SymbolTable, func: FunctionAST) {
        visitChild(symTab, func.returnType)

        // Check to make sure function has not already been defined
        val funcIdent: FunctionIdentifier? = FunctionST.lookup(func.funcName)
        // Create function identifier and add to symbol table
        val funcST = SymbolTable(symTab)
        func.st = funcST
        if (funcIdent != null) {
            errorHandler.repeatVariableDeclaration(func.ctx, func.funcName)
        } else {
            func.returnType.st = funcST
            val returnTypeIdent: TypeIdentifier = func.returnType.getType()

            val newFuncIdent =
                FunctionIdentifier(
                    returnTypeIdent,
                    func.paramList?.getParams(funcST) ?: ArrayList(),
                    funcST
                )

            funcST.add("returnType", returnTypeIdent)

            // add self to higher level symbol table
            FunctionST.add(func.funcName, newFuncIdent)
        }
    }

    override fun visitFunctionAST(func: FunctionAST) {
        val funcIdentifier = FunctionST.lookupAll(func.funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            if (func.paramList != null) {
                visitChild(func.st(), func.paramList)
            }
            visitChild(func.st(), func.body)
        }
    }

    override fun visitParamListAST(list: ParamListAST) {
        for (param in list.paramList) {
            visitChild(list.st(), param)
        }
    }

    override fun visitParamAST(param: ParamAST) {
        // Check validity of parameter type
        visitChild(param.st(), param.type)

        // Create parameter identifier and add to symbol table
        param.type.st = param.st()
        val typeIdent: TypeIdentifier = param.type.getType()
        val paramIdent = ParamIdentifier(typeIdent)
        param.st().add(param.name, paramIdent)
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        return
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        // Check validity of type of identifier that is being declared
        visitChild(decl.st(), decl.type)

        val variable: IdentifierObject? = decl.st().lookup(decl.varName)
        if (variable != null && variable is VariableIdentifier) {
            errorHandler.repeatVariableDeclaration(decl.ctx, decl.varName)
        } else {
            // Check that right hand side and type of identifier match
            decl.type.st = decl.st()
            val typeIdent: TypeIdentifier = decl.type.getType()
            visitChild(decl.st(), decl.assignment)
            val assignmentType: TypeIdentifier = decl.assignment.getType()

            if (typeIdent != assignmentType && assignmentType != TypeIdentifier.GENERIC) {
                errorHandler.typeMismatch(
                    decl.ctx,
                    typeIdent,
                    decl.assignment.getType()
                )
            }

            // Create variable identifier and add to symbol table
            val varIdent = VariableIdentifier(typeIdent)
            decl.st().add(decl.varName, varIdent)
        }
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        val symTab: SymbolTable = assign.st()
        visitChild(symTab, assign.lhs)
        visitChild(symTab, assign.rhs)

        val lhsType = assign.lhs.getType(assign.st())
        val rhsType = assign.rhs.getType()

        if (lhsType != rhsType && lhsType != TypeIdentifier.GENERIC && rhsType != TypeIdentifier.GENERIC) {
            errorHandler.typeMismatch(assign.ctx, lhsType, rhsType)
        }
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        val symTab = SymbolTable(begin.st())
        visitChild(symTab, begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        visitChild(read.st(), read.lhs)

        val type = read.lhs.getType(read.lhs.st())

        if (!(type is TypeIdentifier.IntIdentifier || type is TypeIdentifier.CharIdentifier)) {
            errorHandler.invalidReadType(read.ctx, type)
        }
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        visitChild(exit.st(), exit.expr)

        // Ensure exit is only on an integer
        if (exit.expr.getType() !is TypeIdentifier.IntIdentifier) {
            errorHandler.invalidExitType(exit.ctx, exit.expr.getType())
        }
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        visitChild(free.st(), free.expr)

        val type = free.expr.getType()

        if (!(type is TypeIdentifier.PairIdentifier || type is TypeIdentifier.ArrayIdentifier)) {
            errorHandler.invalidFreeType(free.ctx, type)
        }
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Check validity of conditional expression
        visitChild(ifStat.st(), ifStat.condExpr)

        // Ensure that the condition expression evaluates to a boolean
        if (ifStat.condExpr.getType() != TypeIdentifier.BOOL_TYPE) {
            errorHandler.typeMismatch(
                ifStat.ctx,
                TypeIdentifier.BOOL_TYPE,
                ifStat.condExpr.getType()
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

        visitChild(thenSt, ifStat.thenStat)
        visitChild(elseSt, ifStat.elseStat)
    }

    override fun visitPrintAST(print: StatementAST.PrintAST) {
        visitChild(print.st(), print.expr)
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        if (ret.st().isMain()) {
            errorHandler.invalidReturn(ret.ctx)
        }

        // Check validity of expression
        visitChild(ret.st(), ret.expr)

        // Check that type of expression being returned is the same as the return type of the function that defines the current scope
        val returnType: TypeIdentifier = ret.expr.getType()
        val funcReturnType: TypeIdentifier? = ret.st().lookup("returnType") as TypeIdentifier?
        if (funcReturnType != returnType) {
            errorHandler.invalidReturnType(ret.ctx)
        }
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        visitChild(seq.st(), seq.stat1)
        visitChild(seq.st(), seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        // Check validity of looping expression
        visitChild(whileStat.st(), whileStat.loopExpr)

        // Check that looping expression evaluates to a boolean
        if (whileStat.loopExpr.getType() != TypeIdentifier.BOOL_TYPE) {
            errorHandler.typeMismatch(
                whileStat.ctx,
                TypeIdentifier.BOOL_TYPE,
                whileStat.loopExpr.getType()
            )
        } else {
            val bodySt = SymbolTable(whileStat.st)
            val returnTypeIdent: TypeIdentifier? =
                whileStat.st().lookup("returnType") as TypeIdentifier?
            if (returnTypeIdent != null) {
                bodySt.add("returnType", returnTypeIdent)
            }

            visitChild(bodySt, whileStat.body)
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
        val identifier: IdentifierObject = ident.st().lookupAll(ident.value)!!
        if (ident.st().lookupAll(ident.value) == null) {
            errorHandler.invalidIdentifier(ident.ctx, ident.value)
            // Add the identifier into symbol table for error recovery
            ident.st().add(ident.value, VariableIdentifier(TypeIdentifier.GENERIC))
        }
        if (identifier is FunctionIdentifier) {
            errorHandler.invalidAssignment(ident.ctx, ident.value)
        }
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        for (expr in arrayElem.exprs) {
            visitChild(arrayElem.st(), expr)
            val type: TypeIdentifier = expr.getType()
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
        visitChild(symTab, unop.expr)
        val exprType = unop.expr.getType()

        when (unop.operator) {
            "len" -> {
                if (exprType !is TypeIdentifier.ArrayIdentifier) {
                    errorHandler.typeMismatch(
                        unop.ctx,
                        TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0),
                        exprType
                    )
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
        visitChild(binop.st(), binop.expr1)
        visitChild(binop.st(), binop.expr2)

        val expr1Type = binop.expr1.getType()
        val expr2Type = binop.expr2.getType()

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
        val typeIdent: IdentifierObject? = type.st!!.lookupAll(type.typeName)

        if (typeIdent == null) {
            errorHandler.invalidIdentifier(type.ctx, type.typeName)
        } else if (typeIdent !is TypeIdentifier) {
            errorHandler.invalidType(type.ctx, type.typeName)
        }
    }

    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST) {
        visitChild(type.st(), type.elemsType)
    }

    override fun visitPairTypeAST(type: TypeAST.PairTypeAST) {
        visitChild(type.st(), type.fstType)
        visitChild(type.st(), type.sndType)
    }

    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST) {
        if (elemType.type != null) {
            visitChild(elemType.st(), elemType.type)
        }
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        // If the array literal is empty, no semantic check need to be done
        if (arrayLiter.elems.size != 0) {
            val arraySymTab: SymbolTable = arrayLiter.st()
            visitChild(arraySymTab, arrayLiter.elems[0])
            val firstElemType = arrayLiter.elems[0].getType()

            // Verify that individual elements are semantically correct and that they are all the same type
            for (i in 1 until arrayLiter.elems.size) {
                visitChild(arraySymTab, arrayLiter.elems[i])
                if (arrayLiter.elems[i].getType() != firstElemType) {
                    errorHandler.typeMismatch(
                        arrayLiter.ctx,
                        firstElemType,
                        arrayLiter.elems[i].getType()
                    )
                }
            }
        }
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        val symTab: SymbolTable = lhs.st()
        if (lhs.arrElem != null) {
            visitChild(symTab, lhs.arrElem!!)
        } else if (lhs.pairElem != null) {
            visitChild(symTab, lhs.pairElem!!)
        } else {
            visitChild(symTab, lhs.ident!!)
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        val symTab: SymbolTable = funcCall.st()
        when (val funcIdentifier: FunctionIdentifier? = FunctionST.lookupAll(funcCall.funcName)) {
            null -> {
                errorHandler.invalidIdentifier(funcCall.ctx, funcCall.funcName)
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
                    visitChild(symTab, funcCall.args[i])
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = funcCall.args[i].getType()
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(funcCall.ctx, expectedType, actualType)
                    }
                }
            }
        }
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        visitChild(newPair.st(), newPair.fst)
        visitChild(newPair.st(), newPair.snd)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visitChild(pairElem.st(), pairElem.elem)

        val elemType = pairElem.elem.getType()

        // The type of the element has to be a generic type (when added for error recovery), a pair or a pair literal
        if (elemType != TypeIdentifier.GENERIC
            && elemType !is TypeIdentifier.PairIdentifier
            && elemType !is TypeIdentifier.PairLiterIdentifier
        ) {
            errorHandler.typeMismatch(pairElem.ctx, TypeIdentifier.PairLiterIdentifier, elemType)
        }
    }

    // A helper method that sets the symbol table field of the child node before carrying out semantic checks on it
    private fun visitChild(symTab: SymbolTable, child: AST) {
        child.st = symTab
        visit(child)
    }
}