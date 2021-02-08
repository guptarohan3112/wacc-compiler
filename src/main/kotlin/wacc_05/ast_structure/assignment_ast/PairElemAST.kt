package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class PairElemAST(private val elem: ExprAST) : AssignRHSAST() {

    private lateinit var type: TypeIdentifier

    override fun getType(): TypeIdentifier {
        return type
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        elem.check(st, errorHandler)
        type = elem.getType()
    }

}