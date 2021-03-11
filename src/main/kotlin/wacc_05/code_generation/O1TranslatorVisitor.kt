package wacc_05.code_generation

import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.code_generation.instructions.BranchInstruction
import wacc_05.code_generation.instructions.CompareInstruction
import wacc_05.code_generation.instructions.LabelInstruction
import wacc_05.code_generation.instructions.LoadInstruction
import wacc_05.code_generation.utilities.*

class O1TranslatorVisitor(private val representation: AssemblyRepresentation) : TranslatorVisitor(representation) {

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
            }

        } else {
            super.visitWhileAST(whileStat)
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        if (unop.canEvaluate()) {
            val dest: Register = Registers.allocate()

            representation.addMainInstr(
                LoadInstruction(
                    dest,
                    AddressingMode.AddressingLabel(unop.evaluate().toString())
                )
            )

            unop.setDestReg(dest)
        } else {
            super.visitUnOpAST(unop)
        }
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (binop.canEvaluate()) {
            val dest: Register = Registers.allocate()

            representation.addMainInstr(
                LoadInstruction(
                    dest,
                    AddressingMode.AddressingLabel(binop.evaluate().toString())
                )
            )

            binop.setDestReg(dest)
        } else {
            super.visitBinOpAST(binop)
        }
    }
}