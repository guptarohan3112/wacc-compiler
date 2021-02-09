package wacc_05.semantic_tests

import io.mockk.*
import org.junit.Test
import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

open class ExprSemanticTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BoolIdentifier

    val st: SymbolTable = SymbolTable(null)
    val seh: SemanticErrorHandler = mockk()

    @Test
    fun varIdentPresentCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        ExprAST.IdentAST("x").check(st, seh)
    }

    @Test
    fun varIdentNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any()) } just runs

        ExprAST.IdentAST("x").check(st, seh)

        verify(exactly = 1) { seh.invalidIdentifier("x") }
    }
}