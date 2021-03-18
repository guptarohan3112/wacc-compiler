package wacc_05.code_generation.utilities

import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.graph_colouring.GraphNode
import wacc_05.graph_colouring.InterferenceGraph
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class StackSizeVisitor(val graph: InterferenceGraph): ASTBaseVisitor() {

    private var stackSize: Int = 0

    fun getStackSize(): Int {
        return stackSize
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.assignment)

        val size: Int = decl.getStackSize()
        val correctNode: GraphNode? = decl.getGraphNode()
        if (correctNode != null && !correctNode.isAllocated() && (correctNode.getRegister()) != Register(-1)) {
            return
        } else {
            stackSize += size
            correctNode?.allocate()
        }
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        super.visitBinOpAST(binop)

        incrementStackSizeIfNecessary(binop)
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        super.visitUnOpAST(unop)

        incrementStackSizeIfNecessary(unop)
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        incrementStackSizeIfNecessary(liter)
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        incrementStackSizeIfNecessary(liter)
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        incrementStackSizeIfNecessary(liter)
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        incrementStackSizeIfNecessary(liter)
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        visit(newPair.fst)
        visit(newPair.snd)
        incrementStackSizeIfNecessary(newPair)
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        for(elem in arrayLiter.elems) {
            visit(elem)
        }
        incrementStackSizeIfNecessary(arrayLiter)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        incrementStackSizeIfNecessary(pairElem)
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        for(elem in arrayElem.exprs) {
            visit(elem)
        }
        incrementStackSizeIfNecessary(arrayElem)
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        if(lhs.pairElem != null) {
            visit(lhs.pairElem!!)
        } else if(lhs.arrElem != null) {
            visit(lhs.arrElem!!)
        } else {
            visit(lhs.ident!!)
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        incrementStackSizeIfNecessary(funcCall)
        for(arg in funcCall.args) {
            visit(arg)
        }
    }

    private fun incrementStackSizeIfNecessary(ast: AssignRHSAST) {
        val size: Int = ast.getStackSize()
//        stackSize += size
        val correctNode: GraphNode? = ast.getGraphNode()
        if (correctNode != null && !correctNode.isAllocated() && (correctNode.getRegister()) != Register(-1)) {
            return
        } else {
            stackSize += size
            correctNode?.allocate()
        }
    }

}