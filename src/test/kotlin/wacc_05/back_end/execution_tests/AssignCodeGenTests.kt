package wacc_05.back_end.execution_tests

import antlr.WaccParser
import org.junit.Test
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.TypeAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.AssemblyRepresentation
import wacc_05.code_generation.Registers
import wacc_05.code_generation.TranslatorVisitor
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.SemanticVisitor
import wacc_05.symbol_table.SymbolTable
import java.lang.reflect.Type

open class AssignCodeGenTests : StatCodeGenTests() {

    @Test
    fun unOpNegValidCheck() {

        var instructions: ArrayList<Instruction> = ExprAST.UnOpAST(
            WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
            ExprAST.IntLiterAST("+", "32"), "-"
        ).translate()

        // check expected output: [LDR r4, [sp], RSBS r4, r4, #0]

    }

    @Test
    fun identRead() {

        val translatorVisitor = TranslatorVisitor()

        val readAST: AST = StatementAST.ReadAST(
            WaccParser.StatReadContext(WaccParser.StatContext()),
            AssignLHSAST(
                WaccParser.AssignLHSContext(
                    WaccParser.StatContext(), 0
                ), ExprAST.IdentAST(
                    WaccParser.ExprContext(
                        WaccParser.StatContext(),
                        0
                    ), "x"
                )
            )
        )

        val declAST: AST = StatementAST.DeclAST(
            WaccParser.StatDeclarationContext(WaccParser.StatContext()),
            TypeAST.BaseTypeAST(WaccParser.BaseTypeContext(WaccParser.StatContext(), 0), "char"),
            "x",
            ExprAST.CharLiterAST(
                "c"
            )
        )

        val symTab = SymbolTable(null)
        SymbolTable.makeTopLevel(symTab)

        val semanticChecker = SemanticVisitor(symTab, seh)

        semanticChecker.visit(declAST)
        semanticChecker.visit(readAST)

        translatorVisitor.visit(declAST)
        translatorVisitor.visit(readAST)
        AssemblyRepresentation.buildAssembly("identRead")
        // check expected output: [LDR r4, [sp], RSBS r4, r4, #0]

    }


}