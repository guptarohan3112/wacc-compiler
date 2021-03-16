package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.*

/* Class: ASTBaseVisitor<T>
 * ------------------------
 * A class that will simply visit the children for each
 * node, or do nothing if they do not have children
 */
abstract class ASTBaseVisitor : ASTVisitor<Unit> {

    override fun visitProgramAST(prog: ProgramAST) {
        for (func in prog.functionList) {
            visitFunctionAST(func)
        }

        visit(prog.stat)
    }

    override fun visitFunctionAST(func: FunctionAST) {
        if (func.paramList != null) {
            visitParamListAST(func.paramList)
        }
        visit(func.body)
    }

    override fun visitParamListAST(list: ParamListAST) {
        for (param in list.paramList) {
            visitParamAST(param)
        }
    }

    override fun visitParamAST(param: ParamAST) {
        visit(param.type)
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        // do nothing
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.type)
        visit(decl.assignment)
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        visitAssignLHSAST(assign.lhs)
        visit(assign.rhs)
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        visit(begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        visitAssignLHSAST(read.lhs)
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        visit(exit.expr)
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        visit(free.expr)
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        visit(ifStat.condExpr)
        visit(ifStat.thenStat)
        visit(ifStat.elseStat)
    }

    override fun visitPrintAST(print: StatementAST.PrintAST) {
        visit(print.expr)
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        visit(ret.expr)
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        visit(seq.stat1)
        visit(seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        visit(whileStat.loopExpr)
        visit(whileStat.body)
    }

    override fun visitForAST(forLoop: StatementAST.ForAST) {
        visit(forLoop.decl)
        visit(forLoop.loopExpr)
        visit(forLoop.update)
        visit(forLoop.body)
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        // do nothing
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        // do nothing
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        // do nothing
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        // do nothing
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        // do nothing
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        // do nothing
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        for (elem in arrayElem.exprs) {
            visit(elem)
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)
    }

    override fun visitBaseTypeAST(type: TypeAST.BaseTypeAST) {
        // do nothing
    }

    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST) {
        visit(type.elemsType)
    }

    override fun visitPairTypeAST(type: TypeAST.PairTypeAST) {
        visitPairElemTypeAST(type.fstType)
        visitPairElemTypeAST(type.sndType)
    }

    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST) {
        if (elemType.type != null) {
            visit(elemType.type)
        }
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        // do nothing
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        if (lhs.arrElem != null) {
            visitArrayElemAST(lhs.arrElem!!)
        } else if (lhs.pairElem != null) {
            visitPairElemAST(lhs.pairElem!!)
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        for (arg in funcCall.args) {
            visit(arg)
        }
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        visit(newPair.fst)
        visit(newPair.snd)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visit(pairElem.elem)
    }
}