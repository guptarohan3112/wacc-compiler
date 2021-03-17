package wacc_05.graph_colouring

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.ASTBaseVisitor
import wacc_05.ast_structure.ExprAST
import wacc_05.ast_structure.StatementAST
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

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

        if (decl.assignment.getGraphNode().isVariable()) {
            val graphNode = GraphNode(getLineNo(decl.ctx), decl.varName)
            decl.setGraphNode(graphNode)
            graph.addNode(graphNode)
        } else {
            decl.setGraphNode(decl.assignment.getGraphNode())
            decl.getGraphNode().setIdentifier(decl.varName)
        }

        val identifier: VariableIdentifier = decl.st().lookupAll(decl.varName) as VariableIdentifier
        identifier.setGraphNode(decl.getGraphNode())
        identifier.visitedNow()
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        when {
            assign.lhs.arrElem != null -> {
                visit(assign.lhs.arrElem!!)
                visit(assign.rhs)
                assign.rhs.getGraphNode().addNeighbourTwoWay(assign.lhs.arrElem!!.getGraphNode())

                for (elem in assign.lhs.arrElem!!.exprs) {
                    elem.getGraphNode().addNeighbourTwoWay(assign.rhs.getGraphNode())
                }
            }
            assign.lhs.pairElem != null -> {
                visit(assign.lhs.pairElem!!)
                visit(assign.rhs)
                assign.rhs.getGraphNode().addNeighbourTwoWay(assign.lhs.pairElem!!.getGraphNode())
                assign.rhs.getGraphNode()
                    .addNeighbourTwoWay(assign.lhs.pairElem!!.getPairLocation())
            }
            else -> {
                // Do this, but every rhs needs to not override the graphnode if it has already been set
                val graphNode =
                    (assign.lhs.st()
                        .lookupAllAndCheckVisited(assign.lhs.getStringValue())!! as VariableIdentifier).getGraphNode()
                graphNode.updateEndIndex(getLineNo(assign.ctx))
                assign.rhs.setGraphNode(graphNode)
                visit(assign.rhs)
            }
        }

    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        createAndSetGraphNode(pairElem)

        visit(pairElem.elem)
        val graphNode: GraphNode = pairElem.elem.getGraphNode()
        pairElem.setPairLocation(graphNode)
        graphNode.updateEndIndex(getLineNo(pairElem.ctx))

        pairElem.getGraphNode().addNeighbourTwoWay(pairElem.elem.getGraphNode())
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        createAndSetGraphNode(arrayElem)
        val graphNode: GraphNode =
            (arrayElem.st().lookupAll(arrayElem.ident)!! as VariableIdentifier).getGraphNode()
        arrayElem.setArrayLocation(graphNode)
        graphNode.updateEndIndex(getLineNo(arrayElem.ctx))

        graphNode.addNeighbourTwoWay(arrayElem.getGraphNode())

        for (elem in arrayElem.exprs) {
            visit(elem)
            elem.getGraphNode().addNeighbourTwoWay(arrayElem.getGraphNode())
            arrayElem.getArrayLocation().addNeighbourTwoWay(elem.getGraphNode())
        }
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val graphNode: GraphNode =
            (ident.st().lookupAll(ident.value)!! as VariableIdentifier).getGraphNode()
        graphNode.updateEndIndex(getLineNo(ident.ctx))

        ident.setGraphNode(graphNode)
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
        createAndSetGraphNode(newPair)
        visit(newPair.fst)
        newPair.getGraphNode().addNeighbourTwoWay(newPair.fst.getGraphNode())
        visit(newPair.snd)
        newPair.getGraphNode().addNeighbourTwoWay(newPair.snd.getGraphNode())
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

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        createAndSetGraphNode(funcCall)
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)

        if (unop.expr.getGraphNode().isVariable()) {
            createAndSetGraphNode(unop)
            unop.getGraphNode().addNeighbourTwoWay(unop.expr.getGraphNode())
        } else {
            unop.setGraphNode(unop.expr.getGraphNode())
        }
    }
}