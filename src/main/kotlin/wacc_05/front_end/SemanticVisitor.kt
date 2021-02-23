package wacc_05.front_end

import wacc_05.SemanticErrors
import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class SemanticVisitor(private val st: SymbolTable, private val errorHandler: SemanticErrors) : ASTVisitor<Unit> {

    override fun visitProgramAST(prog: ProgramAST) {
        // preliminary pass through the function list to add all function
        // identifiers to the symbol table
        for (func in prog.functionList) {
            func.preliminaryCheck(st, errorHandler)
        }

        for (func in prog.functionList) {
            visitFunctionAST(func)
        }

        // Check validity of statement
        visit(prog.stat)
    }

    // begin {funcs} <stat> end
    // ProgramAST - TopLevelST
    // {FunctionAST - InnerST(TopLevelST)
    //       Body: StatementAST - InnerST}
    // StatementAST - TopLevel

    override fun visitFunctionAST(func: FunctionAST) {
        val funcIdentifier = st.lookupAll(func.funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            visit(func.body) //((funcIdentifier as FunctionIdentifier).getSymbolTable(), errorHandler)
        }
    }

    override fun visitParamListAST(list: ParamListAST) {
        TODO("Not yet implemented")
    }

    override fun visitParamAST(param: ParamAST) {
        TODO("Not yet implemented")
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        TODO("Not yet implemented")
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        TODO("Not yet implemented")
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        TODO("Not yet implemented")
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        TODO("Not yet implemented")
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        TODO("Not yet implemented")
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        TODO("Not yet implemented")
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        TODO("Not yet implemented")
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        TODO("Not yet implemented")
    }

    override fun visitPrintAST(print: StatementAST.PrintAST) {
        TODO("Not yet implemented")
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        TODO("Not yet implemented")
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        TODO("Not yet implemented")
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        TODO("Not yet implemented")
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        TODO("Not yet implemented")
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        TODO("Not yet implemented")
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        TODO("Not yet implemented")
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
            arrayLiter.elems[0].check(st, errorHandler)
            val firstElemType = arrayLiter.elems[0].getType(st)

            // Verify that individual elements are semantically correct and that they are all the same type
            for (i in 1 until arrayLiter.elems.size) {
                arrayLiter.elems[i].check(st, errorHandler)
                if (arrayLiter.elems[i].getType(st) != firstElemType) {
                    errorHandler.typeMismatch(arrayLiter.ctx, firstElemType, arrayLiter.elems[i].getType(st))
                }
            }
        }
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        if (lhs.arrElem != null) {
            lhs.arrElem!!.check(st, errorHandler)
        } else if (lhs.pairElem != null) {
            lhs.pairElem!!.check(st, errorHandler)
        } else {
            val type = st.lookupAll(lhs.ident!!)
            if (type == null) {
                errorHandler.invalidIdentifier(lhs.ctx, lhs.ident)
                // Add the identifier into symbol table for error recovery
                st.add(lhs.ident, VariableIdentifier(TypeIdentifier.GENERIC))
            } else if (type is FunctionIdentifier) {
                errorHandler.invalidAssignment(lhs.ctx, lhs.ident)
            }
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        when (val funcIdentifier: IdentifierObject? = st.lookupAll(funcCall.funcName)) {
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
                    errorHandler.argNumberError(funcCall.ctx, funcCall.funcName, noOfArgs, funcCall.args.size)
                }

                // Check that arg type match up with corresponding parameter type
                for (i in 0 until funcCall.args.size.coerceAtMost(noOfArgs)) {
                    funcCall.args[i].check(st, errorHandler)
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = funcCall.args[i].getType(st)
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(funcCall.ctx, expectedType, actualType)
                    }
                }
            }
        }
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        newPair.fst.check(st, errorHandler)
        newPair.snd.check(st, errorHandler)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        pairElem.elem.check(st, errorHandler)

        val elemType = pairElem.elem.getType(st)

        // The type of the element has to be a generic type (when added for error recovery), a pair or a pair literal
        if (elemType != TypeIdentifier.GENERIC
            && elemType !is TypeIdentifier.PairIdentifier
            && elemType !is TypeIdentifier.PairLiterIdentifier
        ) {
            errorHandler.typeMismatch(pairElem.ctx, TypeIdentifier.PairLiterIdentifier, elemType)
        }
    }
}