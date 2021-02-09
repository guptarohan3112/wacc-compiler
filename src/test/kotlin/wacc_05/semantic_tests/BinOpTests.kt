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

    @Test
    fun binOpGTIntValidCheck() {
        st.add("bool", boolType)
        st.add("int", intType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "4"),
                ExprAST.IntLiterAST("+", "1"),
                ">"
            )
        ).check(st, seh)
    }

    @Test
    fun binOpGTCharValidCheck() {
        st.add("bool", boolType)
        st.add("char", charType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.CharLiterAST("A"),
                ExprAST.CharLiterAST("a"),
                ">"
            )
        ).check(st, seh)
    }

    @Test
    fun binOpGTInvalidArgTypeCheck() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.BoolLiterAST("true"),
                ExprAST.BoolLiterAST("false"),
                ">"
            )
        ).check(st, seh)

        // use any here to capture expectation of intType or charType
        verify(exactly = 2) { seh.typeMismatch(any(), boolType) }
    }

    @Test
    fun binOpGTIntCharTypeCheck() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "4"),
                ExprAST.CharLiterAST("c"),
                ">"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }

    @Test
    fun binOpGTReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.BinOpAST(
                ExprAST.IntLiterAST("+", "3"),
                ExprAST.IntLiterAST("+", "3"),
                ">"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(charType, boolType) }
    }
}