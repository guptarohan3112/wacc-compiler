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

        if (decl.assignment.hasGraphNode() && decl.assignment.getGraphNode()!!.isVariable()) {
            val graphNode = GraphNode(getLineNo(decl.ctx), decl.varName)
            decl.setGraphNode(graphNode)
            graph.addNode(graphNode)
            graphNode.addNeighbourTwoWay(decl.assignment.getGraphNode())
        } else {
            val assignNode: GraphNode? = decl.assignment.getGraphNode()
            if (assignNode != null) {
                decl.setGraphNode(assignNode)
                decl.getGraphNode().setIdentifier(decl.varName)
            } else {
                decl.setGraphNode(GraphNode(getLineNo(decl.ctx), decl.varName))
                graph.addNode(decl.getGraphNode())
            }
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
                assign.rhs.getGraphNode()?.addNeighbourTwoWay(assign.lhs.arrElem!!.getGraphNode())

                for (elem in assign.lhs.arrElem!!.exprs) {
                    elem.getGraphNode()?.addNeighbourTwoWay(assign.rhs.getGraphNode())
                }
            }
            assign.lhs.pairElem != null -> {
                visit(assign.lhs.pairElem!!)
                visit(assign.rhs)
                assign.rhs.getGraphNode()?.addNeighbourTwoWay(assign.lhs.pairElem!!.getGraphNode())
                assign.rhs.getGraphNode()
                    ?.addNeighbourTwoWay(assign.lhs.pairElem!!.getPairLocation())
            }
            else -> {
                // Do this, but every rhs needs to not override the graphnode if it has already been set
                val identifier = assign.lhs.st().lookupAllAndCheckVisited(assign.lhs.getStringValue())

                if (identifier is VariableIdentifier) {
                    val graphNode = identifier.getGraphNode()
                    graphNode.updateEndIndex(getLineNo(assign.ctx))
                    assign.rhs.setGraphNode(graphNode)
                }
                visit(assign.rhs)
            }
        }

    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        visit(whileStat.loopExpr)
        visit(whileStat.body)
        LoopExprVisitor().visitWhile(whileStat)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        createAndSetGraphNode(pairElem)

        visit(pairElem.elem)
        val graphNode: GraphNode? = pairElem.elem.getGraphNode()
        if (graphNode != null) {
            pairElem.setPairLocation(graphNode)
        }
        graphNode?.updateEndIndex(getLineNo(pairElem.ctx))

        pairElem.getGraphNode()?.addNeighbourTwoWay(pairElem.elem.getGraphNode())
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        createAndSetGraphNode(arrayElem)

        val identifier = arrayElem.st().lookupAll(arrayElem.ident)!!
        if (identifier is VariableIdentifier) {
            val graphNode: GraphNode = identifier.getGraphNode()
            arrayElem.setArrayLocation(graphNode)
            graphNode.updateEndIndex(getLineNo(arrayElem.ctx))

            graphNode.addNeighbourTwoWay(arrayElem.getGraphNode())
        }

        for (elem in arrayElem.exprs) {
            visit(elem)
            elem.getGraphNode()?.addNeighbourTwoWay(arrayElem.getGraphNode())
            arrayElem.getArrayLocation()?.addNeighbourTwoWay(elem.getGraphNode())
        }
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val identifier = ident.st().lookupAll(ident.value)
        if (identifier is VariableIdentifier) {
            val graphNode: GraphNode = identifier.getGraphNode()
            graphNode.updateEndIndex(getLineNo(ident.ctx))

            ident.setGraphNode(graphNode)
        }
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
            elem.getGraphNode()?.addNeighbourTwoWay(arrayLiter.getGraphNode())
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
        newPair.getGraphNode()?.addNeighbourTwoWay(newPair.fst.getGraphNode())
        visit(newPair.snd)
        newPair.getGraphNode()?.addNeighbourTwoWay(newPair.snd.getGraphNode())
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        // these registers have to be used at the same time so we manually add the conflict
        binop.expr1.getGraphNode()?.addNeighbourTwoWay(binop.expr2.getGraphNode())

        // without optimisations taking place we know that the result of the binop is always put in expr1 dest reg
        if (binop.expr1.hasGraphNode() && binop.expr1.getGraphNode()!!.isVariable()) {
            if (binop.expr2.hasGraphNode() && binop.expr2.getGraphNode()!!.isVariable()) {
                createAndSetGraphNode(binop)
                binop.getGraphNode()?.addNeighbourTwoWay(binop.expr1.getGraphNode())
                binop.getGraphNode()?.addNeighbourTwoWay(binop.expr2.getGraphNode())
            } else {
                if (binop.expr2.hasGraphNode() && binop.operator != "*") {
                    binop.setGraphNode(binop.expr2.getGraphNode()!!)
                } else {
                    createAndSetGraphNode(binop)
                    binop.getGraphNode()?.addNeighbourTwoWay(binop.expr1.getGraphNode())
                }
            }
        } else {
            if (binop.expr1.hasGraphNode() && binop.operator != "*") {
                binop.setGraphNode(binop.expr1.getGraphNode()!!)
            } else {
                createAndSetGraphNode(binop)
                binop.getGraphNode()?.addNeighbourTwoWay(binop.expr2.getGraphNode())
            }
        }

        if (!binop.hasGraphNode2() && binop.operator == "*") {
            val graphNode2 = GraphNode(getLineNo(binop.ctx))
            binop.setGraphNode2(graphNode2)
            graph.addNode(graphNode2)
            graphNode2.addNeighbourTwoWay(binop.getGraphNode())
            graphNode2.addNeighbourTwoWay(binop.expr1.getGraphNode())
            graphNode2.addNeighbourTwoWay(binop.expr2.getGraphNode())
            binop.getGraphNode()?.addNeighbourTwoWay(binop.expr1.getGraphNode())
        }
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        createAndSetGraphNode(funcCall)
        for (expr in funcCall.args) {
            visit(expr)
        }
    }

    override fun visitForAST(forLoop: StatementAST.ForAST) {
        super.visitForAST(forLoop)
        LoopExprVisitor().visitFor(forLoop)
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        visit(unop.expr)

        if (unop.expr.hasGraphNode() && unop.expr.getGraphNode()!!.isVariable()) {
            createAndSetGraphNode(unop)
            unop.getGraphNode()?.addNeighbourTwoWay(unop.expr.getGraphNode())
        } else {
            if (unop.expr.hasGraphNode()) {
                unop.setGraphNode(unop.expr.getGraphNode()!!)
            } else {
                createAndSetGraphNode(unop)
            }
        }
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        val leftHand: AssignLHSAST = read.lhs
        val leftHandIdent: ExprAST.IdentAST? = leftHand.ident
        val leftHandPairElem: PairElemAST? = leftHand.pairElem
        val leftHandArrElem: ExprAST.ArrayElemAST? = leftHand.arrElem

        if (leftHandIdent != null) {
            visitIdentAST(leftHandIdent)
        } else if (leftHandPairElem != null) {
            visitPairElemAST(leftHandPairElem)
        }
    }

    override fun visitMapAST(mapAST: ExprAST.MapAST) {
        visit(mapAST.assignRHS)

        mapAST.lengthReg = GraphNode(mapAST.ctx.getStart().line)
        mapAST.spaceReg = GraphNode(mapAST.ctx.getStart().line)
        mapAST.arrLocation = GraphNode(mapAST.ctx.getStart().line)
        mapAST.arrIndexReg = GraphNode(mapAST.ctx.getStart().line)
        mapAST.arrayElemReg = GraphNode(mapAST.ctx.getStart().line)
        mapAST.sizeDest = GraphNode(mapAST.ctx.getStart().line)

        graph.addNode(mapAST.lengthReg!!)
        graph.addNode(mapAST.spaceReg!!)
        graph.addNode(mapAST.arrLocation!!)
        graph.addNode(mapAST.arrIndexReg!!)
        graph.addNode(mapAST.arrayElemReg!!)
        graph.addNode(mapAST.sizeDest!!)

        mapAST.lengthReg?.addNeighbourTwoWay(mapAST.spaceReg)
        mapAST.lengthReg?.addNeighbourTwoWay(mapAST.arrIndexReg)
        mapAST.lengthReg?.addNeighbourTwoWay(mapAST.arrLocation)
        mapAST.lengthReg?.addNeighbourTwoWay(mapAST.arrayElemReg)
        mapAST.arrLocation?.addNeighbourTwoWay(mapAST.arrayElemReg)
        mapAST.arrLocation?.addNeighbourTwoWay(mapAST.arrIndexReg)
        mapAST.arrayElemReg?.addNeighbourTwoWay(mapAST.arrIndexReg)
        mapAST.arrLocation?.addNeighbourTwoWay(mapAST.sizeDest)

        mapAST.setGraphNode(mapAST.arrLocation!!)
    }
}