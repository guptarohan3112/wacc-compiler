package wacc_05

import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import kotlin.system.exitProcess

class SemanticErrorHandler : SemanticErrors {

    companion object {
        val SEMANTIC_ERROR = 200
    }

    override fun invalidIdentifier(name: String) {
        println("Semantic error: ${name} is an unknown element in the context of this program")
        exitProcess(SEMANTIC_ERROR)
    }

    override fun invalidType(typeName: String) {
        println("Semantic error: ${typeName} is not a valid type")
        exitProcess(SEMANTIC_ERROR)
    }

    override fun invalidFunction(funcName: String) {
        println("Semantic error: ${funcName} is not a valid function that has been predefined")
    }

    override fun repeatVariableDeclaration(varName: String) {
        println("Semantic error: ${varName} has already been declared earlier on in the current scope.")
        exitProcess(SEMANTIC_ERROR)
    }

    override fun typeMismatch(expected: TypeIdentifier, actual: TypeIdentifier) {
        println("Semantic error: Could not match the expected type of ${expected} to the actual type of ${actual}")
        exitProcess(SEMANTIC_ERROR)
    }

    override fun argNumberError(fName: String, expected: Int, actual: Int) {
        println("Semantic error: The function ${fName} is expecting ${expected} argument(s), but is given ${actual}")
    }

    override fun invalidReturnType() {
        println("Semantic error: This return statement is of the wrong return type for the function")
    }

}