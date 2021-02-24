package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.ast_structure.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class PairElemAST(val ctx: WaccParser.PairElemContext, val elem: ExprAST, val isFst: Boolean) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        val pairType = elem.getType(st)

        return if (pairType is TypeIdentifier.PairIdentifier) {
            if (isFst) {
                pairType.getFstType()
            } else {
                pairType.getSndType()
            }
        } else {
            pairType
        }
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        elem.check(st, errorHandler)

        val elemType = elem.getType(st)

        // The type of the element has to be a generic type (when added for error recovery), a pair or a pair literal
        if (elemType != TypeIdentifier.GENERIC
            && elemType !is TypeIdentifier.PairIdentifier
            && elemType !is TypeIdentifier.PairLiterIdentifier
        ) {
            errorHandler.typeMismatch(ctx, TypeIdentifier.PairLiterIdentifier, elemType)
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitPairElemAST(this)
    }
}