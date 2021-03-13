package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.ArrayLiterAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class ExprAST : AssignRHSAST() {

    /* This is a helper function for evaluating binary operator expressions
     * it determines if the binary operator can be evaluated to a single value
     * (i.e. it is a chain of binary operators/unary operators that have only identifiers
     * as their arguments, e.g. 1 + (2 + (3 + 4))) )
     * returns true iff the expression can be evaluated to a single value
     */
    open fun canEvaluate(): Boolean {
        return false
    }

    /* This is a helper function used to evaluate a binary operator / unary operator to a single
     * value. This should only be called if canEvaluate() has returned true (undefined behaviour otherwise),
     * AND if the types have already been checked.
     * It returns a long type, which allows for semantic checking of overflows etc in the case when it should
     * be an int, otherwise it can be cast to chars/bools as necessary.
     */
    open fun evaluate(): Long {
        return Long.MAX_VALUE
    }

    data class IntLiterAST(val sign: String, val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.INT_TYPE
        }

        override fun canEvaluate(): Boolean {
            return true
        }

        override fun evaluate(): Long {
            return getValue().toLong()
        }

        fun getValue(): Int {
            return (sign + value).toInt()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIntLiterAST(this)
        }
    }

    data class BoolLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.BOOL_TYPE
        }

        override fun canEvaluate(): Boolean {
            return true
        }

        fun getValue(): Int {
            return when (value) {
                "true" -> 1
                else -> 0
            }
        }

        override fun evaluate(): Long {
            return getValue().toLong()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBoolLiterAST(this)
        }
    }

    data class CharLiterAST(val value: String) : ExprAST() {

        override fun canEvaluate(): Boolean {
            return true
        }

        override fun evaluate(): Long {
            return getValue().toLong()
        }

        fun getValue(): Char {
            return value[0]
        }

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.CHAR_TYPE
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitCharLiterAST(this)
        }
    }

    data class StrLiterAST(val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.StringIdentifier(value.length)
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitStrLiterAST(this)
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.PAIR_LIT_TYPE
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairLiterAST(this)
        }

        fun clear() {
            clearDestReg()
            clearAST()
        }
    }

    data class IdentAST(val ctx: WaccParser.IdentContext, val value: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (val type = st().lookupAll(value)) {
                null -> {
                    TypeIdentifier.GENERIC
                }
                else -> {
                    type.getType()
                }
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIdentAST(this)
        }
    }

    data class ArrayElemAST(
        val ctx: WaccParser.ArrayElemContext,
        val ident: String,
        val exprs: ArrayList<ExprAST>
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            val type = st().lookupAll(ident)
            return if (type == null) {
                TypeIdentifier.GENERIC
            } else {
                val typeIdent = type.getType()
                if (typeIdent !is TypeIdentifier.ArrayIdentifier) {
                    return TypeIdentifier.GENERIC
                }
                return typeIdent.getType()
            }
        }

        fun getElemType(): TypeIdentifier {
            // get the type of the array identifier achieved from getType()
            return getType().getType()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitArrayElemAST(this)
        }
    }

    data class OperatorAST(val operator: String) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (operator) {
                "-" -> TypeIdentifier.INT_TYPE
                "!" -> TypeIdentifier.BOOL_TYPE
                "len" -> TypeIdentifier.INT_TYPE
                "ord" -> TypeIdentifier.INT_TYPE
                "chr" -> TypeIdentifier.CHAR_TYPE
                else -> TypeIdentifier()
            }

        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitOperatorAST(this)
        }

    }

    data class UnOpAST(
        val ctx: WaccParser.UnaryOperContext,
        val expr: ExprAST,
        val operator: OperatorAST
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return when (operator.operator) {
                "-" -> TypeIdentifier.INT_TYPE
                "!" -> TypeIdentifier.BOOL_TYPE
                "len" -> TypeIdentifier.INT_TYPE
                "ord" -> TypeIdentifier.INT_TYPE
                "chr" -> TypeIdentifier.CHAR_TYPE
                else -> TypeIdentifier()
            }
        }

        override fun canEvaluate(): Boolean {
            return operator.operator != "len" && expr.canEvaluate()
        }

        override fun evaluate(): Long {
            return when (operator.operator) {
                "-" -> -1 * expr.evaluate()
                "!" -> if (expr.evaluate() == 1.toLong()) {
                    0
                } else {
                    1
                }
                "ord" -> expr.evaluate()
                "chr" -> expr.evaluate()
                else -> super.evaluate()
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitUnOpAST(this)
        }
    }

    data class BinOpAST(
        val ctx: WaccParser.ExprContext,
        val expr1: ExprAST,
        val expr2: ExprAST,
        val operator: String
    ) : ExprAST() {

        companion object {
            val intIntFunctions = hashSetOf("*", "/", "+", "-", "%")

            val intCharFunctions = hashSetOf(">", ">=", "<", "<=")

            val boolBoolFunctions = hashSetOf("&&", "||")
        }

        override fun getType(): TypeIdentifier {
            return when (operator) {
                // Need valid min and max integers to put here
                "+", "%", "/", "*", "-" -> TypeIdentifier.INT_TYPE
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun canEvaluate(): Boolean {
            return expr1.canEvaluate() && expr2.canEvaluate()
        }

        override fun evaluate(): Long {
            return when (operator) {
                "+" -> expr1.evaluate() + expr2.evaluate()
                "-" -> expr1.evaluate() - expr2.evaluate()
                "*" -> expr1.evaluate() * expr2.evaluate()
                "/" -> expr1.evaluate() / expr2.evaluate()
                "%" -> expr1.evaluate() % expr2.evaluate()

                ">" -> if (expr1.evaluate() > expr2.evaluate()) 1 else 0
                "<" -> if (expr1.evaluate() < expr2.evaluate()) 1 else 0
                ">=" -> if (expr1.evaluate() >= expr2.evaluate()) 1 else 0
                "<=" -> if (expr1.evaluate() <= expr2.evaluate()) 1 else 0
                "==" -> if (expr1.evaluate() == expr2.evaluate()) 1 else 0
                "!=" -> if (expr1.evaluate() != expr2.evaluate()) 1 else 0

                // for the bool-bool types we could just check >0 for TRUE but I have used 1 for stricter
                // type enforcement
                "&&" -> if (expr1.evaluate() == 1.toLong() && expr2.evaluate() == 1.toLong()) 1 else 0
                "||" -> if (expr1.evaluate() == 1.toLong() || expr2.evaluate() == 1.toLong()) 1 else 0

                else -> super.evaluate()
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBinOpAST(this)
        }
    }

    data class MapAST(
        val ctx: WaccParser.ExprContext,
        val operator: OperatorAST,
        val assignRHS: AssignRHSAST
    ) : ExprAST() {

        override fun getType(): TypeIdentifier {
            return TypeIdentifier.ArrayIdentifier(assignRHS.getType().getType(), 0)
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitMapAST(this)
        }
    }
}
