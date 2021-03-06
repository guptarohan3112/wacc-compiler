package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ASTVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class NewPairAST(ctx: WaccParser.NewPairContext, val fst: ExprAST, val snd: ExprAST) : AssignRHSAST(ctx) {

    override fun getType(): TypeIdentifier {
        return TypeIdentifier.PairIdentifier(fst.getType(), snd.getType())
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitNewPairAST(this)
    }
}