package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import io.mockk.*
import org.junit.Test
import wacc_05.front_end.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.ASTVisitor
import wacc_05.front_end.SemanticVisitor
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

open class ExprSemanticTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.INT_TYPE
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CHAR_TYPE
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BOOL_TYPE

    val st: SymbolTable = SymbolTable(null)
    val functionST: FunctionST = FunctionST()
    val seh: SemanticErrorHandler = mockk()

    val visitor: ASTVisitor<Unit> = SemanticVisitor(st, functionST, seh)

    @Test
    fun varIdentPresentCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        val ident = ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "x")

        ident.st = st
        visitor.visitIdentAST(ident)
    }

    @Test
    fun varIdentNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any(), any()) } just runs

        val ident = ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "x")

        ident.st = st
        visitor.visitIdentAST(ident)

        verify(exactly = 1) { seh.invalidIdentifier(any(), "x") }
    }
}