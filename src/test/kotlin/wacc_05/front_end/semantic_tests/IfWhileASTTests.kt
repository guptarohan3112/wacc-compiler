package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST

class IfWhileASTTests : StatSemanticTests() {

    @Test
    fun ifASTValidCheck() {
        // the most basic of if statements as a check

        val ifStat = StatementAST.IfAST(
            WaccParser.StatIfContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        )

        ifStat.st = st
        visitor.visitIfAST(ifStat)
    }

    @Test
    fun ifASTValidExprCheck() {
        // checks a slightly more complicated expr

        val ifStat = StatementAST.IfAST(
            WaccParser.StatIfContext(WaccParser.StatContext()),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.BinOpAST(
                    WaccParser.ExprContext(WaccParser.StatContext(), 0),
                    ExprAST.IntLiterAST(intLitContext, "+", "3"),
                    ExprAST.IntLiterAST(intLitContext, "+", "3"),
                    "+"
                ),
                ExprAST.IntLiterAST(intLitContext, "+", "6"),
                "=="
            ),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        )

        ifStat.st = st
        visitor.visitIfAST(ifStat)
    }

    @Test
    fun nestedIfValidCheck() {
        // tests nested ifs for validity

        val ifStat = StatementAST.IfAST(
            WaccParser.StatIfContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.IfAST(
                WaccParser.StatIfContext(WaccParser.StatContext()),
                ExprAST.BoolLiterAST(boolLitContext, "false"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            ),
            StatementAST.IfAST(
                WaccParser.StatIfContext(WaccParser.StatContext()),
                ExprAST.BoolLiterAST(boolLitContext, "false"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            )
        )

        ifStat.st = st
        visitor.visitIfAST(ifStat)
    }

    @Test
    fun nestedIfInvalidCheck() {
        every { seh.typeMismatch(any(), any(), any()) } just runs

        val ifStat = StatementAST.IfAST(
            WaccParser.StatIfContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.IfAST(
                WaccParser.StatIfContext(WaccParser.StatContext()),
                ExprAST.IntLiterAST(intLitContext, "+", "3"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            ),
            StatementAST.IfAST(
                WaccParser.StatIfContext(WaccParser.StatContext()),
                ExprAST.CharLiterAST(charLitContext, "c"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            )
        )

        ifStat.st = st
        visitor.visitIfAST(ifStat)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
        verify(exactly = 1) { seh.typeMismatch(any(), boolType, charType) }
    }

    @Test
    fun ifASTInvalidExprCheck() {
        every { seh.typeMismatch(any(), any(), any()) } just runs

        val ifStat = StatementAST.IfAST(
            WaccParser.StatIfContext(WaccParser.StatContext()),
            ExprAST.IntLiterAST(intLitContext, "+", "3"),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        )

        ifStat.st = st
        visitor.visitIfAST(ifStat)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
    }

    @Test
    fun whileASTValidCheck() {
        // a very basic while loop check
        val whileStat = StatementAST.WhileAST(
            WaccParser.StatWhileContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.SkipAST
        )

        whileStat.st = st
        visitor.visitWhileAST(whileStat)
    }

    @Test
    fun whileASTValidExprCheck() {
        // a slightly more complex expr check

        val whileStat = StatementAST.WhileAST(
            WaccParser.StatWhileContext(WaccParser.StatContext()),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST(boolLitContext, "true"),
                ExprAST.BoolLiterAST(boolLitContext, "false"),
                "||"
            ),
            StatementAST.SkipAST
        )

        whileStat.st = st
        visitor.visitWhileAST(whileStat)
    }

    @Test
    fun nestedWhileValidCheck() {
        // a simple nested while loop test
        val whileStat = StatementAST.WhileAST(
            WaccParser.StatWhileContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.WhileAST(
                WaccParser.StatWhileContext(WaccParser.StatContext()),
                ExprAST.BoolLiterAST(boolLitContext, "false"),
                StatementAST.SkipAST
            )
        )

        whileStat.st = st
        visitor.visitWhileAST(whileStat)
    }

    @Test
    fun nestedWhileInvalidCheck() {
        // invalid nested while loop test

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val whileStat = StatementAST.WhileAST(
            WaccParser.StatWhileContext(WaccParser.StatContext()),
            ExprAST.BoolLiterAST(boolLitContext, "true"),
            StatementAST.WhileAST(
                WaccParser.StatWhileContext(WaccParser.StatContext()),
                ExprAST.IntLiterAST(intLitContext, "+", "3"),
                StatementAST.SkipAST
            )
        )

        whileStat.st = st
        visitor.visitWhileAST(whileStat)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
    }

    @Test
    fun whileASTInvalidExprCheck() {
        every { seh.typeMismatch(any(), any(), any()) } just runs

        val whileStat = StatementAST.WhileAST(
            WaccParser.StatWhileContext(WaccParser.StatContext()),
            ExprAST.CharLiterAST(charLitContext, "c"),
            StatementAST.SkipAST
        )

        whileStat.st = st
        visitor.visitWhileAST(whileStat)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, charType) }
    }
}