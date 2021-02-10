package wacc_05

import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import kotlin.system.exitProcess

interface SemanticErrors {

    fun invalidIdentifier(name: String)

    fun invalidType(typeName: String)

    fun invalidFunction(funcName: String)

    fun repeatVariableDeclaration(varName: String)

    fun typeMismatch(expected: TypeIdentifier, actual: TypeIdentifier)

    fun identifierMismatch(expected: IdentifierObject, actual: IdentifierObject)

    fun argNumberError(fName: String, expected: Int, actual: Int)

    fun invalidReturnType()

    fun invalidReturn()

    fun invalidReadType(actual: TypeIdentifier)

    fun invalidFreeType(actual: TypeIdentifier)

    fun invalidExitType(actual: TypeIdentifier)
}