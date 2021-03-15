package wacc_05.graph_colouring

import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.ArrayLiterAST
import wacc_05.ast_structure.assignment_ast.AssignLHSAST
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.ast_structure.assignment_ast.PairElemAST
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class GraphFormationVisitor(private var graph: InterferenceGraph) : ASTBaseVisitor() {
    // Visitor class which will make the interference graph (make all necessary nodes and put them
    // in the list of nodes)

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.assignment)

        if (decl.assignment.getGraphNode().getIdent() != "") {
            val graphNode = GraphNode(graph.getIndex(), decl.varName)
            decl.setGraphNode(graphNode)
            graph.addNode(graphNode)
        } else {
            decl.setGraphNode(decl.assignment.getGraphNode())
            decl.getGraphNode().setIdentifier(decl.varName)
        }

        graph.incrementIndex()
    }


    // x = 5 -> x and 5 have registers, move reg for 5 into reg for x
    //       -> x has register, give register to rhs to store 5 in
    // a[3] = rhs -> get rhs and at translation store rhs into a[3] address

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        // Do this, but every rhs needs to not override the graphnode if it has already been set
        visit(assign.rhs)
        val graphNode = graph.findNode(assign.lhs.getStringValue())
        graphNode?.updateEndIndex(graph.getIndex())
        graph.incrementIndex()
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        val graphNode: GraphNode? = graph.findNode(lhs.getStringValue())
        graphNode?.updateEndIndex(graph.getIndex())
        graph.incrementIndex()

        lhs.setGraphNode(graphNode!!)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visit(pairElem.elem)
        pairElem.setGraphNode(pairElem.elem.getGraphNode())
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        val graphNode: GraphNode = graph.findNode(arrayElem.ident)!!
        arrayElem.setArrayLocation(graphNode)
        graphNode.updateEndIndex(graph.getIndex())
        createAndSetGraphNode(arrayElem)
        graph.incrementIndex()
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

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        createAndSetGraphNode(liter)
    }

    // result of a malloc - 1 register
    // another register - take values from literal and store at malloc address
    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        for (elem in arrayLiter.elems) {
            visit(elem)
            graph.incrementIndex()
        }

        arrayLiter.setSizeGraphNode(GraphNode(graph.getIndex(), ""))

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
        if (binop.expr1.getGraphNode().getIdent() != "") {
            if (binop.expr2.getGraphNode().getIdent() != "") {
                createAndSetGraphNode(binop)
                binop.getGraphNode().addNeighbour(binop.expr1.getGraphNode())
                binop.expr1.getGraphNode().addNeighbour(binop.getGraphNode())

                binop.getGraphNode().addNeighbour(binop.expr2.getGraphNode())
                binop.expr2.getGraphNode().addNeighbour(binop.getGraphNode())
            } else {
                binop.setGraphNode(binop.expr2.getGraphNode())
            }
        } else {
            binop.setGraphNode(binop.expr1.getGraphNode())
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)

        if (unop.expr.getGraphNode().getIdent() != "") {
            createAndSetGraphNode(unop)
        } else {
            unop.setGraphNode(unop.expr.getGraphNode())
        }
    }

    private fun createAndSetGraphNode(node: AssignRHSAST) {
        val graphNode = GraphNode(graph.getIndex())
        node.setGraphNode(graphNode)
        graph.addNode(graphNode)
    }
}