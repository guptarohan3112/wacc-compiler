package wacc_05.code_generation

import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class StackSizeVisitor : ASTBaseVisitor<Unit>() {

    private var stackSize: Int = 0

    val INT_SIZE: Int = 4
    val CHAR_SIZE: Int = 1
    val BOOL_SIZE: Int = 1
    val ARR_SIZE: Int = 4
    val STRING_SIZE: Int = 4
    val PAIR_SIZE: Int = 4

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        visit(begin.stat)
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        val size: Int = getSizeOfType(decl.type.getType())
        stackSize += size
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

}