package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(
    val functionList: ArrayList<FunctionAST>,
    val stat: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        for (func in functionList) {
            func.check(st, errorHandler)
        }

        stat.check(st, errorHandler)

    }

}