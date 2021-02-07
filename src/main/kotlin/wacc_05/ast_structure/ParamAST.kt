package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class ParamAST(val type: String,
               val name: String) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        val typeIdent: IdentifierObject? = st.lookupAll(type)

        if (typeIdent == null) {
            errorHandler.invalidIdentifier(type)
        } else if (typeIdent !is TypeIdentifier) {
            errorHandler.invalidType(type)
        }

        val paramIdent = ParamIdentifier(typeIdent as TypeIdentifier, name)
        st.add(name, paramIdent)
    }

    override fun toString(): String {
        return name
    }
}
