package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import java.util.*
import kotlin.collections.ArrayList

class ParamListAST(private val paramList: ArrayList<ParamAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        for (param in paramList) {
            param.check(st, errorHandler)
        }
    }

    fun getParams(st: SymbolTable): ArrayList<ParamIdentifier> {
        val list: ArrayList<ParamIdentifier> = ArrayList()

        for (param in paramList) {
            list.add(ParamIdentifier(param.getType(st)))
        }

        return list
    }

}
