package wacc_05.front_end

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class O1SemanticVisitor(
    symbolTable: SymbolTable,
    functionST: FunctionST,
    private val errorHandler: SemanticErrors
) : SemanticVisitor(symbolTable, functionST, errorHandler) {

    private fun detectErrors(ctx: ParserRuleContext, expr: ExprAST) {
        if (expr.canEvaluate()) {
            // check for division by zero
            if (expr is ExprAST.BinOpAST && expr.operator == "/" && expr.expr2.canEvaluate()
                && expr.expr2.evaluate() == 0.toLong()
            ) {
                errorHandler.divideByZero(expr.ctx)
            } else {
                val evaluation: Long = expr.evaluate()
                if (evaluation > Int.MAX_VALUE || evaluation < Int.MIN_VALUE) {
                    errorHandler.integerOverflow(ctx, evaluation)
                }
            }
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        val symTab: SymbolTable = unop.st()
        visitChild(symTab, unop.expr)
        val exprType = unop.expr.getType()

        when (unop.operator) {
            "len" -> {
                if (exprType !is TypeIdentifier.ArrayIdentifier) {
                    errorHandler.typeMismatch(
                        unop.ctx,
                        TypeIdentifier.ArrayIdentifier(TypeIdentifier(), 0),
                        exprType
                    )
                }
            }
            "ord" -> {
                if (exprType !is TypeIdentifier.CharIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.CHAR_TYPE, exprType)
                }
            }
            "chr", "-" -> {
                if (exprType !is TypeIdentifier.IntIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.INT_TYPE, exprType)
                } else {
                    detectErrors(unop.ctx, unop)
                }
            }
            "!" -> {
                if (exprType !is TypeIdentifier.BoolIdentifier) {
                    errorHandler.typeMismatch(unop.ctx, TypeIdentifier.BOOL_TYPE, exprType)
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    override fun visitIntIntFunction(binop: ExprAST.BinOpAST, expr1Type: TypeIdentifier, expr2Type: TypeIdentifier) {
        super.visitIntIntFunction(binop, expr1Type, expr2Type)

        if (binop.expr1 is ExprAST.IntLiterAST && binop.expr2 is ExprAST.IntLiterAST) {
            detectErrors(binop.ctx, binop)
        }
    }
}