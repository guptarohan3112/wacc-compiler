package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.KeywordIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class AssignLHSAST(private val ident: String?) : AST {

    private var arrElem: ExprAST.ArrayElemAST? = null
    private var pairElem: PairElemAST? = null

    constructor(arrElem: ExprAST.ArrayElemAST) : this(null) {
        this.arrElem = arrElem
    }

    constructor(pairElem: PairElemAST) : this(null) {
        this.pairElem = pairElem
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        if (arrElem != null) {
            arrElem!!.check(st, errorHandler)
        } else if (pairElem != null) {
            pairElem!!.check(st, errorHandler)
        } else {
            if (st.lookupAll(ident!!) == null) {
                errorHandler.invalidIdentifier(ident)
                st.add(ident, VariableIdentifier(ident, TypeIdentifier.GENERIC))
            }
        }
    }

//    fun setType(st: SymbolTable, type: TypeIdentifier) {
//        st.add(ident!!, VariableIdentifier(ident, type))
//    }

    fun getType(st: SymbolTable): TypeIdentifier {
        return when {
            arrElem != null -> {
                arrElem!!.getType(st)
            }
            pairElem != null -> {
                pairElem!!.getType(st)
            }
            else -> {
                (st.lookupAll(ident!!) as VariableIdentifier).getType()
            }
        }
    }
}