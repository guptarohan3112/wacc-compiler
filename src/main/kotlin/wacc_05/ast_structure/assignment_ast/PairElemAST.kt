package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class PairElemAST(private val elem: ExprAST, private val isFst: Boolean) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        val pairType = elem.getType(st)

        return if (pairType is TypeIdentifier.PairIdentifier) {
            if (isFst) {
                pairType.getFstType()
            } else {
                pairType.getSndType()
            }
        } else {
            pairType
        }
    }

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        val pairElemContext = ctx as WaccParser.PairElemContext

        elem.check(pairElemContext.expr(), st, errorHandler)

        val elemType = elem.getType(st)

        // The type of the element has to be a generic type (when added for error recovery), a pair or a pair literal
        if (elemType != TypeIdentifier.GENERIC
            && elemType !is TypeIdentifier.PairIdentifier
            && elemType !is TypeIdentifier.PairLiterIdentifier
        ) {
            errorHandler.typeMismatch(pairElemContext.expr(), TypeIdentifier.PairLiterIdentifier, elemType)
        }
    }

}