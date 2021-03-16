package wacc_05.graph_colouring

import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST

class O1GraphFormationVisitor(graph: InterferenceGraph) : GraphFormationVisitor(graph) {
    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        if (whileStat.loopExpr.canEvaluate() && whileStat.loopExpr.evaluate() == 0.toLong()) {
            // do nothing
        } else {
            super.visitWhileAST(whileStat)
        }
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        if (ifStat.condExpr.canEvaluate()) {
            if (ifStat.condExpr.evaluate() == 1.toLong()) {
                visit(ifStat.thenStat)
            } else {
                visit(ifStat.elseStat)
            }
        } else {
            super.visitIfAST(ifStat)
        }
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (binop.canEvaluate()) {
            createAndSetGraphNode(binop)
        } else {
            super.visitBinOpAST(binop)
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        if (unop.canEvaluate()) {
            createAndSetGraphNode(unop)
        } else {
            super.visitUnOpAST(unop)
        }
    }
}