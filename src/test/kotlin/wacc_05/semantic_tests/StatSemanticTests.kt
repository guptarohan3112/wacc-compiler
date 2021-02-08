package wacc_05.semantic_tests

import io.mockk.*
import org.junit.Test

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class StatSemanticTests {

    val st: SymbolTable = SymbolTable(null)

    var seh: SemanticErrors = mockk()

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(st, seh)
    }

    @Test
    fun declASTValidCheck() {
        st.add("int", TypeIdentifier.IntIdentifier())
        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"), "x",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)
    }

    @Test
    fun declASTRepeatDeclaration() {
        val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        every {seh.repeatVariableDeclaration("x")} just Runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.repeatVariableDeclaration("x") }
    }

    @Test
    fun declASTDifferentTypes() {
        val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
        val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier

        st.add("int", intType)
        st.add("char", charType)

        every{ seh.typeMismatch(any(), any()) } just Runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.CharLiterAST("c")
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }
}