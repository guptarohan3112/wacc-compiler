package wacc_05.ast_structure

sealed class TypeAST : AST {
    data class BaseTypeAST(private val typeName: String) : TypeAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class ArrayTypeAST(private val elemsType: TypeAST) : TypeAST() {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class PairElemTypeAST(private val pair: String? = null, private val type : TypeAST) : AST {
        override fun check() {
            TODO("Not yet implemented")
        }
    }

    data class PairTypeAST(private val fstType: PairElemTypeAST, private val sndType: PairElemTypeAST) : TypeAST() {
        override fun check() {
            TODO("Not yet implemented")
        }

    }
}