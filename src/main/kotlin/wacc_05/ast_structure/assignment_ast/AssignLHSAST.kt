package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

// This class accounts for whether the left hand side of assignment is a identifier, array element or pair element
class AssignLHSAST(private val ident: String?) : AST {

    private var arrElem: ExprAST.ArrayElemAST? = null
    private var pairElem: PairElemAST? = null

    constructor(arrElem: ExprAST.ArrayElemAST) : this(null) {
        this.arrElem = arrElem
    }

    constructor(pairElem: PairElemAST) : this(null) {
        this.pairElem = pairElem
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
            else -> {
                st.lookupAll(ident)!!.getType()
            }
        }
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
                // Add the identifier into symbol table for error recovery
                st.add(ident, VariableIdentifier(TypeIdentifier.GENERIC))
            } else if (type is FunctionIdentifier) {
                errorHandler.invalidAssignment(ident)
            }
        }
    }

}