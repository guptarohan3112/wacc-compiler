package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.ParamIdentifier

class ParamListAST(private val paramList: ArrayList<ParamAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        for (param in paramList) {
            param.check(st, errorHandler)
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitParamListAST(this)
    }

    fun getParams(st: SymbolTable): ArrayList<ParamIdentifier> {
        val list: ArrayList<ParamIdentifier> = ArrayList()

        for (param in paramList) {
            list.add(ParamIdentifier(param.getType(st)))
        }

        return list
    }

}
