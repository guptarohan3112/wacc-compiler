package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(val functionList : ArrayList<FunctionAST>,
                 val statementList : ArrayList<StatementAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        for (func in functionList) {
            func.check(st, errorHandler)
        }

        // TODO this could just pass in statementList and do:
        //  statementList.check(st, errorHandler)
        for (stat in statementList) {
            stat.check(st, errorHandler)
        }

    }

}