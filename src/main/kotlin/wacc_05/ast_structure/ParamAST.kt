package wacc_05.ast_structure

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class ParamAST(
    private val type: TypeAST,
    private val name: String
) : AST {

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        val paramContext = ctx as WaccParser.ParamContext

        // Check validity of parameter type
        type.check(
            if (paramContext.type().pairType() != null) {
                paramContext.type().pairType()
            } else {
                paramContext.type()
            }, st, errorHandler
        )

        // Create parameter identifier and add to symbol table
        val typeIdent: TypeIdentifier = type.getType(st)
        val paramIdent = ParamIdentifier(typeIdent)
        st.add(name, paramIdent)
    }

    override fun toString(): String {
        return name
    }

    fun getType(st: SymbolTable): TypeIdentifier {
        return type.getType(st)
    }

}
