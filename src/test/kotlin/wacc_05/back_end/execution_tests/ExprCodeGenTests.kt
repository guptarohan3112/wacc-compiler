package wacc_05.back_end.execution_tests

import io.mockk.*
import wacc_05.front_end.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

open class ExprCodeGenTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.INT_TYPE
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CHAR_TYPE
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BOOL_TYPE

    val st: SymbolTable = SymbolTable(null)
    val seh: SemanticErrorHandler = mockk()

//    @Test
//    fun varIdentPresentCheck() {
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(intType))
//
//        ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(), 0),"x").check(st, seh)
//    }
//
//    @Test
//    fun varIdentNotPresentCheck() {
//        st.add("int", intType)
//
//        every { seh.invalidIdentifier(any(), any()) } just runs
//
//        ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(),0),"x").check(st, seh)
//
//        verify(exactly = 1) { seh.invalidIdentifier(any(), "x") }
//    }
}