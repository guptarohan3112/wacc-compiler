package wacc_05.semantic_tests

import io.mockk.*
import org.junit.Test

import wacc_05.SemanticErrors
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

open class StatSemanticTests {

    var intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier

    var st: SymbolTable = SymbolTable(null)
    var seh: SemanticErrors = mockk()

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(st, seh)
    }

}