package wacc_05.graph_colouring

import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class LoopExprVisitor {
    /* this class will visit all ast nodes of a while's loopexpr and where
     * necessary extend their live range to last the whole of the while loop
     */

    fun visitFor(forAST: StatementAST.ForAST) {
        visitExpr(forAST.loopExpr, forAST.ctx.getStop().line)
    }

    fun visitWhile(whileAST: StatementAST.WhileAST) {
        visitExpr(whileAST.loopExpr, whileAST.ctx.getStop().line)
    }

    private fun visitExpr(expr: ExprAST, endLine: Int) {
        when (expr) {
            is ExprAST.BinOpAST -> {
                visitExpr(expr.expr1, endLine)
                visitExpr(expr.expr2, endLine)
            }

            is ExprAST.UnOpAST -> {
                visitExpr(expr.expr, endLine)
            }

            is ExprAST.IdentAST -> {
                val ident = expr.st().lookupAll(expr.value)
                if (ident is VariableIdentifier) {
                    ident.getGraphNode().updateEndIndex(endLine)
                }
            }

            else -> {
            }
        }
    }
}