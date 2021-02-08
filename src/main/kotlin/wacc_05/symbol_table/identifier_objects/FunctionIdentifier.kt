package wacc_05.symbol_table.identifier_objects

import wacc_05.symbol_table.SymbolTable

class FunctionIdentifier(
    private val returnType: TypeIdentifier,
    private val params: ArrayList<ParamIdentifier>,
    parentST: SymbolTable
) : ScopedIdentifierObject(parentST) {

    fun getReturnType(): TypeIdentifier {
        return returnType
    }

    fun getParams(): ArrayList<ParamIdentifier> {
        return params
    }
}