package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ArrayLiterAST(val ctx: WaccParser.ArrayLitContext, val elems: ArrayList<ExprAST>) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return if (elems.size == 0) {
            // If the array literal is empty, the element type could be any type
            TypeIdentifier.GENERIC
        } else {
            // Otherwise, determine the type of the first element and create an ArrayIdentifier
            TypeIdentifier.ArrayIdentifier(elems[0].getType(st), elems.size)
        }
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        // If the array literal is empty, no semantic check need to be done
        if (elems.size != 0) {
            elems[0].check(st, errorHandler)
            val firstElemType = elems[0].getType(st)

            // Verify that individual elements are semantically correct and that they are all the same type
            for (i in 1 until elems.size) {
                elems[i].check(st, errorHandler)
                if (elems[i].getType(st) != firstElemType) {
                    errorHandler.typeMismatch(ctx, firstElemType, elems[i].getType(st))
                }
            }
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitArrayLiterAST(this)
    }
}