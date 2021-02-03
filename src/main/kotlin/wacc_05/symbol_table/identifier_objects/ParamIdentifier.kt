package wacc_05.symbol_table.identifier_objects

import main.kotlin.wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.type_identifiers.TypeIdentifier

class ParamIdentifier(private val type: TypeIdentifier, private val ident: String) : IdentifierObject()