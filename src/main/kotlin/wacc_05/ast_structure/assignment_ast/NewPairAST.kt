package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.instructions.Instruction
import wacc_05.ast_structure.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class NewPairAST(val fst: ExprAST, val snd: ExprAST) : AssignRHSAST() {

    override fun getType(): TypeIdentifier {
        return TypeIdentifier.PairIdentifier(fst.getType(), snd.getType())
    }

    override fun translate(): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitNewPairAST(this)
    }
}