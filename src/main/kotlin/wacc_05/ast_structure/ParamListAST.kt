package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.ParamIdentifier

class ParamListAST(val paramList: ArrayList<ParamAST>) : AST() {

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
