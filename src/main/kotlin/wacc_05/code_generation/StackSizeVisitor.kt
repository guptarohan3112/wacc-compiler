package wacc_05.code_generation

import wacc_05.ast_structure.ASTBaseVisitor
//import wacc_05.ast_structure.FunctionAST
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class StackSizeVisitor /*: ASTBaseVisitor<Unit>()*/ {

    private var stackSize: Int = 0

    val INT_SIZE: Int = 4
    val CHAR_SIZE: Int = 1
    val BOOL_SIZE: Int = 1
    val ARR_SIZE: Int = 4
    val STRING_SIZE: Int = 4
    val PAIR_SIZE: Int = 4

    fun getStackSize(stat: StatementAST) : Int {
        visitStat(stat)
        return stackSize
    }

    private fun getSizeOfType(type: TypeIdentifier): Int {
        return when (type) {
            TypeIdentifier.INT_TYPE -> INT_SIZE
            TypeIdentifier.BOOL_TYPE -> BOOL_SIZE
            TypeIdentifier.CHAR_TYPE -> CHAR_SIZE
            TypeIdentifier.STRING_TYPE -> STRING_SIZE
            is TypeIdentifier.PairIdentifier -> PAIR_SIZE
            is TypeIdentifier.ArrayIdentifier -> ARR_SIZE
            else -> 0
        }
    }

    fun visitStat(stat: StatementAST) {
        return when (stat) {
            is StatementAST.DeclAST -> {
                val size: Int = getSizeOfType(stat.type.getType())
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

//    override fun visitBeginAST(begin: StatementAST.BeginAST) {
//        visit(begin.stat)
//    }
//
//    override fun visitAssignAST(assign: StatementAST.AssignAST) {
//        return
//    }
//
//    override fun visitReadAST(read: StatementAST.ReadAST) {
//        return
//    }
//
//    override fun visitExitAST(exit: StatementAST.ExitAST) {
//        return
//    }
//
//    override fun visitFreeAST(free: StatementAST.FreeAST) {
//        return
//    }
//
//    override fun visitIfAST(ifStat: StatementAST.IfAST) {
//        // Options:
//        // Allocate space on the stack for both branches at the beginning
//        // Determine which branch you will fall into and allocate stack space for that one branch
//        // Leave it up to the individual branch to deal with how much stack space they allocate (reference Compiler)
//        visit(ifStat.thenStat)
//        visit(ifStat.elseStat)
//    }
//
//    override fun visitPrintAST(print: StatementAST.PrintAST) {
//        return
//    }
//
//    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
//        return
//    }
//
//    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
//        visit(seq.stat1)
//        visit(seq.stat2)
//    }
//
//    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
//        visit(whileStat.body)
//    }
//
//    override fun visitFunctionAST(func: FunctionAST) {
//        visit(func.body)
//    }
//
//    override fun visitDeclAST(decl: StatementAST.DeclAST) {
//        val size: Int = getSizeOfType(decl.type.getType())
//        stackSize += size
//    }

}