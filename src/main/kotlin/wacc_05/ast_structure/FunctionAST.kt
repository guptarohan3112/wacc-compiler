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
        if (func != null) {
            errorHandler.repeatVariableDeclaration(funcName)
        }

        // Create function identifier and add to symbol table
        val funcST = SymbolTable(st)
        val returnTypeIdent: IdentifierObject? = st.lookupAll(returnType.toString())
        val funcIdent =
            FunctionIdentifier(returnTypeIdent as TypeIdentifier, ArrayList(), funcST)
        st.add(funcName, funcIdent)

        // Add return type as key value pair of symbol table for function (for future reference)
        funcST.add(returnType.toString(), returnTypeIdent)

        // Check parameter list and function body
        paramList?.check(funcST, errorHandler)
        body.check(funcST, errorHandler)

    }

}
