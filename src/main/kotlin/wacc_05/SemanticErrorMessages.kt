package wacc_05

import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import kotlin.system.exitProcess

class SemanticErrorMessages {

    companion object {
        val SEMANTIC_ERROR = 200
    }

    fun invalidIdentifier(name: String) {
        println("Semantic error: ${name} is an unknown element in the context of this program")
        exitProcess(SEMANTIC_ERROR)
    }

    fun invalidType(typename: String) {
        println("Semantic error: ${typename} is not a valid type")
        exitProcess(SEMANTIC_ERROR)
    }

    fun repeatVariableDeclaration(varname: String) {
        println("Semantic error: ${varname} has already been declared earlier on in the program")
        exitProcess(SEMANTIC_ERROR)
    }

    fun typeMismatch(exprname: String, expected: TypeIdentifier, actual: TypeIdentifier) {
        println("Semantic error: The expected type of ${exprname} is ${expected.toString()}, but the actual type is ${actual.toString()}")
        exitProcess(SEMANTIC_ERROR)
    }

}