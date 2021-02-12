package wacc_05.ast_structure

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    private val returnType: TypeAST,
    private val funcName: String,
    private val paramList: ParamListAST?,
    private val body: StatementAST
) : AST {

    fun preliminaryCheck(ctx: WaccParser.FuncContext, st: SymbolTable, errorHandler: SemanticErrors) {
        returnType.check(ctx, st, errorHandler)

        // Check to make sure function has not already been defined
        val func: IdentifierObject? = st.lookup(funcName)
        if (func != null && func is FunctionIdentifier) {
            errorHandler.repeatVariableDeclaration(ctx, funcName)
        } else {
            // Create function identifier and add to symbol table
            val funcST = SymbolTable(st)
            val returnTypeIdent: TypeIdentifier = returnType.getType(st)

            // create the param identifier array list
            val params: ArrayList<ParamIdentifier> = paramList?.getParams(st) ?: ArrayList()

            val funcIdent =
                FunctionIdentifier(returnTypeIdent, params, funcST)

            // Check parameter list and function body
            paramList?.check(ctx.paramList(), funcST, errorHandler)

            funcST.add("returnType", returnTypeIdent)

            // add self to higher level symbol table
            st.add(funcName, funcIdent)
        }
    }

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        val functionContext = ctx as WaccParser.FuncContext

        val funcIdentifier = st.lookupAll(funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            body.check(functionContext.stat(), (funcIdentifier as FunctionIdentifier).getSymbolTable(), errorHandler)
        }
    }
}
