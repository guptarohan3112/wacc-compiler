package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class PairElemAST(private val elem: ExprAST) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return elem.getType(st)
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        elem.check(st, errorHandler)
    }

}