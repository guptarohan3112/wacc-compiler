package wacc_05.graph_colouring

import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register

class GraphNode(private var startIndex: Int, private var ident: String = "") {
    // the register that will be allocated to this node during graph colouring
    // Make an operand to deal with variables that end up going on the stack?
    private var operand: Operand? = null

    // Start and end index which defines the live range for a graph node
//    private var startIndex: Int = 0
    private var endIndex: Int = startIndex


    // neighbouring nodes in the graph
    private val neighbours: HashSet<GraphNode> = HashSet()

    fun addNeighbour(neighbour: GraphNode) {
        neighbours.add(neighbour)
    }

    fun setIdentifier(identifier: String) {
        this.ident = identifier
    }

    fun setOperand(operand: Operand) {
        this.operand = operand
    }

    fun getOperand(): Operand {
        return operand!!
    }

    private fun getStartIndex(): Int {
        return startIndex
    }

    fun updateEndIndex(endIndex: Int) {
        this.endIndex = endIndex
    }

    fun getEndIndex(): Int {
        return endIndex
    }

    fun getIdent(): String {
        return ident
    }

    fun getNeighbours(): HashSet<GraphNode> {
        return neighbours
    }

    fun overlapsWith(node: GraphNode): Boolean {
        val thisStart: Int = this.startIndex
        val thatStart: Int = node.getStartIndex()
        val thisEnd: Int = this.endIndex
        val thatEnd: Int = node.getEndIndex()

        return if (thisStart < thatStart) {
            thisEnd >= thatStart
        } else {
            thatEnd >= thisStart
        }
    }
}