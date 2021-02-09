package wacc_05.semantic_tests

import io.mockk.*
import org.junit.Test

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

open class StatSemanticTests {

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CharIdentifier
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BoolIdentifier

    var st: SymbolTable = SymbolTable(null)
    var seh: SemanticErrors = mockk()

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(st, seh)
    }

    @Test
    fun readASTIntCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        StatementAST.ReadAST(AssignLHSAST("x")).check(st, seh)
    }

    @Test
    fun readASTCharCheck() {
        st.add("char", charType)
        st.add("x", VariableIdentifier("x", charType))

        StatementAST.ReadAST(AssignLHSAST("x")).check(st, seh)
    }

    @Test
    fun readASTInvalidReadTypeCheck() {
        st.add("bool", boolType)
        st.add("x", VariableIdentifier("x", boolType))

        every { seh.invalidReadType(any()) } just runs

        StatementAST.ReadAST(AssignLHSAST("x")).check(st, seh)

        verify(exactly = 1) { seh.invalidReadType(boolType) }
    }

    @Test
    fun freeASTPairTypeCheck() {
        val identifier = TypeIdentifier.PairIdentifier(intType, intType)
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", identifier))

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(st, seh)
    }

    @Test
    fun freeASTArrayTypeCheck() {
        val identifier = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", identifier))

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(st, seh)
    }

    @Test
    fun freeASTInvalidFreeTypeCheck() {
        // anything not a pair or array type is an invalid free type
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        every { seh.invalidFreeType(any()) } just runs

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(st, seh)

        verify(exactly = 1) { seh.invalidFreeType(intType) }
    }

    @Test
    fun returnASTValidReturnType() {
        // we recreate this just by giving the return ast a symbol table with the desired return type
        // in it

        st.add("bool", boolType)

        StatementAST.ReturnAST(ExprAST.BoolLiterAST("true")).check(st, seh)
    }

    @Test
    fun returnASTInvalidReturnType() {
        st.add("bool", boolType)

        every { seh.invalidReturnType() } just runs

        StatementAST.ReturnAST(ExprAST.IntLiterAST("+", "3")).check(st, seh)

        verify(exactly = 1) { seh.invalidReturnType() }
    }

    @Test
    fun exitASTValidCheck() {
        // an exit statement is valid if its expression is of integer type
        st.add("int", intType)

        StatementAST.ExitAST(ExprAST.IntLiterAST("+", "0")).check(st, seh)
    }

    @Test
    fun exitASTValidExprCheck() {
        st.add("int", intType)
        StatementAST.ExitAST(
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "3"),
                ExprAST.IntLiterAST("+", "4"),
                "+"
            )
        ).check(st, seh)
    }

    @Test
    fun exitASTInvalidTypeCheck() {
        st.add("char", charType)

        every { seh.invalidExitType(any()) } just runs

        StatementAST.ExitAST(ExprAST.CharLiterAST("c")).check(st, seh)

        verify(exactly = 1) { seh.invalidExitType(charType) }
    }
}