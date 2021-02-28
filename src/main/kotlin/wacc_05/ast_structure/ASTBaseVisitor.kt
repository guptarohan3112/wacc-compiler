package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.*

abstract class ASTBaseVisitor<T> {

    abstract fun visitProgramAST(prog: ProgramAST): T

    abstract fun visitFunctionAST(func: FunctionAST): T

    abstract fun visitParamListAST(list: ParamListAST): T

    abstract fun visitParamAST(param: ParamAST): T

    abstract fun visitSkipAST(skip: StatementAST.SkipAST): T

    abstract fun visitDeclAST(decl: StatementAST.DeclAST): T

    abstract fun visitAssignAST(assign: StatementAST.AssignAST): T

    abstract fun visitBeginAST(begin: StatementAST.BeginAST): T

    abstract fun visitReadAST(read: StatementAST.ReadAST): T

    abstract fun visitExitAST(exit: StatementAST.ExitAST): T

    abstract fun visitFreeAST(free: StatementAST.FreeAST): T

    abstract fun visitIfAST(ifStat: StatementAST.IfAST): T

    abstract fun visitPrintAST(print: StatementAST.PrintAST): T

    abstract fun visitReturnAST(ret: StatementAST.ReturnAST): T

    abstract fun visitSequentialAST(seq: StatementAST.SequentialAST): T

    abstract fun visitWhileAST(whileStat: StatementAST.WhileAST): T

    abstract fun visitIntLiterAST(liter: ExprAST.IntLiterAST): T

    abstract fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST): T

    abstract fun visitCharLiterAST(liter: ExprAST.CharLiterAST): T

    abstract fun visitStrLiterAST(liter: ExprAST.StrLiterAST): T

    abstract fun visitPairLiterAST(liter: ExprAST.PairLiterAST): T

    abstract fun visitIdentAST(ident: ExprAST.IdentAST): T

    abstract fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST): T

    abstract fun visitUnOpAST(unop: ExprAST.UnOpAST): T

    abstract fun visitBinOpAST(binop: ExprAST.BinOpAST): T

    abstract fun visitBaseTypeAST(type: TypeAST.BaseTypeAST): T

    abstract fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST): T

    abstract fun visitPairTypeAST(type: TypeAST.PairTypeAST): T

    abstract fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST): T

    abstract fun visitArrayLiterAST(arrayLiter: ArrayLiterAST): T

    abstract fun visitAssignLHSAST(lhs: AssignLHSAST): T

    abstract fun visitFuncCallAST(funcCall: FuncCallAST): T

    abstract fun visitNewPairAST(newPair: NewPairAST): T

    abstract fun visitPairElemAST(pairElem: PairElemAST): T
}