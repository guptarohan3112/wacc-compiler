package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class NewPairAST(private val fst: ExprAST, private val snd: ExprAST) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return TypeIdentifier.PairIdentifier(fst.getType(st), snd.getType(st))
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        fst.check(st, errorHandler)
        snd.check(st, errorHandler)
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitNewPairAST(this)
    }
}