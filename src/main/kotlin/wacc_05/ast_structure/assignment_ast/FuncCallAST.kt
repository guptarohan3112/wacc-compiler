package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(private val function: String, private val args: ArrayList<ExprAST>) : AssignRHSAST() {

    private lateinit var returnType: TypeIdentifier

    override fun getType(): TypeIdentifier {
        return returnType
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        val funcIdentifier: IdentifierObject? = st.lookupAll(function)
        if (funcIdentifier == null) {
            errorHandler.invalidIdentifier(function)
        } else if (funcIdentifier !is FunctionIdentifier) {
            errorHandler.invalidFunction(function)
        } else {
            // Check that the number of args is as expected
            val noOfArgs: Int = funcIdentifier.getParams().size
            if (noOfArgs != args.size) {
                errorHandler.argNumberError(function, noOfArgs, args.size)
            }

            // Check that arg type match up with corresponding parameter type
            for (i in 0 until args.size) {
                args[i].check(st, errorHandler)
                val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                val actualType: TypeIdentifier = args[i].getType()
                if (expectedType != actualType) {
                    errorHandler.typeMismatch(expectedType, actualType)
                }
            }
            // Set the return type after relevant checks have been done
            returnType = funcIdentifier.getReturnType()
        }
    }

}