package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.ASTVisitor
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class PairElemAST(val ctx: WaccParser.PairElemContext, val elem: ExprAST, val isFst: Boolean) : AssignRHSAST() {

    override fun getType(): TypeIdentifier {
        val pairType = elem.getType()

        return if (pairType is TypeIdentifier.PairIdentifier) {
            if (isFst) {
                pairType.getFstType()
            } else {
                pairType.getSndType()
            }
        } else {
            pairType
        }
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitPairElemAST(this)
    }
}