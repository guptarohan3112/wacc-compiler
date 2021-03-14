package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ForLoopTests: StatSemanticTests() {
    @Test
    fun forLoopValidCheck() {
        st.add("int", TypeIdentifier.IntIdentifier())
        val forLoop = StatementAST.ForAST(
            WaccParser.StatForContext(WaccParser.StatContext()),
            StatementAST.DeclAST(
                WaccParser.StatDeclarationContext(WaccParser.StatContext()),
                TypeAST.BaseTypeAST(
                    WaccParser.BaseTypeContext(WaccParser.StatContext(), 0),
                    "int"
                ), "i",
                ExprAST.IntLiterAST("+", "0")
            ),
            ExprAST.BinOpAST(
                WaccParser.ExprContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "i"),
                ExprAST.IntLiterAST("+", "5"),
                "<"
            ),
            StatementAST.AssignAST(
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
            ),
            StatementAST.SkipAST
        )

        forLoop.st = st
        visitor.visitForAST(forLoop)
    }

    @Test
    fun forLoopNoDeclaration() {
        TODO("Implement this test")
    }

    @Test
    fun forLoopNoIntDeclaration() {
        TODO("Implement this test")
    }

    @Test
    fun forLoopNoComparison() {
        TODO("Implement this test")
    }

    @Test
    fun forLoopNoUpdate() {
        TODO("Implement this test")
    }
}