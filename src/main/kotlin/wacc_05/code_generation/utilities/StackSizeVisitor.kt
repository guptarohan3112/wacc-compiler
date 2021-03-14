package wacc_05.code_generation.utilities

import wacc_05.ast_structure.StatementAST
import wacc_05.graph_colouring.GraphNode
import wacc_05.graph_colouring.InterferenceGraph

class StackSizeVisitor {

    private var stackSize: Int = 0

//    fun getStackSize(graph: InterferenceGraph): Int {
//        val nodes = graph.getNodes()
//    }

    fun getStackSize(stat: StatementAST, graph: InterferenceGraph): Int {
        visitStat(stat, graph)
        return stackSize
    }

    private fun visitStat(stat: StatementAST, graph: InterferenceGraph) {
        return when (stat) {
            is StatementAST.DeclAST -> {
                val size: Int = stat.type.getType().getStackSize()
                incrementStackSizeIfNecessary(stat, size, graph)
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

    private fun incrementStackSizeIfNecessary(stat: StatementAST.DeclAST, size: Int, graph: InterferenceGraph) {
        val correctNode: GraphNode? = graph.findNode(stat.varName)
        if (correctNode != null && !correctNode.getRegister().equals(InterferenceGraph.DefaultReg)) {
            return
        } else {
            stackSize += size
        }
    }

}