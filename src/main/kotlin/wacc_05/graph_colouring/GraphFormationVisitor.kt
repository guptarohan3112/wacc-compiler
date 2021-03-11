package wacc_05.graph_colouring

import wacc_05.ast_structure.ASTBaseVisitor

class GraphFormationVisitor(graph: InterferenceGraph): ASTBaseVisitor() {
    // Visitor class which will make the interference graph (make all necessary nodes and put them
    // in the list of nodes)

    // Only need to worry about Assign (AssignRHS), Decl, Ident
}