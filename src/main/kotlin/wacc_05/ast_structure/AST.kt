package wacc_05.ast_structure

interface AST {
    // Function that applies semantic checks
    fun check()

    override fun toString() : String
}