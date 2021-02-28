package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.*

abstract class ASTBaseVisitor<T> : ASTVisitor<T> {

    override fun visitProgramAST(prog: ProgramAST): T {
        TODO("Not yet implemented")
    }

    override fun visitFunctionAST(func: FunctionAST): T {
        TODO("Not yet implemented")
    }

    override fun visitParamListAST(list: ParamListAST): T {
        TODO("Not yet implemented")
    }

    override fun visitParamAST(param: ParamAST): T {
        TODO("Not yet implemented")
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST): T {
        TODO("Not yet implemented")
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST): T {
        TODO("Not yet implemented")
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST): T {
        TODO("Not yet implemented")
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST): T {
        TODO("Not yet implemented")
    }

    override fun visitReadAST(read: StatementAST.ReadAST): T {
        TODO("Not yet implemented")
    }

    override fun visitExitAST(exit: StatementAST.ExitAST): T {
        TODO("Not yet implemented")
    }

    override fun visitFreeAST(free: StatementAST.FreeAST): T {
        TODO("Not yet implemented")
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST): T {
        TODO("Not yet implemented")
    }

    override fun visitPrintAST(print: StatementAST.PrintAST): T {
        TODO("Not yet implemented")
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST): T {
        TODO("Not yet implemented")
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST): T {
        TODO("Not yet implemented")
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST): T {
        TODO("Not yet implemented")
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST): T {
        TODO("Not yet implemented")
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST): T {
        TODO("Not yet implemented")
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST): T {
        TODO("Not yet implemented")
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST): T {
        TODO("Not yet implemented")
    }

    override fun visitBaseTypeAST(type: TypeAST.BaseTypeAST): T {
        TODO("Not yet implemented")
    }

    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST): T {
        TODO("Not yet implemented")
    }

    override fun visitPairTypeAST(type: TypeAST.PairTypeAST): T {
        TODO("Not yet implemented")
    }

    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST): T {
        TODO("Not yet implemented")
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST): T {
        TODO("Not yet implemented")
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST): T {
        TODO("Not yet implemented")
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST): T {
        TODO("Not yet implemented")
    }

    override fun visitNewPairAST(newPair: NewPairAST): T {
        TODO("Not yet implemented")
    }

    override fun visitPairElemAST(pairElem: PairElemAST): T {
        TODO("Not yet implemented")
    }
}