package wacc_05.code_generation

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.ast_structure.ASTVisitor

class TranslatorVisitor: ASTVisitor<Unit> {
    override fun visitProgramAST(prog: ProgramAST) {
        TODO("Not yet implemented")
    }

    override fun visitFunctionAST(func: FunctionAST) {
        TODO("Not yet implemented")
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

    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        TODO("Not yet implemented")
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        TODO("Not yet implemented")
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        TODO("Not yet implemented")
    }
}