package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ASTVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.graph_colouring.GraphNode
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class PairElemAST(ctx: WaccParser.PairElemContext, val elem: ExprAST, val isFst: Boolean) :
    AssignRHSAST(ctx) {

    private var pairLocation: GraphNode? = null

    fun setPairLocation(node: GraphNode) {
        this.pairLocation = node
    }

    fun getPairLocation(): GraphNode {
        return pairLocation!!
    }

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