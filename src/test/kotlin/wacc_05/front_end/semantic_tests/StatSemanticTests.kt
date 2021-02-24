package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import io.mockk.*
import org.junit.Test

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.front_end.ASTVisitor
import wacc_05.front_end.SemanticVisitor
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

    var visitor: ASTVisitor<Unit> = SemanticVisitor(st, seh)

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        visitor.visit(StatementAST.SkipAST)
    }

    @Test
    fun readASTIntCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        val read = StatementAST.ReadAST(
            WaccParser.StatReadContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        read.st = st
        visitor.visitReadAST(read)
    }

    @Test
    fun readASTCharCheck() {
        st.add("char", charType)
        st.add("x", VariableIdentifier(charType))

        val read = StatementAST.ReadAST(
            WaccParser.StatReadContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        read.st = st
        visitor.visitReadAST(read)
    }

    @Test
    fun readASTInvalidReadTypeCheck() {
        st.add("bool", boolType)
        st.add("x", VariableIdentifier(boolType))

        every { seh.invalidReadType(any(), any()) } just runs

        val read = StatementAST.ReadAST(
            WaccParser.StatReadContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        read.st = st
        visitor.visitReadAST(read)

        verify(exactly = 1) { seh.invalidReadType(any(), boolType) }
    }

    @Test
    fun freeASTPairTypeCheck() {
        val identifier = TypeIdentifier.PairIdentifier(intType, intType)
        st.add("int", intType)
        st.add("x", VariableIdentifier(identifier))

        val free = StatementAST.FreeAST(
            WaccParser.StatFreeContext(WaccParser.StatContext()),
            ExprAST.IdentAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        free.st = st
        visitor.visitFreeAST(free)
    }

    @Test
    fun freeASTArrayTypeCheck() {
        val identifier = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier(identifier))

        val free = StatementAST.FreeAST(
            WaccParser.StatFreeContext(WaccParser.StatContext()),
            ExprAST.IdentAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        free.st = st
        visitor.visitFreeAST(free)
    }

    @Test
    fun freeASTInvalidFreeTypeCheck() {
        // anything not a pair or array type is an invalid free type
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        every { seh.invalidFreeType(any(), any()) } just runs

        val free = StatementAST.FreeAST(
            WaccParser.StatFreeContext(WaccParser.StatContext()),
            ExprAST.IdentAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                "x"
            )
        )

        free.st = st
        visitor.visitFreeAST(free)

        verify(exactly = 1) { seh.invalidFreeType(any(), intType) }
    }

    @Test
    fun returnASTValidReturnType() {
        // we recreate this just by giving the return ast a symbol table with the desired return type
        // in it

        st.add("bool", boolType)
        childSt.add("returnType", boolType)

        val ret = StatementAST.ReturnAST(
            WaccParser.StatReturnContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST("true")
        )

        ret.st = childSt
        visitor.visitReturnAST(ret)
    }

    @Test
    fun returnASTInvalidReturnType() {
        st.add("bool", boolType)

        every { seh.invalidReturnType(any()) } just runs

        val ret = StatementAST.ReturnAST(
            WaccParser.StatReturnContext(WaccParser.StatContext()),
            ExprAST.IntLiterAST("+", "3")
        )

        ret.st = childSt
        visitor.visitReturnAST(ret)

        verify(exactly = 1) { seh.invalidReturnType(any()) }
    }

    @Test
    fun exitASTValidCheck() {
        // an exit statement is valid if its expression is of integer type
        st.add("int", intType)

        val exit = StatementAST.ExitAST(
            WaccParser.StatExitContext(WaccParser.StatContext()),
            ExprAST.IntLiterAST("+", "0")
        )

        exit.st = st
        visitor.visitExitAST(exit)
    }

    @Test
    fun exitASTValidExprCheck() {
        st.add("int", intType)

        val exit = StatementAST.ExitAST(
            WaccParser.StatExitContext(WaccParser.StatContext()),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "3"),
                ExprAST.IntLiterAST("+", "4"),
                "+"
            )
        )

        exit.st = st
        visitor.visitExitAST(exit)
    }

    @Test
    fun exitASTInvalidTypeCheck() {
        st.add("char", charType)

        every { seh.invalidExitType(any(), any()) } just runs

        val exit = StatementAST.ExitAST(
            WaccParser.StatExitContext(WaccParser.StatContext()),
            ExprAST.CharLiterAST("c")
        )

        exit.st = st
        visitor.visitExitAST(exit)

        verify(exactly = 1) { seh.invalidExitType(any(), charType) }
    }
}