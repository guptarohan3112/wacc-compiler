package wacc_05.front_end

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class SemanticErrorHandler : SemanticErrors {

    private val errs: ArrayList<String> = ArrayList()

    override fun hasErrors(): Boolean {
        return errs.isNotEmpty()
    }

    override fun printErrors() {
        for (err in errs) {
            println(err)
        }
    }

    override fun invalidIdentifier(ctx: ParserRuleContext, name: String) {
        val err =
            semanticErrStr(ctx) + "$name is an unknown identifier in the context of this scope"
        semanticErr(err)
    }

    override fun invalidType(ctx: ParserRuleContext, typeName: String) {
        val err = semanticErrStr(ctx) + "$typeName is not a valid type"
        semanticErr(err)
    }

    override fun invalidFunction(ctx: ParserRuleContext, funcName: String) {
        val err = semanticErrStr(ctx) + "$funcName is not a valid function that has been predefined"
        semanticErr(err)
    }

    override fun repeatVariableDeclaration(ctx: ParserRuleContext, varName: String) {
        val err =
            semanticErrStr(ctx) + "$varName has already been declared earlier on in the current scope."
        semanticErr(err)
    }

    override fun typeMismatch(
        ctx: ParserRuleContext,
        expected: TypeIdentifier,
        actual: TypeIdentifier
    ) {
        val err =
            semanticErrStr(ctx) + "Could not match the expected type of $expected to the actual type of $actual"
        semanticErr(err)
    }

    override fun identifierMismatch(
        ctx: ParserRuleContext,
        expected: IdentifierObject,
        actual: IdentifierObject
    ) {
        val err =
            semanticErrStr(ctx) + "Could not match the expected identifier of $expected to the actual identifier of $actual"
        semanticErr(err)
    }

    override fun argNumberError(ctx: ParserRuleContext, fName: String, expected: Int, actual: Int) {
        val err =
            semanticErrStr(ctx) + "The function $fName is expecting $expected argument(s), but is given $actual"
        semanticErr(err)
    }

    override fun invalidReturnType(ctx: ParserRuleContext) {
        val err =
            semanticErrStr(ctx) + "This return statement is of the wrong return type for the function"
        semanticErr(err)
    }

    override fun invalidReturn(ctx: ParserRuleContext) {
        val err = semanticErrStr(ctx) + "Cannot return from the main program"
        semanticErr(err)
    }

    override fun invalidAssignment(ctx: ParserRuleContext, fName: String) {
        val err = semanticErrStr(ctx) + "Cannot assign a value to the function $fName"
        semanticErr(err)
    }

    override fun invalidReadType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        val err =
            semanticErrStr(ctx) + "Invalid read type. Could not match expected type of {int, char} to $actual"
        semanticErr(err)
    }

    override fun invalidFreeType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        val err =
            semanticErrStr(ctx) + "Invalid free type. Could not match expected type of {pair(T1, T2), T[]} to $actual"
        semanticErr(err)
    }

    override fun invalidExitType(ctx: ParserRuleContext, actual: TypeIdentifier) {
        val err =
            semanticErrStr(ctx) + "Invalid exit type. Could not match expected type of int to $actual"
        semanticErr(err)
    }

    private fun semanticErrStr(ctx: ParserRuleContext): String {
        return "Semantic error at (${ctx.getStart().line}:${ctx.getStart().charPositionInLine})" +
                "-(${ctx.getStop().line}:${ctx.getStop().charPositionInLine}): "
    }

    private fun semanticErr(error: String) {
        errs.add(error)
    }
}
