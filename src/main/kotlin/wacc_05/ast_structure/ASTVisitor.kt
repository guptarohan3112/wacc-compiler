package wacc_05.ast_structure

import wacc_05.ast_structure.assignment_ast.*

interface ASTVisitor<T> {

    fun visit(node: AST): T {
        return node.accept(this)
    }

    fun visitProgramAST(prog: ProgramAST): T

    fun visitFunctionAST(func: FunctionAST): T

    fun visitParamListAST(list: ParamListAST): T

    fun visitParamAST(param: ParamAST): T

    fun visitSkipAST(skip: StatementAST.SkipAST): T

    fun visitDeclAST(decl: StatementAST.DeclAST): T

    fun visitAssignAST(assign: StatementAST.AssignAST): T

    fun visitBeginAST(begin: StatementAST.BeginAST): T

    fun visitReadAST(read: StatementAST.ReadAST): T

    fun visitExitAST(exit: StatementAST.ExitAST): T

    fun visitFreeAST(free: StatementAST.FreeAST): T

    fun visitIfAST(ifStat: StatementAST.IfAST): T

    fun visitPrintAST(print: StatementAST.PrintAST): T

    fun visitReturnAST(ret: StatementAST.ReturnAST): T

    fun visitSequentialAST(seq: StatementAST.SequentialAST): T

    fun visitWhileAST(whileStat: StatementAST.WhileAST): T

    fun visitForAST(forLoop: StatementAST.ForAST): T

    fun visitIntLiterAST(liter: ExprAST.IntLiterAST): T

    fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST): T

    fun visitCharLiterAST(liter: ExprAST.CharLiterAST): T

    fun visitStrLiterAST(liter: ExprAST.StrLiterAST): T

    fun visitPairLiterAST(liter: ExprAST.PairLiterAST): T

    fun visitIdentAST(ident: ExprAST.IdentAST): T

    fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST): T

    fun visitUnOpAST(unop: ExprAST.UnOpAST): T

    fun visitBinOpAST(binop: ExprAST.BinOpAST): T

    fun visitBaseTypeAST(type: TypeAST.BaseTypeAST): T

    fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST): T

    fun visitPairTypeAST(type: TypeAST.PairTypeAST): T

    fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST): T

    fun visitArrayLiterAST(arrayLiter: ArrayLiterAST): T

    fun visitAssignLHSAST(lhs: AssignLHSAST): T

    fun visitFuncCallAST(funcCall: FuncCallAST): T

    fun visitNewPairAST(newPair: NewPairAST): T

    fun visitPairElemAST(pairElem: PairElemAST): T

    fun visitMapAST(mapAST: ExprAST.MapAST): T
}