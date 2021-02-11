package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    private val returnType: TypeAST,
    private val funcName: String,
    private val paramList: ParamListAST?,
    private val body: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

        // Check validity of the return type
        returnType.check(st, errorHandler)

        // Check to make sure function has not already been defined
        val func: IdentifierObject? = st.lookup(funcName)
        if (func != null && func is FunctionIdentifier) {
            errorHandler.repeatVariableDeclaration(funcName)
        }

        // Create function identifier and add to symbol table
        val funcST = SymbolTable(st)
        <<<<<<< HEAD
        val returnTypeIdent: IdentifierObject? = st.lookupAll(returnType.toString())

        // create the param identifier array list
        val params: ArrayList<ParamIdentifier> = paramList?.getParams(st) ?: ArrayList()

        val funcIdent = FunctionIdentifier(returnTypeIdent as TypeIdentifier, params, funcST)
        st.add(funcName, funcIdent)

        funcST.add(returnTypeIdent.toString(), returnTypeIdent)

        // Check parameter list and function body
        paramList?.check(funcST, errorHandler)
        body.check(funcST, errorHandler)

    }

}
