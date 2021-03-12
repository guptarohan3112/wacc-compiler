package wacc_05.graph_colouring

import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST

class GraphFormationVisitor(private var graph: InterferenceGraph): ASTBaseVisitor() {
    // Visitor class which will make the interference graph (make all necessary nodes and put them
    // in the list of nodes)

    // int x = 3   0
    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.assignment)
        val graphNode = GraphNode(graph.getIndex(), decl.varName)

        graph.addNode(graphNode)
        graph.incrementIndex()
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        visit(assign.rhs)
        graph.incrementIndex()

//        val graphNode = GraphNode(graph.getIndex(), assign.lhs.getStringValue())
//
//        graph.addNode(graphNode)
//        graph.incrementIndex()
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val graphNode: GraphNode? = graph.findNode(ident.value)
        graphNode?.updateEndIndex(graph.getIndex())
    }
}