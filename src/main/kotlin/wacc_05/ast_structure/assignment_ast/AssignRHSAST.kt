package wacc_05.ast_structure.assignment_ast

import wacc_05.ast_structure.AST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

abstract class AssignRHSAST : AST {

    abstract fun getType(st: SymbolTable) : TypeIdentifier

}