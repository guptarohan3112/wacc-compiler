package wacc_05.ast_structure

import antlr.BasicParser

open class StatementAST(ctx : BasicParser.StatContext){

}

class SkipAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

// AssignAST classes go here

class ReadAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class FreeAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class ReturnAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class ExitAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class PrintAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class PrintlnAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class IfAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {
//    val expr : ExprAST = new ExprAST(ctx.expr());
}

class WhileAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}

class BeginAST(ctx: BasicParser.StatContext) : StatementAST(ctx) {

}