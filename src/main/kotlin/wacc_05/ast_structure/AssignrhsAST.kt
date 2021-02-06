package wacc_05.ast_structure

import wacc_05.symbol_table.SymbolTable

sealed class AssignrhsAST() : AST {

    // Expression?

    data class NewPairAST(val expr1: ExprAST, val expr2: ExprAST) : AssignrhsAST() {

        override fun check(st: SymbolTable) {
//            TODO("Not yet implemented")
        }


    }

}
