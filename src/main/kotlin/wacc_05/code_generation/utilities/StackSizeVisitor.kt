package wacc_05.code_generation.utilities

import wacc_05.ast_structure.StatementAST

class StackSizeVisitor {

    private var stackSize: Int = 0

    fun getStackSize(stat: StatementAST): Int {
        visitStat(stat)
        return stackSize
    }

    private fun visitStat(stat: StatementAST) {
        return when (stat) {
            is StatementAST.DeclAST -> {
                val size: Int = stat.type.getType().getStackSize()
                stackSize += size
            }
            is StatementAST.SequentialAST -> {
                visitStat(stat.stat1)
                visitStat(stat.stat2)
            }
            else -> {
                return
            }
        }
    }
}