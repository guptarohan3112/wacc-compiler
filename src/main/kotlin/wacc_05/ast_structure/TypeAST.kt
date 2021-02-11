package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class TypeAST : AST {

    abstract fun getType(st: SymbolTable): TypeIdentifier

    data class BaseTypeAST(private val typeName: String) : TypeAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            val typeIdent: IdentifierObject? = st.lookupAll(typeName)
            return if (typeIdent == null) {
                TypeIdentifier.GENERIC
            } else {
                typeIdent as TypeIdentifier
            }
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            val typeIdent: IdentifierObject? = st.lookupAll(typeName)

            if (typeIdent == null) {
                errorHandler.invalidIdentifier(typeName)
            } else if (typeIdent !is TypeIdentifier) {
                errorHandler.invalidType(typeName)
            }
        }

        override fun toString(): String {
            return typeName
        }
    }

    data class ArrayTypeAST(private val elemsType: TypeAST) : TypeAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            elemsType.check(st, errorHandler)
        }

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.ArrayIdentifier(elemsType.getType(st), 0)
        }

        override fun toString(): String {
            return elemsType.toString()
        }
    }

    data class PairElemTypeAST(private val pair: String? = null, private val type: TypeAST?) : AST {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            type?.check(st, errorHandler)
        }

        override fun toString(): String {
            return type.toString()
        }

        fun getType(st: SymbolTable): TypeIdentifier {
            return type?.getType(st) ?: TypeIdentifier.GENERIC_PAIR_TYPE
        }
    }

    data class PairTypeAST(
        private val fstType: PairElemTypeAST,
        private val sndType: PairElemTypeAST
    ) : TypeAST() {

        override fun getType(st: SymbolTable) : TypeIdentifier {
            return TypeIdentifier.PairIdentifier(fstType.getType(st), sndType.getType(st))
        }

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            fstType.check(st, errorHandler)
            sndType.check(st, errorHandler)
        }

        override fun toString(): String {
            return "pair($fstType, $sndType)"
        }
    }
}