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
open class SymbolTable(private val parentST: SymbolTable?) {
    // hashmap storing mappings from a string identifier to a corresponding IdentifierObject
    private val map: HashMap<String, IdentifierObject?> = HashMap()
    // stack pointer for this scope
    private var spAndOffset: Pair<Int, Int> = Pair(0, 0)

    /* Function: add()
     * ------------------------------
     * Adds a mapping into the hashmap
     * Param name - the string identifier we want this mapping to have
     * Param obj - the object we want to map to. This should not be null.
     */
    open fun add(name: String, obj: IdentifierObject) {
        map[name] = obj
    }

    /* Function: lookup()
     * --------------------
     * looks up the given string identifier in this symbol table only
     * Param name - the identifier we wish to look up
     * Returns the corresponding identifier object if found or null otherwise
     */
    open fun lookup(name: String): IdentifierObject? {
        return map.getOrDefault(name, null)
    }

    /* Function: lookupAll()
     * ----------------------
     * Looks up the given identifier in this context and all parent contexts
     * Param name - the identifier we wish to look up
     * Returns the corresponding identifier object if found or null otherwise
     */
    open fun lookupAll(name: String): IdentifierObject? {
        return lookup(name) ?: parentST?.lookupAll(name)
    }

    open fun lookUpAndCheckAllocation(name: String): IdentifierObject? {
        val ident: IdentifierObject? = map.getOrDefault(name, null)
        if (ident is VariableIdentifier && !ident.isAllocated()) {
            return null
        }
        return ident
    }

    open fun lookUpAllAndCheckAllocation(name: String): IdentifierObject? {
        return lookUpAndCheckAllocation(name) ?: parentST?.lookUpAllAndCheckAllocation(name)
    }

    fun getStackPtr(): Int {
        return spAndOffset.first
    }

    // When setting the boundary stack pointer, change the offset from that to be 0
    fun setStackPtr(sp: Int) {
        this.spAndOffset = Pair(sp, 0)
    }

    fun getStackPtrOffset(): Int {
        return spAndOffset.second
    }

    fun updatePtrOffset(update: Int) {
        val ptr = this.spAndOffset.first
        var currOffset = this.spAndOffset.second
        currOffset += update
        this.spAndOffset = Pair(ptr, currOffset)
    }

    fun isMain(): Boolean {
        return parentST == null
    }

    fun clear() {
        map.clear()
    }

    companion object {
        /* Function: makeTopLevel()
         * ------------------------
         * We use makeTopLevel() to insert everything that would need to be included in the
         * top level symbol table - this includes any type keywords and unary operators.
         */
        fun makeTopLevel(st: SymbolTable) {
            // add the key type keywords to the symbol table with their corresponding types
            st.add("int", TypeIdentifier.INT_TYPE)
            st.add("char", TypeIdentifier.CHAR_TYPE)
            st.add("bool", TypeIdentifier.BOOL_TYPE)
            st.add("string", TypeIdentifier.STRING_TYPE)
            st.add("pair", TypeIdentifier.PAIR_LIT_TYPE)

            addUnaryOps(st)
        }

        /* Function: addUnaryOps()
         * -----------------------
         * We use this function to add the unary operators to a top level symbol table.
         * We add the unary operators since len, chr and ord are all valid identifiers, but
         * should not be defined by the program as they are built into the language.
         */
        private fun addUnaryOps(st: SymbolTable) {
            st.add(
                "!",
                UnaryOpIdentifier(
                    TypeIdentifier.BOOL_TYPE,
                    TypeIdentifier.BOOL_TYPE
                )
            )

            st.add(
                "-",
                UnaryOpIdentifier(
                    TypeIdentifier.INT_TYPE,
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "len",
                UnaryOpIdentifier(
                    TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0),
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "ord",
                UnaryOpIdentifier(
                    TypeIdentifier.CHAR_TYPE,
                    TypeIdentifier.INT_TYPE
                )
            )

            st.add(
                "chr",
                UnaryOpIdentifier(
                    TypeIdentifier.INT_TYPE,
                    TypeIdentifier.CHAR_TYPE
                )
            )
        }
    }
}