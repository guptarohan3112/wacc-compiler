package wacc_05.code_generation

import wacc_05.ast_structure.ASTBaseVisitor
//import wacc_05.ast_structure.FunctionAST
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class StackSizeVisitor : ASTBaseVisitor() {

    private var stackSize: Int = 0

    fun getStackSize(stat: StatementAST) : Int {
        visitStat(stat)
        return stackSize
    }

    private fun visitStat(stat: StatementAST) {
        return when (stat) {
            is StatementAST.DeclAST -> {
                val size: Int = stat.type.getType().getSize()
                stackSize += size
            }
            is StatementAST.WhileAST -> {
                visitStat(stat.body)
            }
            is StatementAST.SequentialAST -> {
                visitStat(stat.stat1)
                visitStat(stat.stat2)
            }
            is StatementAST.BeginAST -> {
                visitStat(stat.stat)
            }
            is StatementAST.IfAST -> {
                // Options:
                // Allocate space on the stack for both branches at the beginning
                // Determine which branch you will fall into and allocate stack space for that one branch
                // Leave it up to the individual branch to deal with how much stack space they allocate (reference Compiler)
                visitStat(stat.thenStat)
                visitStat(stat.elseStat)
            }
            else -> {
                return
            }
        }
    }
}