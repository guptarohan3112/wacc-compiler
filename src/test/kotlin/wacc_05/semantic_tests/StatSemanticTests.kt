package wacc_05.semantic_tests

import io.mockk.*
import org.junit.Test

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.PairElemAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class StatSemanticTests {

    var intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier

    var st: SymbolTable = SymbolTable(null)
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

    @Test
    fun assignASTLHSValidIdentCheck() {
        st.add("int", intType)
        st.add("x", intType)

        StatementAST.AssignAST(AssignLHSAST("x"), ExprAST.IntLiterAST("+", "3")).check(st, seh)
    }

    @Test
    fun assignASTLHSValidPairElemCheck() {
        st.add("int", intType)
        st.add("x", TypeIdentifier.PairIdentifier(intType, intType))

        StatementAST.AssignAST(
            AssignLHSAST(PairElemAST(ExprAST.IdentAST("x"))),
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)
    }

    @Test
    fun assignASTLHSValidArrayElemCheck() {
        st.add("int", intType)
        st.add("x", TypeIdentifier.ArrayIdentifier(intType, 4))

        StatementAST.AssignAST(
            AssignLHSAST(ExprAST.ArrayElemAST("x", arrayListOf(ExprAST.IntLiterAST("+", "3")))),
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)
    }

    @Test
    fun assignASTLHSIdentNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier("x") } just Runs

        StatementAST.AssignAST(
            AssignLHSAST("x"),
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.invalidIdentifier("x") }
    }

    @Test
    fun assignASTLHSPairNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any()) } just runs

        StatementAST.AssignAST(
            AssignLHSAST(PairElemAST(ExprAST.IdentAST("x"))),
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.invalidIdentifier("x") }
    }

    @Test
    fun assignASTLHSArrayNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any())} just runs

        StatementAST.AssignAST(
            AssignLHSAST(ExprAST.ArrayElemAST("x", arrayListOf(ExprAST.IntLiterAST("+", "3")))),
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)

        verify(exactly = 1) { seh.invalidIdentifier("x") }
    }

    @Test
    fun assignASTRHSIncorrectTypeCheck() {
        st.add("int", intType)
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.AssignAST(
            AssignLHSAST("x"),
            ExprAST.CharLiterAST("c")
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }
}