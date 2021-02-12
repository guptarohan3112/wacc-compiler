package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(private val funcName: String, private val args: ArrayList<ExprAST>) :
    AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return (st.lookupAll(funcName) as FunctionIdentifier).getReturnType()
    }

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        val funcCallContext = ctx as WaccParser.FuncCallContext

        val funcIdentifier: IdentifierObject? = st.lookupAll(funcName)
        when (funcIdentifier) {
            null -> {
                errorHandler.invalidIdentifier(funcCallContext, funcName)
            }
            !is FunctionIdentifier -> {
                errorHandler.invalidFunction(funcCallContext, funcName)
            }
            else -> {
                // Check that the number of args is as expected
                val noOfArgs: Int = funcIdentifier.getParams().size
                if (noOfArgs != args.size) {
                    errorHandler.argNumberError(funcCallContext, funcName, noOfArgs, args.size)
                }

                // Check that arg type match up with corresponding parameter type
                for (i in 0 until args.size.coerceAtMost(noOfArgs)) {
                    args[i].check(funcCallContext.argList(), st, errorHandler)
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = args[i].getType(st)
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(funcCallContext.argList(), expectedType, actualType)
                    }
                }
            }
        }
    }

}