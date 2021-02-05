package wacc_05

import antlr.WaccParser
import antlr.WaccParserBaseVisitor
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.ast_structure.assignment_ast.ArrayLiterAST

class Visitor : WaccParserBaseVisitor<AST>() {

    override fun visitStatSkip(ctx: WaccParser.StatSkipContext): StatementAST {
        return StatementAST.SkipAST
    }

    override fun visitStatDeclaration(ctx: WaccParser.StatDeclarationContext): StatementAST {
        return StatementAST.DeclAST(visitType(ctx.type()), ctx.IDENT().text, visitAssignRHS(ctx.assignRHS()))
    }

    override fun visitAssignRHS(ctx: WaccParser.AssignRHSContext): AssignRHSAST {
        // TODO - return purely for compilation purposes
        return ArrayLiterAST(ArrayList())
    }

    override fun visitType(ctx: WaccParser.TypeContext): TypeAST {
        // TODO
        return TypeAST("")
    }

}