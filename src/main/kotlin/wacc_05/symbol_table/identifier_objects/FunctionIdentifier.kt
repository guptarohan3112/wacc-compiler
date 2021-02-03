package wacc_05.symbol_table.identifier_objects

import wacc_05.symbol_table.SymbolTable

class FunctionIdentifier(
    private val name: String,
    private val returnType: TypeIdentifier,
    parentST: SymbolTable
) : ScopedIdentifierObject(parentST)