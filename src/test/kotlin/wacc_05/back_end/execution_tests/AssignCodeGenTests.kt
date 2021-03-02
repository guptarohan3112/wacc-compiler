package wacc_05.back_end.execution_tests

import antlr.WaccParser
import org.junit.Test
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.AssemblyRepresentation
import wacc_05.code_generation.TranslatorVisitor

open class AssignCodeGenTests : StatCodeGenTests() {

    @Test
    fun unOpNegValidCheck(){

        var ast: AST = ExprAST.UnOpAST(
        WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
        ExprAST.IntLiterAST("+", "32"), "-")

        var tv = TranslatorVisitor();
        tv.visit(ast);
        AssemblyRepresentation.buildAssembly("test");

        // check expected output: [LDR r4, [sp], RSBS r4, r4, #0]

    }

}