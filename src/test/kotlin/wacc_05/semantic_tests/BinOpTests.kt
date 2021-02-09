package wacc_05.semantic_tests

import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST

class BinOpTests : ExprSemanticTests() {

    @Test
    fun binOpMultValidCheck() {
        // these tests will use DeclAST as a way of verifying the return type of the binOp

        st.add("int", intType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "3"),
                ExprAST.IntLiterAST("+", "5"),
                "*"
            )
        ).check(st, seh)
    }

    @Test
    fun binOpMultIncorrectArgTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "4"),
                ExprAST.CharLiterAST("c"),
                "*"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }

    @Test
    fun binOpMultReturnTypeCheck() {
        st.add("char", charType)
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "4"),
                ExprAST.IntLiterAST("+", "5"),
                "*"
            )
        ).check(st, seh)

        verify { seh.typeMismatch(charType, intType) }
    }
}