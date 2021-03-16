package wacc_05.symbol_table.identifier_objects

abstract class IdentifierObject {

    /* Function: getType()
     * -------------------
     * Each IdentifierObject will have a getType method which is used to get
     * the corresponding TypeIdentifier for any given IdentifierObject. This
     * is used in check functions in collaboration with the symbol table
     * for type comparison between different AST nodes.
     *
     * Returns: The corresponding TypeIdentifier representing the type of the
     * IdentifierObject.
     */
    abstract fun getType(): TypeIdentifier
}
