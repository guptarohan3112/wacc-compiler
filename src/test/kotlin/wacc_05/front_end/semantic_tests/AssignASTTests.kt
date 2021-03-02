package wacc_05.front_end.semantic_tests

import antlr.WaccParser
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class AssignASTTests : StatSemanticTests() {

    @Test
    fun assignASTLHSValidIdentCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTLHSValidPairElemCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(TypeIdentifier.PairIdentifier(intType, intType)))

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                PairElemAST(
                    WaccParser.PairElemContext(WaccParser.StatContext(), 0),
                    ExprAST.IdentAST(
                        WaccParser.ExprContext(WaccParser.StatContext(), 0), "x"
                    ), false
                )
            ), ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTLHSValidArrayElemCheck() {
        // x[3] = 3
        st.add("int", intType)
        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                ExprAST.ArrayElemAST(
                    WaccParser.ArrayElemContext(
                        WaccParser.ExprContext(WaccParser.StatContext(), 0),
                        0
                    ),
                    "x",
                    arrayListOf(ExprAST.IntLiterAST("+", "3"))
                )
            ), ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTLHSIdentNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.invalidIdentifier(any(), "x") }
    }

    @Test
    fun assignASTLHSIncorrectTypeCheck() {
        st.add("int", intType)
        st.add("char", charType)
        st.add("x", VariableIdentifier(charType))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.typeMismatch(any(), any(), any()) }
    }

    @Test
    fun assignASTLHSPairNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(),
                    0
                ), PairElemAST(
                    WaccParser.PairElemContext(WaccParser.StatContext(), 0),
                    ExprAST.IdentAST(
                        WaccParser.ExprContext(WaccParser.StatContext(), 0), "x"
                    ), true
                )
            ),
            ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.invalidIdentifier(any(), "x") }
    }

    @Test
    fun assignASTLHSArrayNotPresentCheck() {
        st.add("int", intType)

        every { seh.invalidIdentifier(any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(WaccParser.StatContext(), 0),
                ExprAST.ArrayElemAST(
                    WaccParser.ArrayElemContext(WaccParser.StatContext(), 0),
                    "x",
                    arrayListOf(ExprAST.IntLiterAST("+", "3"))
                )
            ),
            ExprAST.IntLiterAST("+", "3")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.invalidIdentifier(any(), "x") }
    }

    @Test
    fun assignASTRHSValidArrayLiterCheck() {
        // we assign an array of same type but different length to x
        st.add("int", intType)
        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))

        // x = [3]

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ArrayLiterAST(
                WaccParser.ArrayLitContext(WaccParser.StatContext(), 0),
                arrayListOf(ExprAST.IntLiterAST("+", "3"))
            )
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTRHSValidNewPairCheck() {
        val identifier = TypeIdentifier.PairIdentifier(intType, charType)

        st.add("int", intType)
        st.add("char", charType)
        st.add("x", VariableIdentifier(identifier))

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            NewPairAST(ExprAST.IntLiterAST("+", "3"), ExprAST.CharLiterAST("c"))
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTRHSValidCallCheck() {
        // create a generic function identifier of int return type
        // test should succeed since function has same return type as variable x
        val identifier = FunctionIdentifier(intType, ArrayList(), st)

        st.add("int", intType)
        st.add("foo", identifier)
        st.add("x", VariableIdentifier(intType))

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            FuncCallAST(WaccParser.FuncCallContext(WaccParser.StatContext(), 0), "foo", ArrayList())
        )

        assign.st = st
        visitor.visitAssignAST(assign)
    }

    @Test
    fun assignASTRHSIncorrectTypeCheck() {
        st.add("int", intType)
        st.add("char", charType)
        st.add("x", VariableIdentifier(intType))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ExprAST.CharLiterAST("c")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }

    @Test
    fun assignASTRHSUndefinedIdentifierCheck() {
        st.add("int", intType)
        st.add("x", VariableIdentifier(intType))

        every { seh.invalidIdentifier(any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(), 0), "y")
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.invalidIdentifier(any(), "y") }
    }

    @Test
    fun assignASTRHSArrayLiterIncorrectTypeCheck() {
        // we try and assign an array of different type
        st.add("int", intType)
        st.add("char", charType)
        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            ArrayLiterAST(
                WaccParser.ArrayLitContext(WaccParser.StatContext(), 0),
                arrayListOf(ExprAST.CharLiterAST("c"), ExprAST.CharLiterAST("f"))
            )
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) {
            seh.typeMismatch(
                any(),
                TypeIdentifier.ArrayIdentifier(intType, 0),
                TypeIdentifier.ArrayIdentifier(charType, 0)
            )
        }
    }

    @Test
    fun assignASTRHSFuncCallIncorrectTypeCheck() {
        val identifier = FunctionIdentifier(charType, ArrayList(), st)
        st.add("int", intType)
        st.add("char", charType)
        st.add("x", VariableIdentifier(intType))
        st.add("foo", identifier)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            FuncCallAST(WaccParser.FuncCallContext(WaccParser.StatContext(), 0), "foo", ArrayList())
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }

    @Test
    fun assignASTRHSNewPairTypeMismatchCheck() {
        val intCharIdentifier = TypeIdentifier.PairIdentifier(intType, charType)
        val charIntIdentifier = TypeIdentifier.PairIdentifier(charType, intType)
        st.add("char", charType)
        st.add("int", intType)
        st.add("x", VariableIdentifier(intCharIdentifier))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val assign = StatementAST.AssignAST(
            WaccParser.StatAssignContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            ),
            NewPairAST(ExprAST.CharLiterAST("c"), ExprAST.IntLiterAST("+", "3"))
        )

        assign.st = st
        visitor.visitAssignAST(assign)

        verify { seh.typeMismatch(any(), intCharIdentifier, charIntIdentifier) }
    }
}