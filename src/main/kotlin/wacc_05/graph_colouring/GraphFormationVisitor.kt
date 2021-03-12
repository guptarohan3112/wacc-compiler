package wacc_05.graph_colouring

import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.ArrayLiterAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST

class GraphFormationVisitor(private var graph: InterferenceGraph) : ASTBaseVisitor() {
    // Visitor class which will make the interference graph (make all necessary nodes and put them
    // in the list of nodes)

    // int x = 3   0
    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.assignment)
        decl.setGraphNode(decl.assignment.getGraphNode())
        decl.getGraphNode().setIdentifier(decl.varName)

        graph.incrementIndex()
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        visit(assign.rhs)
        visit(assign.lhs)

        assign.setGraphNode(assign.rhs.getGraphNode())
        assign.getGraphNode().setIdentifier(assign.lhs.getStringValue())
        graph.incrementIndex()

//        val graphNode = GraphNode(graph.getIndex(), assign.lhs.getStringValue())
//
//        graph.addNode(graphNode)
//        graph.incrementIndex()
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        val graphNode: GraphNode? = graph.findNode(lhs.getStringValue())
        graphNode?.updateEndIndex(graph.getIndex())
        graph.incrementIndex()

        lhs.setGraphNode(graphNode!!)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val graphNode: GraphNode? = graph.findNode(ident.value)
        graphNode?.updateEndIndex(graph.getIndex())
        graph.incrementIndex()

        ident.setGraphNode(graphNode!!)
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        createAndSetGraphNode(liter)
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        createAndSetGraphNode(liter)
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        createAndSetGraphNode(liter)
    }

    // result of a malloc - 1 register
    // another register - take values from literal and store at malloc address
    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        for(elem in arrayLiter.elems) {
            visit(elem)
            graph.incrementIndex()
        }

        createAndSetGraphNode(arrayLiter)
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        createAndSetGraphNode(liter)
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        // these registers have to be used at the same time so we manually add the conflict
        binop.expr1.getGraphNode().addNeighbour(binop.expr2.getGraphNode())
        binop.expr2.getGraphNode().addNeighbour(binop.expr1.getGraphNode())

        // without optimisations taking place we know that the result of the binop is always put in expr1 dest reg
        binop.setGraphNode(binop.expr1.getGraphNode())
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)
        unop.setGraphNode(unop.expr.getGraphNode())
    }

    private fun createAndSetGraphNode(node: AssignRHSAST) {
        val graphNode = GraphNode(graph.getIndex())
        node.setGraphNode(graphNode)
        graph.addNode(graphNode)
    }
}