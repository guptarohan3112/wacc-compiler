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

class BinOpTests : ExprSemanticTests() {

    val statDeclarationContext: WaccParser.StatDeclarationContext =
        WaccParser.StatDeclarationContext(WaccParser.StatContext())

    val baseTypeContext: WaccParser.BaseTypeContext =
        WaccParser.BaseTypeContext(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), 0)

    val exprContext: WaccParser.ExprContext =
        WaccParser.ExprContext(WaccParser.StatContext(), 0)

    @Test
    fun binOpMultValidCheck() {
        // these tests will use DeclAST as a way of verifying the return type of the binOp

        st.add("int", intType)

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "int"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.IntLiterAST(ctx, "+", "3"),
                ExprAST.IntLiterAST(ctx, "+", "5"),
                "*"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun binOpMultIncorrectArgTypeCheck() {
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "int"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.IntLiterAST(ctx, "+", "4"),
                ExprAST.CharLiterAST(ctx, "c"),
                "*"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), intType, charType) }
    }

    @Test
    fun binOpMultReturnTypeCheck() {
        st.add("char", charType)
        st.add("int", intType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "char"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.IntLiterAST(ctx, "+", "4"),
                ExprAST.IntLiterAST(ctx, "+", "5"),
                "*"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify { seh.typeMismatch(any(), charType, intType) }
    }

    @Test
    fun binOpLogicalValidCheck() {
        st.add("bool", boolType)

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "bool"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.BoolLiterAST(ctx, "true"),
                ExprAST.BoolLiterAST(ctx, "false"),
                "&&"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)
    }

    @Test
    fun binOpLogicalInvalidArgumentTypeCheck() {
        st.add("bool", boolType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "bool"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.CharLiterAST(ctx, "c"),
                ExprAST.BoolLiterAST(ctx, "true"),
                "&&"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), boolType, charType) }
    }

    @Test
    fun binOpLogicalReturnTypeCheck() {
        st.add("char", charType)

        every { seh.typeMismatch(any(), any(), any()) } just runs

        val decl = StatementAST.DeclAST(
            statDeclarationContext,
            TypeAST.BaseTypeAST(baseTypeContext, "char"),
            "x",
            ExprAST.BinOpAST(
                exprContext,
                ExprAST.BoolLiterAST(ctx, "true"),
                ExprAST.BoolLiterAST(ctx, "false"),
                "||"
            )
        )

        decl.st = st
        visitor.visitDeclAST(decl)

        verify(exactly = 1) { seh.typeMismatch(any(), charType, boolType) }
    }
}