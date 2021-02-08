package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(
    private val functionList: ArrayList<FunctionAST>,
    private val stat: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        // Check validity of each function
        for (func in functionList) {
            func.check(st, errorHandler)
        }
        // Check validity of statement
        stat.check(st, errorHandler)
    }

}