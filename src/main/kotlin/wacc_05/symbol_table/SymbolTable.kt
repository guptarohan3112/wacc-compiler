package main.kotlin.wacc_05.symbol_table

import main.kotlin.wacc_05.symbol_table.identifier_objects.IdentifierObject
import java.util.HashMap

class SymbolTable(private val enclosed: SymbolTable?) {
    private val map: HashMap<String, IdentifierObject?> = HashMap()

    fun add(name: String, obj: IdentifierObject?) {
        map.put(name, obj)
    }

    fun lookup(name: String): IdentifierObject? {
        return map.getOrDefault(name, null)
    }

    fun lookupAll(name: String): IdentifierObject? {
        return lookup(name) ?: enclosed?.lookupAll(name)
    }
}