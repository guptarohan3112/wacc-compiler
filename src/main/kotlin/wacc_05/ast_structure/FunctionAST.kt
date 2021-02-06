package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable

class FunctionAST(val returnType: String,
                  val fname: String,
                  val paramList: ParamListAST,
                  val body: StatementAST): AST {

    override fun check(st: SymbolTable) {
        // look up type in symbol table using returnType as the key
//        TODO("Not yet implemented")
    }

}
