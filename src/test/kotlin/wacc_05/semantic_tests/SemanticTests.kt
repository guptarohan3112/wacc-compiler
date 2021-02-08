package wacc_05.semantic_tests

import org.junit.Test
import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.SymbolTable

class SemanticTests {

    val st: SymbolTable = SymbolTable(null)
    val seh: SemanticErrorHandler = SemanticErrorHandler()

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(st, seh)
    }
}