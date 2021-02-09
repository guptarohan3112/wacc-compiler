package wacc_05.semantic_tests

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class DeclASTTests : StatSemanticTests() {
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
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        every { seh.repeatVariableDeclaration("x") } just Runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.repeatVariableDeclaration("x") }
    }

    @Test
    fun declASTKeywordClash() {
        st.add("int", intType)

        every { seh.repeatVariableDeclaration(any()) } just Runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "int",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.repeatVariableDeclaration("int") }
    }

    @Test
    fun declASTDifferentTypes() {
        st.add("int", intType)
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just Runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.CharLiterAST("c")
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }
}