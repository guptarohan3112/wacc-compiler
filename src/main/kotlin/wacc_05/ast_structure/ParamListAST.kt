package wacc_05.ast_structure

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import java.util.*
import kotlin.collections.ArrayList

class ParamListAST(private val paramList: ArrayList<ParamAST>) : AST {

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        for (i in 0 until paramList.size) {
            paramList[i].check((ctx as WaccParser.ParamListContext).param(i), st, errorHandler)
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
