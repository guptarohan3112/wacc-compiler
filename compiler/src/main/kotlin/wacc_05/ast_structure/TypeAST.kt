package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class TypeAST : AST() {

    abstract fun getType(): TypeIdentifier

    fun getStackSize(): Int {
        return getType().getStackSize()
    }

    data class BaseTypeAST(val ctx: WaccParser.BaseTypeContext, val typeName: String) : TypeAST() {

        override fun getType(): TypeIdentifier {
            val typeIdent: IdentifierObject? = st().lookupAll(typeName)
            return if (typeIdent == null) {
                TypeIdentifier.GENERIC
            } else {
                typeIdent as TypeIdentifier
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBaseTypeAST(this)
        }

        override fun toString(): String {
            return typeName
        }
    }

    data class ArrayTypeAST(val elemsType: TypeAST) : TypeAST() {

        override fun getType(): TypeIdentifier {
            elemsType.st = st()
            return TypeIdentifier.ArrayIdentifier(elemsType.getType(), 0)
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitArrayTypeAST(this)
        }

        override fun toString(): String {
            return elemsType.toString()
        }
    }

    data class PairElemTypeAST(val pair: String? = null, val type: TypeAST?) : AST() {

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairElemTypeAST(this)
        }

        override fun toString(): String {
            return type.toString()
        }

        fun getType(): TypeIdentifier {
            if (type != null) {
                type.st = st()
                return type.getType()
            } else {
                return TypeIdentifier.GENERIC_PAIR_TYPE
            }
        }
    }

    data class PairTypeAST(
        val fstType: PairElemTypeAST,
        val sndType: PairElemTypeAST
    ) : TypeAST() {

        override fun getType(): TypeIdentifier {
            fstType.st = st()
            sndType.st = st()
            return TypeIdentifier.PairIdentifier(fstType.getType(), sndType.getType())
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairTypeAST(this)
        }

        override fun toString(): String {
            return "pair($fstType, $sndType)"
        }
    }
}