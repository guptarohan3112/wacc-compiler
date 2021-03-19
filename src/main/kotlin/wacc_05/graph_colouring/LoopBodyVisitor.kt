package wacc_05.graph_colouring

import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.PairElemAST

class LoopBodyVisitor(private val loop: StatementAST, private val endLine: Int) : ASTBaseVisitor() {
    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val identifier = loop.st().lookupAllAndCheckVisited(ident.value)
        if (identifier != null) {
            ident.getGraphNode()?.updateEndIndex(endLine)
        }
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        visitAssignLHSAST(assign.lhs)
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        when {
            lhs.arrElem != null -> {
                visitArrayElemAST(lhs.arrElem!!)
            }
            lhs.pairElem != null -> {
                visitPairElemAST(lhs.pairElem!!)
            }
            else -> {
                visitIdentAST(lhs.ident!!)
            }
        }
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        val identifier = loop.st().lookupAllAndCheckVisited(arrayElem.ident)
        if(identifier != null) {
            arrayElem.getArrayLocation()?.updateEndIndex(endLine)
        }
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visit(pairElem.elem)
    }
}