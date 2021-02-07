package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable

class AssignLHSAST(private val ident : String?) : AST {
    // we have to store these instead of making them extend AssignLHSAST
    // due to the possibility of ident
    private var arrElem: ExprAST.ArrayElemAST? = null
    private var pairElem: PairElemAST? = null

    constructor(arrElem: ExprAST.ArrayElemAST) : this(null) {
        this.arrElem = arrElem
    }

    constructor(pairElem: PairElemAST) : this(null) {
        this.pairElem = pairElem
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        TODO("Not yet implemented")
    }
}