package wacc_05.symbol_table.identifier_objects

import wacc_05.symbol_table.SymbolTable

abstract class ScopedIdentifierObject(parentST: SymbolTable) : IdentifierObject() {
    private val st : SymbolTable = SymbolTable(parentST)
}