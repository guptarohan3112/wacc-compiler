package wacc_05.graph_colouring

import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register
import wacc_05.code_generation.utilities.Registers

class InterferenceGraph {

    companion object {
        private val defaultReg: Register = Register(-1)
    }

    // Index used to indicate live range of a graph node in this graph
    // This is incremented every time you move onto the next line
    // OR incremented every time you refer to or declare a new variable (we only care about variables)
    private var index: Int = 0

    private val listOfNodes: ArrayList<GraphNode> = ArrayList()


    fun formGraph() {
        // set the neighbours of the nodes to form the interference graph
        for (node in listOfNodes) {
            for (other in listOfNodes) {
                if (node != other && node.overlapsWith(other)) {
                    node.addNeighbour(other)
                    other.addNeighbour(node)
                }
            }
        }
    }

    fun findNode(name: String): GraphNode? {
        for (node in this.listOfNodes) {
            if (node.getIdent() == name) {
                return node
            }
        }
        return null
    }

    fun addNode(graphNode: GraphNode) {
        listOfNodes.add(graphNode)
    }

    fun colourGraph() {
        // TODO: Colours the graph by assigning registers/addressing modes using a greedy approach
        val allRegisters: ArrayList<Register> = Registers.allRegisters()

        for(node in listOfNodes) {
            node.setRegister(defaultReg)
        }

        for(node in listOfNodes) {
            colourNode(node, HashSet(), allRegisters)
        }
    }

    private fun colourNode(node: GraphNode, opsInUse: HashSet<Register>, allRegisters: ArrayList<Register>) {
        for(neighbour in node.getNeighbours()) {
            if(neighbour.getRegister() == defaultReg) {
                colourNode(neighbour, opsInUse, allRegisters)
            } else {
                opsInUse.add(neighbour.getRegister())
            }
        }

        val notInUse = allRegisters.subtract(opsInUse)

        if(notInUse.isNotEmpty()) {
            node.setRegister(notInUse.elementAt(0))
        }
    }

    fun incrementIndex() {
        index++
    }

    fun getIndex(): Int {
        return index
    }
}
