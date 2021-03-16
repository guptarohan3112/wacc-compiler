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

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "bool"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST(boolLitContext, "true"),
                "!"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun unOpNotInvalidArgumentType() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "bool"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "4"),
                "!"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, intType) }
    }

    @Test
    fun unOpNotReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST(boolLitContext, "true"),
                "!"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, boolType) }
    }

    @Test
    fun unOpNegValidCheck() {
        st.add("int", intType)

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "3"),
                "-"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun unOpNegInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST(charLitContext, "c"),
                "-"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }

    @Test
    fun unOpNegReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "-", "4"),
                "-"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpLenValidCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("x", VariableIdentifier(arrIdent))

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "y",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "x"),
                "len"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun unOpLenInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "3"),
                "len"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), any(), intType) }
    }

    @Test
    fun unOpLenReturnTypeCheck() {
        val arrIdent = TypeIdentifier.ArrayIdentifier(intType, 5)
        st.add("int", intType)
        st.add("char", charType)
        st.add("arr", VariableIdentifier(arrIdent))

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IdentAST(WaccParser.IdentContext(WaccParser.StatContext(), 0), "arr"),
                "len"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpOrdValidCheck() {
        st.add("int", intType)

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST(charLitContext, "c"),
                "ord"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun unOpOrdInvalidArgumentTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "4"),
                "ord"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpOrdReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.CharLiterAST(charLitContext, "c"),
                "ord"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun unOpChrValidCheck() {
        st.add("char", charType)

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "42"),
                "chr"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun unOpChrInvalidArgumentTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.BoolLiterAST(boolLitContext, "false"),
                "chr"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, boolType) }
    }

    @Test
    fun unOpChrReturnTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "int"),
            "x",
            ExprAST.UnOpAST(
                WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
                ExprAST.IntLiterAST(intLitContext, "+", "42"),
                "chr"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }
}