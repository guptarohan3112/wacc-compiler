package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.AST
import wacc_05.code_generation.utilities.Register
import wacc_05.graph_colouring.GraphNode
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

abstract class AssignRHSAST : AST() {

    private var dest: Register? = null

    private var graphNode: GraphNode? = null

    abstract fun getType(): TypeIdentifier

    fun getStackSize(): Int {
        return getType().getStackSize()
    }

    fun getDestReg(): Register {
        return graphNode!!.getRegister()
    }

    fun setDestReg(reg: Register) {
        this.dest = reg
    }

    fun clearDestReg() {
        this.dest = null
    }

    fun getGraphNode(): GraphNode {
        return graphNode!!
    }

    fun setGraphNode(graphNode: GraphNode) {
        this.graphNode = graphNode
    }

}