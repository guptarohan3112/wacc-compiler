package wacc_05.semantic_tests

import org.jmock.Expectations
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class StatSemanticTests {

    var st: SymbolTable = SymbolTable(null)

    var context: JUnitRuleMockery = JUnitRuleMockery()
    var seh: SemanticErrors = context.mock(SemanticErrors::class.java)

    @Test
    fun skipASTCheck() {
        // a skip AST check should not find any errors
        StatementAST.SkipAST.check(st, seh)
    }

    @Test
    fun declASTValidCheck() {
        st.add("int", TypeIdentifier.IntIdentifier())
        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"), "x",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)
    }

    @Test
    fun declASTRepeatDeclaration() {
        val intType: TypeIdentifier.IntIdentifier = TypeIdentifier.IntIdentifier()
        st.add("int", intType)
        st.add("x", VariableIdentifier("x", intType))

        context.checking(
            Expectations().apply {
                exactly(1).of(seh).repeatVariableDeclaration("x")
            }
        )

        StatementAST.DeclAST(
            TypeAST.BaseTypeAST("int"),
            "x",
            ExprAST.IntLiterAST("+", "3")
        ).check(st, seh)
    }
}