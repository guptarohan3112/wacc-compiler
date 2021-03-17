package wacc_05.graph_colouring

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

open class GraphFormationVisitor(private var graph: InterferenceGraph) : ASTBaseVisitor() {
    // Visitor class which will make the interference graph (make all necessary nodes and put them
    // in the list of nodes)

    private fun getLineNo(ctx: ParserRuleContext): Int {
        return ctx.getStart().line
    }

    protected fun createAndSetGraphNode(node: AssignRHSAST) {
        if (!node.hasGraphNode()) {
            val graphNode = GraphNode(getLineNo(node.ctx))
            node.setGraphNode(graphNode)
            graph.addNode(graphNode)
        }
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        visit(decl.assignment)

        if (decl.assignment.getGraphNode().getIdent() != "") {
            val graphNode = GraphNode(getLineNo(decl.ctx), decl.varName)
            decl.setGraphNode(graphNode)
            graph.addNode(graphNode)
        } else {
            decl.setGraphNode(decl.assignment.getGraphNode())
            decl.getGraphNode().setIdentifier(decl.varName)
        }
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        // Do this, but every rhs needs to not override the graphnode if it has already been set
        val graphNode = graph.findNode(assign.lhs.getStringValue())
        graphNode?.updateEndIndex(getLineNo(assign.ctx))
        assign.rhs.setGraphNode(graphNode!!)
        visit(assign.rhs)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visit(pairElem.elem)
        val graphNode: GraphNode = pairElem.elem.getGraphNode()
        pairElem.setPairLocation(graphNode)
        graphNode.updateEndIndex(getLineNo(pairElem.ctx))
        createAndSetGraphNode(pairElem)
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        val graphNode: GraphNode = graph.findNode(arrayElem.ident)!!
        arrayElem.setArrayLocation(graphNode)
        graphNode.updateEndIndex(getLineNo(arrayElem.ctx))
        createAndSetGraphNode(arrayElem)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val graphNode: GraphNode? = graph.findNode(ident.value)
        graphNode?.updateEndIndex(getLineNo(ident.ctx))

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
        createAndSetGraphNode(arrayLiter)

        for (elem in arrayLiter.elems) {
            visit(elem)
            elem.getGraphNode().addNeighbourTwoWay(arrayLiter.getGraphNode())
        }

        arrayLiter.setSizeGraphNode(GraphNode(getLineNo(arrayLiter.ctx), ""))
        graph.addNode(arrayLiter.getSizeGraphNode())
        arrayLiter.getSizeGraphNode().addNeighbourTwoWay(arrayLiter.getGraphNode())
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        createAndSetGraphNode(liter)
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        visit(newPair.fst)
        visit(newPair.snd)
        createAndSetGraphNode(newPair)
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        // these registers have to be used at the same time so we manually add the conflict
        binop.expr1.getGraphNode().addNeighbourTwoWay(binop.expr2.getGraphNode())

        // without optimisations taking place we know that the result of the binop is always put in expr1 dest reg
        if (binop.expr1.getGraphNode().isVariable()) {
            if (binop.expr2.getGraphNode().isVariable()) {
                createAndSetGraphNode(binop)
                binop.getGraphNode().addNeighbourTwoWay(binop.expr1.getGraphNode())
                binop.getGraphNode().addNeighbourTwoWay(binop.expr2.getGraphNode())
            } else {
                binop.setGraphNode(binop.expr2.getGraphNode())
            }
        } else {
            binop.setGraphNode(binop.expr1.getGraphNode())
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)

        if (unop.expr.getGraphNode().isVariable()) {
            createAndSetGraphNode(unop)
        } else {
            unop.setGraphNode(unop.expr.getGraphNode())
        }
    }
}