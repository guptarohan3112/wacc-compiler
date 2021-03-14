package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ForLoopTests : StatSemanticTests() {

    private val statForContext = WaccParser.StatForContext(WaccParser.StatContext())
    private val statDeclContext = WaccParser.StatDeclarationContext(WaccParser.StatContext())

    private val validLoopVarDecl: StatementAST.DeclAST = StatementAST.DeclAST(
        statDeclContext,
        TypeAST.BaseTypeAST(
            WaccParser.BaseTypeContext(WaccParser.StatContext(), 0),
            "int"
        ), "i",
        ExprAST.IntLiterAST("+", "0")
    )

    private val loopVarIncrement = StatementAST.AssignAST(
        WaccParser.StatAssignContext(WaccParser.StatContext()),
        AssignLHSAST(
            WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
            ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i")
        ),
        ExprAST.BinOpAST(
            WaccParser.ExprContext(WaccParser.StatContext(), 0),
            ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i"),
            ExprAST.IntLiterAST("+", "1"),
            "+"
        )
    )

    @Test
    fun forLoopValidCheck() {
        st.add("int", TypeIdentifier.INT_TYPE)
        val forLoop = StatementAST.ForAST(
            statForContext,
            validLoopVarDecl,
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i"),
                ExprAST.IntLiterAST("+", "5"),
                "<"
            ),
            loopVarIncrement,
            StatementAST.SkipAST
        )

        forLoop.st = st
        visitor.visitForAST(forLoop)
    }

    @Test
    fun forLoopNoDeclaration() {
        st.add("int", TypeIdentifier.INT_TYPE)

        every { seh.invalidDeclaration(any()) } just runs

        val forLoopWithRead = StatementAST.ForAST(
            statForContext,
            StatementAST.ReadAST(
                WaccParser.StatReadContext(WaccParser.StatContext()),
                AssignLHSAST(
                    WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                    ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i")
                )
            ),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i"),
                ExprAST.IntLiterAST("+", "2"),
                "<="
            ),
            loopVarIncrement,
            StatementAST.SkipAST
        )

        val sequential: StatementAST.SequentialAST =
            StatementAST.SequentialAST(validLoopVarDecl, forLoopWithRead)

        forLoopWithRead.st = st
        sequential.st = st
        visitor.visitSequentialAST(sequential)

        verify(exactly = 1) {
            seh.invalidDeclaration(any())
        }
    }

    @Test
    fun forLoopNoIntDeclaration() {
        st.add("int", TypeIdentifier.INT_TYPE)
        st.add("char", TypeIdentifier.CHAR_TYPE)

        every { seh.typeMismatch(any(), any(), any())} just runs

        val forLoopNoInt = StatementAST.ForAST(
            statForContext,
            StatementAST.DeclAST(
                statDeclContext,
                TypeAST.BaseTypeAST(
                    WaccParser.BaseTypeContext(WaccParser.StatContext(), 0),
                    "char"
                ), "c",
                ExprAST.CharLiterAST("a")
            ),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "c"),
                ExprAST.CharLiterAST("b"),
                "=="
            ),
            StatementAST.AssignAST(
                WaccParser.StatAssignContext(WaccParser.StatContext()),
                AssignLHSAST(
                    WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                    ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "c")
                ),
                ExprAST.CharLiterAST("b")
            ),
            StatementAST.SkipAST
        )

        forLoopNoInt.st = st
        visitor.visitForAST(forLoopNoInt)

        verify(exactly = 1) {
            seh.typeMismatch(any(), intType, charType)
        }
    }

    @Test
    fun forLoopNoComparison() {
        st.add("int", TypeIdentifier.INT_TYPE)

        every { seh.typeMismatch(any(), any(), any())} just runs

        val forLoopAddition = StatementAST.ForAST(
            statForContext,
            validLoopVarDecl,
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i"),
                ExprAST.IntLiterAST("+", "7"),
                "+"
            ),
            loopVarIncrement,
            StatementAST.SkipAST
        )

        forLoopAddition.st = st
        visitor.visitForAST(forLoopAddition)

        verify(exactly = 1) {
            seh.typeMismatch(any(), boolType, intType)
        }
    }

    @Test
    fun forLoopNoUpdate() {
        TODO("Implement this test")
    }
}