package wacc_05.front_end

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class SemanticErrorHandler : SemanticErrors {

    var err: Int = Error.SUCCESS

    override fun invalidIdentifier(ctx: ParserRuleContext, name: String) {
        println(semanticErrStr(ctx) + "$name is an unknown identifier in the context of this scope")
        semanticErr()
    }

    override fun invalidType(ctx: ParserRuleContext, typeName: String) {
        println(semanticErrStr(ctx) + "$typeName is not a valid type")
        semanticErr()
    }

    override fun invalidFunction(ctx: ParserRuleContext, funcName: String) {
        println(semanticErrStr(ctx) + "$funcName is not a valid function that has been predefined")
        semanticErr()
    }

    override fun repeatVariableDeclaration(ctx: ParserRuleContext, varName: String) {
        println(semanticErrStr(ctx) + "$varName has already been declared earlier on in the current scope.")
        semanticErr()
    }

    override fun typeMismatch(ctx: ParserRuleContext, expected: TypeIdentifier, actual: TypeIdentifier) {
        println(semanticErrStr(ctx) + "Could not match the expected type of $expected to the actual type of $actual")
        semanticErr()
    }

    override fun identifierMismatch(ctx: ParserRuleContext, expected: IdentifierObject, actual: IdentifierObject) {
        println(semanticErrStr(ctx) + "Could not match the expected identifier of $expected to the actual identifier of $actual")
        semanticErr()
    }

    override fun argNumberError(ctx: ParserRuleContext, fName: String, expected: Int, actual: Int) {
        println(semanticErrStr(ctx) + "The function $fName is expecting $expected argument(s), but is given $actual")
        semanticErr()
    }

    override fun invalidReturnType(ctx: ParserRuleContext) {
        println(semanticErrStr(ctx) + "This return statement is of the wrong return type for the function")
        semanticErr()
    }

    override fun invalidReturn(ctx: ParserRuleContext) {
        println(semanticErrStr(ctx) + "Cannot return from the main program")
        semanticErr()
    }

    override fun invalidAssignment(ctx: ParserRuleContext, fName: String) {
        println(semanticErrStr(ctx) + "Cannot assign a value to the function $fName")
        semanticErr()
    }

    override fun invalidReadType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        println(semanticErrStr(ctx) + "Invalid read type. Could not match expected type of {int, char} to $actual")
        semanticErr()
    }

    override fun invalidFreeType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        println(semanticErrStr(ctx) + "Invalid free type. Could not match expected type of {pair(T1, T2), T[]} to $actual")
        semanticErr()
    }

    override fun invalidExitType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        println(semanticErrStr(ctx) + "Invalid exit type. Could not match expected type of int to $actual")
        semanticErr()
    }

    private fun semanticErrStr(ctx: ParserRuleContext): String {
        return "Semantic error at (${ctx.getStart().line}:${ctx.getStart().charPositionInLine})" +
                "-(${ctx.getStop().line}:${ctx.getStop().charPositionInLine}): "
    }

    private fun semanticErr() {
        err = Error.SEMANTIC_ERROR
    }
}
