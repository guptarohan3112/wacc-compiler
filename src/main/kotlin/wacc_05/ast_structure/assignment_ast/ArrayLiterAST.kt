package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ArrayLiterAST(private val elems: ArrayList<ExprAST>) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        if (elems.size == 0) {
            return TypeIdentifier.GENERIC
        } else {
            return TypeIdentifier.ArrayIdentifier(elems[0].getType(st), elems.size)
        }
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        if (elems.size != 0) {
            elems[0].check(st, errorHandler)
            val firstElemType = elems[0].getType(st)

            for (i in 1 until elems.size) {
                elems[i].check(st, errorHandler)
                if (elems[i].getType(st) != firstElemType) {
                    errorHandler.typeMismatch(firstElemType, elems[i].getType(st))
                }
            }
        }
    }
}