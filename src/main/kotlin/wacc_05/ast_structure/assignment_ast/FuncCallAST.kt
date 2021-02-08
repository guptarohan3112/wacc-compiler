package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject

class FuncCallAST(private val function: String, private val args: ArrayList<ExprAST>) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        val funcIdentifier: IdentifierObject? = st.lookupAll(function)
        if (funcIdentifier == null) {
            errorHandler.invalidIdentifier(function)
        } else if (funcIdentifier !is FunctionIdentifier) {
            errorHandler.invalidFunction(function)
        } else {
            val noOfArgs: Int = funcIdentifier.getParams().size
            // Check for the expected number of arguments for this function call
            if (noOfArgs != args.size) {
                errorHandler.argNumberError(function, noOfArgs, args.size)
            }
            for (arg in args) {
                arg.check(st, errorHandler)
            }
        }
    }

}