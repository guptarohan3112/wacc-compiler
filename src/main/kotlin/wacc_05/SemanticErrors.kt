package wacc_05

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import kotlin.system.exitProcess

interface SemanticErrors {

    fun invalidIdentifier(ctx: ParserRuleContext, name: String)

    fun invalidType(ctx: ParserRuleContext, typeName: String)

    fun invalidFunction(ctx: ParserRuleContext, funcName: String)

    fun repeatVariableDeclaration(ctx: ParserRuleContext, varName: String)

    fun typeMismatch(ctx: ParserRuleContext, expected: TypeIdentifier, actual: TypeIdentifier)

    fun identifierMismatch(ctx: ParserRuleContext, expected: IdentifierObject, actual: IdentifierObject)

    fun argNumberError(ctx: ParserRuleContext, fName: String, expected: Int, actual: Int)

    fun invalidReturnType(ctx: ParserRuleContext, )

    fun invalidReturn(ctx: ParserRuleContext, )

    fun invalidAssignment(ctx: ParserRuleContext, fName: String)

    fun invalidReadType(ctx: ParserRuleContext, actual: TypeIdentifier)

    fun invalidFreeType(ctx: ParserRuleContext, actual: TypeIdentifier)

    fun invalidExitType(ctx: ParserRuleContext, actual: TypeIdentifier)
}
