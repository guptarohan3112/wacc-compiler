package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(
    private val functionList: ArrayList<FunctionAST>,
    private val stat: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

        for (func in functionList) {
            func.check(st, errorHandler)
        }

        stat.check(st, errorHandler)

    }

}