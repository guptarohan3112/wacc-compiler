package wacc_05.back_end.execution_tests

import antlr.WaccParser
import org.junit.Test
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction

open class AssignCodeGenTests : StatCodeGenTests() {

    @Test
    fun unOpNegValidCheck(){

        var instructions: ArrayList<Instruction> = ExprAST.UnOpAST(
        WaccParser.UnaryOperContext(WaccParser.StatContext(), 0),
        ExprAST.IntLiterAST("+", "32"), "-").translate(Registers())

        // check expected output: [LDR r4, [sp], RSBS r4, r4, #0]

    }

}