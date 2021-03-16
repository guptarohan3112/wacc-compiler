package wacc_05.code_generation

import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.code_generation.instructions.*
import wacc_05.code_generation.utilities.*
import wacc_05.graph_colouring.InterferenceGraph

class O1TranslatorVisitor(private val representation: AssemblyRepresentation, private val graph: InterferenceGraph) :
    TranslatorVisitor(representation, graph) {

    override fun visitIfAST(ifStat: StatementAST.IfAST) {

        val condition: ExprAST = ifStat.condExpr

        if (condition.canEvaluate()) {

            val eval = condition.evaluate()
            if (eval == 1.toLong()) {
                visit(ifStat.thenStat)
            } else {
                visit(ifStat.elseStat)
            }

        } else {
            super.visitIfAST(ifStat)
        }

    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {

        val condition: ExprAST = whileStat.loopExpr

        if (condition.canEvaluate()) {

            val eval = condition.evaluate()
            if (eval == 0.toLong()) {
                println("nothing to do")
            } else {
                super.visitWhileAST(whileStat)
            }

        } else {
            super.visitWhileAST(whileStat)
        }
    }

    private fun storeValue(dest: Operand, value: String) {
        val destReg: Register = if (dest is AddressingMode) {
            representation.addMainInstr(PushInstruction(Registers.r11))
            Registers.r11
        } else {
            dest as Register
        }

        representation.addMainInstr(LoadInstruction(destReg, AddressingMode.AddressingLabel(value)))

        if (dest is AddressingMode) {
            representation.addMainInstr(PopInstruction(Registers.r11))
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        if (unop.canEvaluate()) {
            storeValue(unop.getOperand(), unop.evaluate().toString())
        } else {
            super.visitUnOpAST(unop)
        }
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (binop.canEvaluate()) {
            storeValue(binop.getOperand(), binop.evaluate().toString())
        } else {
            super.visitBinOpAST(binop)
        }
    }
}