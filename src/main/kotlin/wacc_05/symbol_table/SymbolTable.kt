package wacc_05.symbol_table

import wacc_05.symbol_table.identifier_objects.*
import java.util.HashMap

/* Class: SymbolTable
 * -------------------
 * This represents the symbol table data type, mapping from identifiers (strings) to a corresponding
 * identifier object which stores any necessary information.
 *
 * parentST - this is used to create a hierarchical structure of symbol tables. parentST can be null IFF it is the top
 *            level symbol table. NOTE we do not have a child SymbolTable stored here directly - these are instead
 *            stored in IdentifierObjects
 */
class SymbolTable(private val parentST: SymbolTable?) {
    // hashmap storing mappings from a string identifier to a corresponding IdentifierObject
    private val map: HashMap<String, IdentifierObject?> = HashMap()

    /* Function: add()
     * ------------------------------
     * Adds a mapping into the hashmap
     * Param name - the string identifier we want this mapping to have
     * Param obj - the object we want to map to. This should not be null.
     */
    fun add(name: String, obj: IdentifierObject) {
        map[name] = obj
    }

    /* Function: lookup()
     * --------------------
     * looks up the given string identifier in this symbol table only
     * Param name - the identifier we wish to look up
     * Returns the corresponding identifier object if found or null otherwise
     */
    fun lookup(name: String): IdentifierObject? {
        return map.getOrDefault(name, null)
    }

    /* Function: lookupAll()
     * ----------------------
     * Looks up the given identifier in this context and all parent contexts
     * Param name - the identifier we wish to look up
     * Returns the corresponding identifier object if found or null otherwise
     */
    fun lookupAll(name: String): IdentifierObject? {
        return lookup(name) ?: parentST?.lookupAll(name)
    }

    companion object {
        fun makeTopLevel(st: SymbolTable) {
            st.add("int", TypeIdentifier.INT_TYPE)
            st.add("char", TypeIdentifier.CHAR_TYPE)
            st.add("bool", TypeIdentifier.BOOL_TYPE)
            st.add("string", TypeIdentifier.STRING_TYPE)
            st.add("pair", TypeIdentifier.PairLiterIdentifier)

            addUnaryOps(st)
        }

        private fun addUnaryOps(st: SymbolTable) {
            st.add(
                "!",
                UnaryOpIdentifier(
                    UnaryOpIdentifier.UnaryOp.NOT,
                    TypeIdentifier.BOOL_TYPE,
                    TypeIdentifier.BOOL_TYPE
                )
            )

            st.add(
                "-",
                UnaryOpIdentifier(
                    UnaryOpIdentifier.UnaryOp.NEGATIVE,
                    TypeIdentifier.INT_TYPE,
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "len",
                UnaryOpIdentifier(
                    UnaryOpIdentifier.UnaryOp.LEN,
                    TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0),
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "ord",
                UnaryOpIdentifier(
                    UnaryOpIdentifier.UnaryOp.ORD,
                    TypeIdentifier.CHAR_TYPE,
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "chr",
                UnaryOpIdentifier(
                    UnaryOpIdentifier.UnaryOp.CHR,
                    TypeIdentifier.IntIdentifier(0, 256),
                    TypeIdentifier.CharIdentifier
                )
            )
        }
    }
}