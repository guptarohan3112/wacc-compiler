package wacc_05.ast_structure

import java.util.*

class ProgramAST(val functionList : ArrayList<FunctionAST>,
                 val statementList : ArrayList<StatementAST>) : AST {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
//        TODO("Not yet implemented")
        return ""
    }
}