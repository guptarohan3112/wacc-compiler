package wacc_05.back_end.execution_tests

import io.mockk.mockk
import wacc_05.front_end.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

open class StatCodeGenTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.INT_TYPE
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CHAR_TYPE
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BOOL_TYPE

    var st: SymbolTable = SymbolTable(null)
    var childSt: SymbolTable = SymbolTable(st)
    var seh: SemanticErrors = mockk()

}