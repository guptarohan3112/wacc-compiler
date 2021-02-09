package wacc_05.semantic_tests

import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

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

    @Test
    fun unOpNegValidCheck() {
        st.add("int", intType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "3"),
                "-"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpNegInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.CharLiterAST("c"),
                "-"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }

    @Test
    fun unOpNegReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("-", "4"),
                "-"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(charType, intType) }
    }

    @Test
    fun unOpLenValidCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", arrIdent))

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "y",
            ExprAST.UnOpAST(
                ExprAST.IdentAST("x"),
                "len"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpLenInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "3"),
                "len"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), intType) }
    }

    @Test
    fun unOpLenReturnTypeCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("char", charType)
        st.add("arr", arrIdent)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IdentAST("arr"),
                "len"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(charType, intType) }
    }

    @Test
    fun unOpOrdValidCheck() {
        st.add("int", intType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.CharLiterAST("c"),
                "ord"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpOrdInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "4"),
                "ord"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(charType, intType) }
    }

    @Test
    fun unOpOrdReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.CharLiterAST("c"),
                "ord"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(charType, intType) }
    }

    @Test
    fun unOpChrValidCheck() {
        st.add("char", charType)

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "42"),
                "chr"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpChrInvalidArgumentTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("char"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.BoolLiterAST("false"),
                "chr"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, boolType) }
    }

    @Test
    fun unOpChrReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any()) } just runs

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.UnOpAST(
                ExprAST.IntLiterAST("+", "42"),
                "chr"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
    }
}