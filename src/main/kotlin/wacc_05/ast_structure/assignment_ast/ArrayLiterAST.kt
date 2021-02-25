package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.instructions.Instruction
import wacc_05.ast_structure.ASTVisitor
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

    override fun translate(): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitArrayLiterAST(this)
    }
}