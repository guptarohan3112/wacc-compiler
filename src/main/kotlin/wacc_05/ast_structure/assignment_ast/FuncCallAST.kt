package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(private val function: String, private val args: ArrayList<ExprAST>) : AssignRHSAST() {


    override fun getType(st: SymbolTable): TypeIdentifier {
        return (st.lookupAll(function) as FunctionIdentifier).getReturnType()
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        val funcIdentifier: IdentifierObject? = st.lookupAll(function)
        when (funcIdentifier) {
            null -> {
                errorHandler.invalidIdentifier(function)
            }

            !is FunctionIdentifier -> {
                errorHandler.invalidFunction(function)
            }

            else -> {
                // Check that the number of args is as expected
                val noOfArgs: Int = funcIdentifier.getParams().size
                if (noOfArgs != args.size) {
                    errorHandler.argNumberError(function, noOfArgs, args.size)
                }

                // Check that arg type match up with corresponding parameter type
                for (i in 0 until args.size.coerceAtMost(noOfArgs)) {
                    args[i].check(st, errorHandler)
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = args[i].getType(st)
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(expectedType, actualType)
                    }
                }
            }
        }
    }
}