package wacc_05.semantic_tests

import io.mockk.mockk
import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

open class ExprSemanticTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BoolIdentifier

    val st: SymbolTable = SymbolTable(null)
    val seh: SemanticErrorHandler = mockk()
}