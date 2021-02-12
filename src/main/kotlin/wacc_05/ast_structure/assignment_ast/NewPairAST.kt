package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class NewPairAST(private val fst: ExprAST, private val snd: ExprAST) : AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return TypeIdentifier.PairIdentifier(fst.getType(st), snd.getType(st))
    }

    override fun check(ctx: ParserRuleContext?, st: SymbolTable, errorHandler: SemanticErrors) {
        val newPairContext = (ctx as WaccParser.AssignRHSContext).newPair()

        fst.check(newPairContext.expr(0), st, errorHandler)
        snd.check(newPairContext.expr(1), st, errorHandler)
    }

}