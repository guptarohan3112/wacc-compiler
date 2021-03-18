package wacc_05.code_generation.utilities

import wacc_05.ast_structure.StatementAST
import wacc_05.graph_colouring.GraphNode
import wacc_05.graph_colouring.InterferenceGraph

class StackSizeVisitor {

    private var stackSize: Int = 0

    fun getStackSize(stat: StatementAST, graph: InterferenceGraph): Int {
        visitStat(stat, graph)
        return stackSize
    }

    private fun visitStat(stat: StatementAST, graph: InterferenceGraph) {
        return when (stat) {
            is StatementAST.DeclAST -> {
                incrementStackSizeIfNecessary(stat, graph)
            }
            is StatementAST.SequentialAST -> {
                visitStat(stat.stat1, graph)
                visitStat(stat.stat2, graph)
            }
            else -> {
                return
            }
        }
    }

    private fun incrementStackSizeIfNecessary(stat: StatementAST.DeclAST, graph: InterferenceGraph) {
        val size: Int = stat.type.getType().getStackSize()
        val correctNode: GraphNode? = graph.findNode(stat.varName)
        if (correctNode != null && (correctNode.getRegister()) != Register(-1)) {
            return
        } else {
            stackSize += size
        }
    }

}