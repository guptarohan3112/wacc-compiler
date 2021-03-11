package wacc_05.graph_colouring

class InterferenceGraph {

    // Index used to indicate live range of a graph node in this graph
    // This is incremented every time you move onto the next line
    // OR incremented every time you refer to or declare a new variable (we only care about variables)
    private val index: Int = 0

    private val listOfNodes: ArrayList<GraphNode> = ArrayList()

    fun formGraph() {
        // set the neighbours of the nodes to form the interference graph
    }

    fun findNode(name: String) : GraphNode? {
        // TODO: To be implemented
        return null
    }

    fun addNode(graphNode: GraphNode) {
        listOfNodes.add(graphNode)
    }

    fun colourgraph() {
        // TODO: Colours the graph by assigning registers/addressing modes using a greedy approach
    }
}
