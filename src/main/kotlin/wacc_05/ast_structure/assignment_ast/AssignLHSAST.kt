package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

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
            val type = st.lookupAll(ident!!)
            if (type == null) {
                errorHandler.invalidIdentifier(ident)
                st.add(ident, VariableIdentifier(TypeIdentifier.GENERIC))
            }
            else if (type is FunctionIdentifier) {
                errorHandler.invalidAssignment(ident)
            }
        }
    }

    fun getType(st: SymbolTable): TypeIdentifier {
        return when {
            arrElem != null -> {
                arrElem!!.getType(st)
            }
            pairElem != null -> {
                pairElem!!.getType(st)
            }
            st.lookupAll(ident!!) is FunctionIdentifier -> {
                TypeIdentifier.GENERIC
            }
            st.lookupAll(ident) is VariableIdentifier -> {
                (st.lookupAll(ident) as VariableIdentifier).getType()
            }
            else -> {
                (st.lookupAll(ident) as ParamIdentifier).getType()
            }
        }
    }
}