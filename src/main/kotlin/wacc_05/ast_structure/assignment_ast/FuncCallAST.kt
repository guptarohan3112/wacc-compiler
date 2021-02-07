package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ArgListAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject

class FuncCallAST(private val function: String, private val args: ArgListAST?) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        val funcIdentifier: IdentifierObject? = st.lookupAll(function)
        if (funcIdentifier == null) {
            errorHandler.invalidIdentifier(function)
        } else if (funcIdentifier !is FunctionIdentifier) {
            errorHandler.invalidFunction(function)
        } else {
            if (args != null) {
                args.check(st, errorHandler)
                // arglist needs to know how many arguments are meant to be here for this function
            } else {
                val noOfArgs = funcIdentifier.getParams().size
                if (noOfArgs != 0) {
                    errorHandler.argNumberError(function, 0, noOfArgs)
                }
            }
        }
    }

}