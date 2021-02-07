package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    val returnType: TypeAST,
    val funcName: String,
    val paramList: ParamListAST?,
    val body: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        // look up return type in symbol table and check valid identifier
        val returnType: IdentifierObject? = st.lookupAll(returnType.toString())
        val func: IdentifierObject? = st.lookup(funcName)

        // assuming all types can be returned, do we need to check this?
        if (returnType == null) {
            errorHandler.invalidIdentifier(returnType.toString())
        } else if (returnType !is TypeIdentifier) {
            errorHandler.invalidType(returnType.toString())
        } else if (func != null) {
            // TODO do we need a specific repeatFunctionDeclaration? (I think this is fine)
            errorHandler.repeatVariableDeclaration(funcName)
        }

        val funcST = SymbolTable(st)

        val funcIdent =
            FunctionIdentifier(funcName, returnType as TypeIdentifier, ArrayList(), funcST)
        st.add(funcName, funcIdent)

        // The return type is added as a explicit entry to the symbol table for the function scope
        funcST.add(returnType.toString(), returnType)

        paramList?.check(funcST, errorHandler)

        body.check(funcST, errorHandler)

    }

}
