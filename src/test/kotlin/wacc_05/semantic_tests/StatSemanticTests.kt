package wacc_05.semantic_tests

import antlr.WaccParser
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

    val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.INT_TYPE
    val charType: TypeIdentifier.CharIdentifier = TypeIdentifier.CHAR_TYPE
    val boolType: TypeIdentifier.BoolIdentifier = TypeIdentifier.BOOL_TYPE

    var st: SymbolTable = SymbolTable(null)
    var childSt: SymbolTable = SymbolTable(st)
    var seh: SemanticErrors = mockk()

    val statCtx = WaccParser.StatContext()

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(null, st, seh)
    }

    @Test
    fun readASTIntCheck() {
        val readCtx = WaccParser.StatReadContext(statCtx)
        val assignLHSCtx = WaccParser.AssignLHSContext(readCtx, 0)

        readCtx.addChild(assignLHSCtx)

        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        StatementAST.ReadAST(AssignLHSAST("x")).check(readCtx, st, seh)
    }

    @Test
    fun readASTCharCheck() {
        val readCtx = WaccParser.StatReadContext(statCtx)
        val assignLHSCtx = WaccParser.AssignLHSContext(readCtx, 0)

        readCtx.addChild(assignLHSCtx)

        st.add("char", charType)
        st.add("x", VariableIdentifier(charType))

        StatementAST.ReadAST(AssignLHSAST("x")).check(readCtx, st, seh)
    }

    @Test
    fun readASTInvalidReadTypeCheck() {
        val readCtx = WaccParser.StatReadContext(statCtx)
        val assignLHSCtx = WaccParser.AssignLHSContext(readCtx, 0)

        readCtx.addChild(assignLHSCtx)

        st.add("bool", boolType)
        st.add("x", VariableIdentifier(boolType))

        every { seh.invalidReadType(any(), any()) } just runs

        StatementAST.ReadAST(AssignLHSAST("x")).check(readCtx, st, seh)

        verify(exactly = 1) { seh.invalidReadType(any(), boolType) }
    }

    @Test
    fun freeASTPairTypeCheck() {
        val freeContext = WaccParser.StatFreeContext(statCtx)
        val exprContext = WaccParser.ExprContext(freeContext, 0)

        freeContext.addChild(exprContext)

        val identifier = TypeIdentifier.PairIdentifier(intType, intType)
        st.add("int", intType)
        st.add("x", VariableIdentifier(identifier))

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(freeContext, st, seh)
    }

    @Test
    fun freeASTArrayTypeCheck() {
        val freeContext = WaccParser.StatFreeContext(statCtx)
        val exprContext = WaccParser.ExprContext(freeContext, 0)

        freeContext.addChild(exprContext)

        val identifier = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier(identifier))

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(freeContext, st, seh)
    }

    @Test
    fun freeASTInvalidFreeTypeCheck() {
        val freeContext = WaccParser.StatFreeContext(statCtx)
        val exprContext = WaccParser.ExprContext(freeContext, 0)

        freeContext.addChild(exprContext)

        // anything not a pair or array type is an invalid free type
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        every { seh.invalidFreeType(any(), any()) } just runs

        StatementAST.FreeAST(ExprAST.IdentAST("x")).check(freeContext, st, seh)

        verify(exactly = 1) { seh.invalidFreeType(any(), intType) }
    }

    @Test
    fun returnASTValidReturnType() {
        // we recreate this just by giving the return ast a symbol table with the desired return type
        // in it

        val returnContext = WaccParser.StatReturnContext(statCtx)
        val exprContext = WaccParser.ExprContext(returnContext, 0)

        returnContext.addChild(exprContext)

        st.add("bool", boolType)
        childSt.add("returnType", boolType)

        StatementAST.ReturnAST(ExprAST.BoolLiterAST("true")).check(returnContext, childSt, seh)
    }

    @Test
    fun returnASTInvalidReturnType() {
        val returnContext = WaccParser.StatReturnContext(statCtx)
        val exprContext = WaccParser.ExprContext(returnContext, 0)

        returnContext.addChild(exprContext)

        st.add("bool", boolType)

        every { seh.invalidReturnType(any()) } just runs

        StatementAST.ReturnAST(ExprAST.IntLiterAST("+", "3")).check(returnContext, childSt, seh)

        verify(exactly = 1) { seh.invalidReturnType(any()) }
    }

    @Test
    fun exitASTValidCheck() {
        val exitContext = WaccParser.StatExitContext(statCtx)
        val exprContext = WaccParser.ExprContext(exitContext, 0)

        exitContext.addChild(exprContext)

        // an exit statement is valid if its expression is of integer type
        st.add("int", intType)

        StatementAST.ExitAST(ExprAST.IntLiterAST("+", "0")).check(exitContext, st, seh)
    }

    @Test
    fun exitASTValidExprCheck() {
        val exitContext = WaccParser.StatExitContext(statCtx)
        val exprContext = WaccParser.ExprContext(exitContext, 0)

        exitContext.addChild(exprContext)

        st.add("int", intType)
        StatementAST.ExitAST(
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "3"),
                ExprAST.IntLiterAST("+", "4"),
                "+"
            )
        ).check(exitContext, st, seh)
    }

    @Test
    fun exitASTInvalidTypeCheck() {
        val exitContext = WaccParser.StatExitContext(statCtx)
        val exprContext = WaccParser.ExprContext(exitContext, 0)

        exitContext.addChild(exprContext)

        st.add("char", charType)

        every { seh.invalidExitType(any(), any()) } just runs

        StatementAST.ExitAST(ExprAST.CharLiterAST("c")).check(exitContext, st, seh)

        verify(exactly = 1) { seh.invalidExitType(any(), charType) }
    }
}