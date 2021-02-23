package wacc_05.front_end.semantic_tests

import antlr.WaccParser
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
        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(
                WaccParser.BaseTypeContext(WaccParser.StatContext(), 0),
                "int"
            ), "x",
            ExprAST.IntLiterAST("+", "3")
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun declASTRepeatDeclaration() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        every { seh.repeatVariableDeclaration(any(), "x") } just Runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.IntLiterAST("+", "3")
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.repeatVariableDeclaration(any(), "x") }
    }

    @Test
    fun declASTDifferentTypes() {
        st.add("int", intType)
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just Runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(
                WaccParser.BaseTypeContext(WaccParser.StatContext(), 0),
                "int"
            ),
            "x",
            ExprAST.CharLiterAST("c")
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }
}