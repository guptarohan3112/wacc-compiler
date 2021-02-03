package wacc_05.ast_structure

class FunctionAST(val returnType: TypeAST,
                  val fname: String,
                  val paramList: ParamListAST,
                  val body: StatementAST): AST {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }

}
