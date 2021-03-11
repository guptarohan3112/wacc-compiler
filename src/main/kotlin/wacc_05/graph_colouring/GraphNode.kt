package wacc_05.graph_colouring

import wacc_05.ast_structure.AST
import wacc_05.code_generation.utilities.Register

class GraphNode(private val ast: AST) {
    // the register that will be allocated to this node during graph colouring
    private var register: Register? = null

    // neighbouring nodes in the graph
    private val neighbours: ArrayList<GraphNode> = ArrayList()

    fun addNeighbour(neighbour: GraphNode) {
        neighbours.add(neighbour)
    }

    fun setRegister(reg: Register) {
        this.register = reg
    }

    fun getRegister(): Register {
        return register!!
    }
}