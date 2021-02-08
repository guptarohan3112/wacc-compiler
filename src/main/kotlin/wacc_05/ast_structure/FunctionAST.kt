package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    private val returnType: TypeAST,
    private val funcName: String,
    private val paramList: ParamListAST?,
    private val body: StatementAST
) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {

        // look up return type in symbol table and check valid identifier
        returnType.check(st, errorHandler)
//        val returnType: IdentifierObject? = st.lookupAll(returnType.toString())

        // assuming all types can be returned, do we need to check this?
//        if (returnType == null) {
//            errorHandler.invalidIdentifier(returnType.toString())
//        } else if (returnType !is TypeIdentifier) {
//            errorHandler.invalidType(returnType.toString())
//        } else if (func != null) {
//            errorHandler.repeatVariableDeclaration(funcName)
//        }

        val func: IdentifierObject? = st.lookup(funcName)
        if (func != null) {
            errorHandler.repeatVariableDeclaration(funcName)
        }

        val funcST = SymbolTable(st)
        val returnTypeIdent: IdentifierObject? = st.lookupAll(returnType.toString())

        val funcIdent =
            FunctionIdentifier(funcName, returnTypeIdent as TypeIdentifier, ArrayList(), funcST)
        st.add(funcName, funcIdent)

        // The return type is added as a explicit entry to the symbol table for the function scope
        funcST.add(returnType.toString(), returnTypeIdent)

        paramList?.check(funcST, errorHandler)

        body.check(funcST, errorHandler)

    }

}
