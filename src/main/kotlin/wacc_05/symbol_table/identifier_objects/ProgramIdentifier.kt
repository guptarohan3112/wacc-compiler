package wacc_05.symbol_table.identifier_objects

import main.kotlin.wacc_05.symbol_table.SymbolTable
import main.kotlin.wacc_05.symbol_table.identifier_objects.IdentifierObject

class ProgramIdentifier(parentST: SymbolTable?) : IdentifierObject() {
    private val st : SymbolTable = SymbolTable(parentST)
}