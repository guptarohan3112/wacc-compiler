package wacc_05.ast_structure

class FunctionAST(val returnType: String,
                  val fname: String,
                  val paramList: ParamListAST,
                  val body: StatementAST): AST {

    override fun check() {
        // look up type in symbol table using returnType as the key
//        TODO("Not yet implemented")
    }

    // For debugging purposes
    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }

}
