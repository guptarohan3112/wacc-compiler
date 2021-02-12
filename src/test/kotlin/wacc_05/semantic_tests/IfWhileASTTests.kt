package wacc_05.semantic_tests

import antlr.WaccParser
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST

class IfWhileASTTests : StatSemanticTests() {

    val ifContext = WaccParser.StatIfContext(statCtx)
    val exprContext = WaccParser.ExprContext(ifContext, 0)

    init {
        ifContext.addChild(exprContext)
    }

    @Test
    fun ifASTValidCheck() {
        // the most basic of if statements as a check

        StatementAST.IfAST(
            ExprAST.BoolLiterAST("true"),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        ).check(ifContext, st, seh)
    }

    @Test
    fun ifASTValidExprCheck() {
        val exprContext1 = WaccParser.ExprContext(ifContext, 0)

        exprContext.addChild(exprContext1)
        exprContext.addChild(exprContext1)

        // checks a slightly more complicated expr

        StatementAST.IfAST(
            ExprAST.BinOpAST(
                ExprAST.BinOpAST(
                    ExprAST.IntLiterAST("+", "3"),
                    ExprAST.IntLiterAST("+", "3"),
                    "+"
                ),
                ExprAST.IntLiterAST("+", "6"),
                "=="
            ),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        ).check(ifContext, st, seh)
    }

    @Test
    fun nestedIfValidCheck() {
        // tests nested ifs for validity
        ifContext.addChild(ifContext)
        ifContext.addChild(ifContext)

        StatementAST.IfAST(
            ExprAST.BoolLiterAST("true"),
            StatementAST.IfAST(
                ExprAST.BoolLiterAST("false"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            ),
            StatementAST.IfAST(
                ExprAST.BoolLiterAST("false"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            )
        ).check(ifContext, st, seh)
    }

    @Test
    fun nestedIfInvalidCheck() {
        ifContext.addChild(ifContext)
        ifContext.addChild(ifContext)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.IfAST(
            ExprAST.BoolLiterAST("true"),
            StatementAST.IfAST(
                ExprAST.IntLiterAST("+", "3"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            ),
            StatementAST.IfAST(
                ExprAST.CharLiterAST("c"),
                StatementAST.SkipAST,
                StatementAST.SkipAST
            )
        ).check(ifContext, st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
        verify(exactly = 1) { seh.typeMismatch(any(), boolType, charType) }
    }

    @Test
    fun ifASTInvalidExprCheck() {
        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.IfAST(
            ExprAST.IntLiterAST("+", "3"),
            StatementAST.SkipAST,
            StatementAST.SkipAST
        ).check(ifContext, st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
    }

//    @Test
//    fun whileASTValidCheck() {
//        // a very basic while loop check
//        StatementAST.WhileAST(
//            ExprAST.BoolLiterAST("true"),
//            StatementAST.SkipAST
//        ).check(st, seh)
//    }
//
//    @Test
//    fun whileASTValidExprCheck() {
//        // a slightly more complex expr check
//
//        StatementAST.WhileAST(
//            ExprAST.BinOpAST(
//                ExprAST.BoolLiterAST("true"),
//                ExprAST.BoolLiterAST("false"),
//                "||"
//            ),
//            StatementAST.SkipAST
//        ).check(st, seh)
//    }
//
//    @Test
//    fun nestedWhileValidCheck() {
//        // a simple nested while loop test
//        StatementAST.WhileAST(
//            ExprAST.BoolLiterAST("true"),
//            StatementAST.WhileAST(
//                ExprAST.BoolLiterAST("false"),
//                StatementAST.SkipAST
//            )
//        ).check(st, seh)
//    }
//
//    @Test
//    fun nestedWhileInvalidCheck() {
//        // invalid nested while loop test
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.WhileAST(
//            ExprAST.BoolLiterAST("true"),
//            StatementAST.WhileAST(
//                ExprAST.IntLiterAST("+", "3"),
//                StatementAST.SkipAST
//            )
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.typeMismatch(boolType, intType) }
//    }
//
//    @Test
//    fun whileASTInvalidExprCheck() {
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.WhileAST(
//            ExprAST.CharLiterAST("c"),
//            StatementAST.SkipAST
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.typeMismatch(boolType, charType) }
//    }
}