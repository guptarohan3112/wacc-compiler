package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ParamAST(
    private val type: TypeAST,
    private val name: String
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        type.check(st, errorHandler)
//        val typeIdent: IdentifierObject? = st.lookupAll(type.toString())
//
//        if (typeIdent == null) {
//            errorHandler.invalidIdentifier(type.toString())
//        } else if (typeIdent !is TypeIdentifier) {
//            errorHandler.invalidType(type.toString())
//        }

        val typeIdent: IdentifierObject? = st.lookupAll(type.toString())
        val paramIdent = ParamIdentifier(typeIdent as TypeIdentifier)
        st.add(name, paramIdent)
    }

    override fun toString(): String {
        return name
    }

}
