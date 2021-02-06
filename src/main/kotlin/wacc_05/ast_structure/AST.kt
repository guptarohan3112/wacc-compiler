package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable

interface AST {
    // Function that applies semantic checks
    fun check(st : SymbolTable)

}