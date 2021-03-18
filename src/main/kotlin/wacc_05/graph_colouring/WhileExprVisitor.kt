package wacc_05.graph_colouring

import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class WhileExprVisitor {
    /* this class will visit all ast nodes of a while's loopexpr and where
     * necessary extend their live range to last the whole of the while loop
     */

    fun visitWhile(whileAST: StatementAST.WhileAST) {
        visitExpr(whileAST.loopExpr, whileAST.ctx.getStop().line)
    }

    private fun visitExpr(expr: ExprAST, endLineOfWhile: Int) {
        when (expr) {
            is ExprAST.BinOpAST -> {
                visitExpr(expr.expr1, endLineOfWhile)
                visitExpr(expr.expr2, endLineOfWhile)
            }

            is ExprAST.UnOpAST -> {
                visitExpr(expr.expr, endLineOfWhile)
            }

            is ExprAST.IdentAST -> {
                val ident = expr.st().lookupAll(expr.value) as VariableIdentifier
                ident.getGraphNode().updateEndIndex(endLineOfWhile)
            }

            else -> {
            }
        }
    }
}