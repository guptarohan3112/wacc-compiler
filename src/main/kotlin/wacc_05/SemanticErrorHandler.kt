package wacc_05

import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class SemanticErrorHandler : SemanticErrors {

    var err : Int = Error.SUCCESS

    override fun invalidIdentifier(name: String) {
        println("Semantic error: ${name} is an unknown element in the context of this program")
        semanticErr()
    }

    override fun invalidType(typeName: String) {
        println("Semantic error: $typeName is not a valid type")
        semanticErr()
    }

    override fun invalidFunction(funcName: String) {
        println("Semantic error: $funcName is not a valid function that has been predefined")
        semanticErr()
    }

    override fun repeatVariableDeclaration(varName: String) {
        println("Semantic error: $varName has already been declared earlier on in the current scope.")
        semanticErr()
    }

    override fun typeMismatch(expected: TypeIdentifier, actual: TypeIdentifier) {
        println("Semantic error: Could not match the expected type of $expected to the actual type of $actual")
        semanticErr()
    }

    override fun identifierMismatch(expected: IdentifierObject, actual: IdentifierObject) {
        println("Semantic error: Could not match the expected identifier of ${expected} to the actual identifier of ${actual}")
        semanticErr()
    }

    override fun argNumberError(fName: String, expected: Int, actual: Int) {
        println("Semantic error: The function $fName is expecting $expected argument(s), but is given $actual")
        semanticErr()
    }

    override fun invalidReturnType() {
        println("Semantic error: This return statement is of the wrong return type for the function")
        semanticErr()
    }

    override fun invalidReadType(actual: TypeIdentifier) {
        println("Semantic error: Invalid read type. Could not match expected type of {int, char} to $actual")
        semanticErr()
    }

    override fun invalidFreeType(actual: TypeIdentifier) {
        println("Semantic error: Invalid free type. Could not match expected type of {pair(T1, T2), T[]} to $actual")
        semanticErr()
    }

    override fun invalidExitType(actual: TypeIdentifier) {
        println("Semantic error: Invalid exit type. Could not match expected type of int to $actual")
        semanticErr()
    }

    private fun semanticErr() {
        err = Error.SEMANTIC_ERROR
    }
}