package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable

sealed class TypeAST : AST {

    // Not sure about toString method for all of these classes

    data class BaseTypeAST(private val typeName: String) : TypeAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return typeName
        }
    }

    data class ArrayTypeAST(private val elemsType: TypeAST) : TypeAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return elemsType.toString()
        }
    }

    data class PairElemTypeAST(private val pair: String? = null, private val type: TypeAST?) : AST {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return type.toString()
        }
    }

    data class PairTypeAST(
        private val fstType: PairElemTypeAST,
        private val sndType: PairElemTypeAST
    ) : TypeAST() {

        override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return fstType.toString()
        }

    }
}