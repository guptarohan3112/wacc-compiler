package wacc_05.graph_colouring

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.AST
import wacc_05.code_generation.utilities.Operand
import wacc_05.code_generation.utilities.Register
import wacc_05.code_generation.utilities.Registers

class InterferenceGraph {

    companion object DefaultReg{
        private val defaultReg: Register = Register(-1)
    }

    private val listOfNodes: ArrayList<GraphNode> = ArrayList()

    fun getNodes(): ArrayList<GraphNode> {
        return listOfNodes
    }

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
        val allRegisters: ArrayList<Register> = Registers.allRegisters()

        for (node in listOfNodes) {
            node.setOperand(defaultReg)
        }

        for (node in listOfNodes) {
            colourNode(node, HashSet(), allRegisters)
        }
    }

    private fun colourNode(node: GraphNode, opsInUse: HashSet<Register>, allRegisters: ArrayList<Register>) {
        for (neighbour in node.getNeighbours()) {
            if (neighbour.getRegister() != defaultReg) {
                opsInUse.add(neighbour.getRegister())
            }
        }

        val notInUse = allRegisters.subtract(opsInUse)

        if (notInUse.isNotEmpty()) {
            node.setOperand(notInUse.elementAt(0))
        }
    }

    fun regsInUse(ctx: ParserRuleContext): ArrayList<Register> {
        val regsInUse: ArrayList<Register> = ArrayList()
        val currentLineNo = ctx.getStart().line
        for (gNode in listOfNodes) {
            if (gNode.variableActive(currentLineNo)) {
                regsInUse.add(gNode.getRegister())
            }
        }
        return regsInUse
    }
}
