package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ParamListAST(private val paramList: ArrayList<ParamAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        for (param in paramList) {
            param.check(st, errorHandler)
        }
    }

}
