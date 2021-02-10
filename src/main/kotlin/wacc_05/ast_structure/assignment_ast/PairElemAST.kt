package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class PairElemAST(private val elem: ExprAST, val fst: Boolean) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return elem.getType(st)
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        elem.check(st, errorHandler)
        if (elem != ExprAST.IdentAST(elem.toString())) {
            // Not sure about this error
            errorHandler.invalidType(elem.toString())
        } else {
            val actualType: IdentifierObject = st.lookupAll(elem.toString())!!
            val anyPairType = TypeIdentifier.PairIdentifier(
                TypeIdentifier(),
                TypeIdentifier()
            )
            if (actualType != VariableIdentifier(elem.toString(), anyPairType)) {
                errorHandler.identifierMismatch(
                    VariableIdentifier(elem.toString(), anyPairType),
                    actualType
                )
            }
        }
    }

}