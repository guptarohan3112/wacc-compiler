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
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class UnOpTests : ExprSemanticTests() {

    @Test
    fun unOpNotValidCheck() {
        st.add("bool", boolType)

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "bool"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST("true"),
                "!"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpNotInvalidArgumentType() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "bool"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "4"),
                "!"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
    }

    @Test
    fun unOpNotReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST("true"),
                "!"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, boolType) }
    }

    @Test
    fun unOpNegValidCheck() {
        st.add("int", intType)

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "3"),
                "-"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpNegInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST("c"),
                "-"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }

    @Test
    fun unOpNegReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("-", "4"),
                "-"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpLenValidCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier(arrIdent))

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "y",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(), 0), "x"),
                "len"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpLenInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "3"),
                "len"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), any(), intType) }
    }

    @Test
    fun unOpLenReturnTypeCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("char", charType)
        st.add("arr", VariableIdentifier(arrIdent))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.ExprContext(WaccParser.StatContext(), 0), "arr"),
                "len"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpOrdValidCheck() {
        st.add("int", intType)

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST("c"),
                "ord"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpOrdInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "4"),
                "ord"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpOrdReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST("c"),
                "ord"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpChrValidCheck() {
        st.add("char", charType)

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "42"),
                "chr"
            )
        ).check(st, seh)
    }

    @Test
    fun unOpChrInvalidArgumentTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST("false"),
                "chr"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, boolType) }
    }

    @Test
    fun unOpChrReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST("+", "42"),
                "chr"
            )
        ).check(st, seh)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }
}