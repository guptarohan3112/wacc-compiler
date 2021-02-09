package wacc_05.semantic_tests

import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST

class UnOpTests : ExprSemanticTests() {

    @Test
    fun unOpNotValidCheck() {
        st.add("bool", boolType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.BoolLiterAST("true"),
                "!"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpNotInvalidArgumentType() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("bool"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "4"),
                "!"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(boolType, intType) }
    }

    @Test
    fun unOpNotReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.BoolLiterAST("true"),
                "!"
            )
        )

        verify(exactly = 1) { seh.typeMismatch(intType, boolType) }
    }
}