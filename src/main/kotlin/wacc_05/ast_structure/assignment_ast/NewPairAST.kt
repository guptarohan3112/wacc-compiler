package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class NewPairAST(private val fst: ExprAST, private val snd: ExprAST) : AssignRHSAST() {

    private lateinit var elemType: TypeIdentifier

    override fun getType(): TypeIdentifier {
        return elemType
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {

        fst.check(st, errorHandler)
        snd.check(st, errorHandler)

        // Set type after
        elemType = TypeIdentifier.PairIdentifier(fst.getType(), snd.getType())
    }

}