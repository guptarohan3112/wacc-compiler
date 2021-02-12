package wacc_05.ast_structure

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable

interface AST {
    // Function that applies semantic checks
    fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors)

}