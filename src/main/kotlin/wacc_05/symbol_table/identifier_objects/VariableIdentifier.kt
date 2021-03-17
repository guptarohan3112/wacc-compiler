package wacc_05.symbol_table.identifier_objects

import wacc_05.graph_colouring.GraphNode

open class VariableIdentifier(private val type: TypeIdentifier) : IdentifierObject() {

    // Absolute stack address of a variable
    private var addr: Int = -1

    private var graphNode: GraphNode? = null

    // Field indicating whether a variable has been allocated on the stack
    private var stackAllocated: Boolean = false

    override fun getType(): TypeIdentifier {
        return type
    }

    fun setGraphNode(node: GraphNode) {
        this.graphNode = node
    }

    fun getGraphNode(): GraphNode {
        return graphNode!!
    }

    fun getAddr(): Int {
        return addr
    }

    fun setAddr(addr: Int) {
        this.addr = addr
    }

    fun isAllocated(): Boolean {
        return stackAllocated
    }

    fun allocatedNow() {
        this.stackAllocated = true
    }

}