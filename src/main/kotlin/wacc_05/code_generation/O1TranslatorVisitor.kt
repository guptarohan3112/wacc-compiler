package wacc_05.code_generation

import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.instructions.LoadInstruction
import wacc_05.code_generation.utilities.AddressingMode
import wacc_05.code_generation.utilities.Register
import wacc_05.code_generation.utilities.Registers

class O1TranslatorVisitor(private val representation: AssemblyRepresentation) : TranslatorVisitor(representation) {

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