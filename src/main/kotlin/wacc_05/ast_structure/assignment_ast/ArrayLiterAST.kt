package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ASTVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.graph_colouring.GraphNode
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ArrayLiterAST(ctx: WaccParser.ArrayLitContext, val elems: ArrayList<ExprAST>) :
    AssignRHSAST(ctx) {

    private var sizeGraphNode: GraphNode? = null

    override fun getType(): TypeIdentifier {
        return if (elems.size == 0) {
            // If the array literal is empty, the element type could be any type
            TypeIdentifier.GENERIC
        } else {
            // Otherwise, determine the type of the first element and create an ArrayIdentifier
            TypeIdentifier.ArrayIdentifier(elems[0].getType(), elems.size)
        }
    }

    fun elemsLength(): Int {
        return elems.size
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitArrayLiterAST(this)
    }

    fun setSizeGraphNode(sizeGraphNode: GraphNode) {
        this.sizeGraphNode = sizeGraphNode
    }

    fun getSizeGraphNode(): GraphNode {
        return sizeGraphNode!!
    }
}