package wacc_05.ast_structure

import wacc_05.code_generation.instructions.Instruction
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ParamAST(
    val type: TypeAST,
    val name: String
) : AST() {

    override fun translate(): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitParamAST(this)
    }

    override fun toString(): String {
        return name
    }

    fun getType(st: SymbolTable): TypeIdentifier {
        return type.getType(st)
    }

}
