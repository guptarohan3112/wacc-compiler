package wacc_05.symbol_table.identifier_objects

import main.kotlin.wacc_05.symbol_table.SymbolTable
import main.kotlin.wacc_05.symbol_table.identifier_objects.IdentifierObject

abstract class ScopedIdentifierObject(parentST: SymbolTable) : IdentifierObject() {
    private val st : SymbolTable = SymbolTable(parentST, null)

    init {
        parentST.setEnclosed(st)
    }
}