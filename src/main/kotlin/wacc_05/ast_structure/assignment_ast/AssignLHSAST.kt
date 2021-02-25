package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.instructions.Instruction
import wacc_05.ast_structure.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

// This class accounts for whether the left hand side of assignment is a identifier, array element or pair element
class AssignLHSAST(val ctx: WaccParser.AssignLHSContext, val ident: String?) : AST() {

    var arrElem: ExprAST.ArrayElemAST? = null
        private set
    var pairElem: PairElemAST? = null
        private set

    constructor(ctx: WaccParser.AssignLHSContext,arrElem: ExprAST.ArrayElemAST) : this(ctx, null) {
        this.arrElem = arrElem
    }

    constructor(ctx: WaccParser.AssignLHSContext,pairElem: PairElemAST) : this(ctx, null) {
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

    override fun translate(): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitAssignLHSAST(this)
    }

}