package wacc_05.ast_structure

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(
    private val context: WaccParser.ProgContext,
    private val functionList: ArrayList<FunctionAST>,
    private val stat: StatementAST
) : AST {

    override fun check(ctx: ParserRuleContext? /* unused */, st: SymbolTable, errorHandler: SemanticErrors) {

        // preliminary pass through the function list to add all function
        // identifiers to the symbol table
        for (i in 0 until functionList.size) {
            functionList[i].preliminaryCheck(context.func(i), st, errorHandler)
        }

        for (i in 0 until functionList.size) {
            functionList[i].check(context.func(i), st, errorHandler)
        }

        // Check validity of statement
        stat.check(context.stat(), st, errorHandler)
    }

}