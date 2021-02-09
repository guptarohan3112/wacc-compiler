package wacc_05.ast_structure

import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

sealed class TypeAST : AST {

    data class BaseTypeAST(private val typeName: String) : TypeAST() {

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
    }

    data class PairTypeAST(
        private val fstType: PairElemTypeAST,
        private val sndType: PairElemTypeAST
    ) : TypeAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
            fstType.check(st, errorHandler)
            sndType.check(st, errorHandler)
        }

        override fun toString(): String {
            return fstType.toString()
        }
    }
}