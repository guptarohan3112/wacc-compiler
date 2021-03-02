package wacc_05.symbol_table

import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject

object FunctionST : SymbolTable(null) {
    override fun lookup(name: String): FunctionIdentifier? {
        return super.lookup(name) as FunctionIdentifier?
    }

    override fun lookupAll(name: String): FunctionIdentifier? {
        return super.lookupAll(name) as FunctionIdentifier?
    }

    override fun add(name: String, obj: IdentifierObject) {
        if(obj is FunctionIdentifier) {
            super.add(name, obj)
        }
    }
}