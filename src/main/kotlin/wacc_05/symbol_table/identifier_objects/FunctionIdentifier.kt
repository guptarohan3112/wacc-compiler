package wacc_05.symbol_table.identifier_objects

import main.kotlin.wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.type_identifiers.TypeIdentifier

class FunctionIdentifier(
    parentST: SymbolTable,
    private val returnType: TypeIdentifier,
    private val name: String,
    private val params: ParamListIdentifier?,
    private val body: StatementIdentifier
) : ScopedIdentifierObject(parentST) {

    /*init {
        parentST.add(name, this);
    }*/
}