package wacc_05

import antlr.WaccParserBaseVisitor
import wacc_05.ast_structure.AST

class Visitor : WaccParserBaseVisitor<AST>() {
}