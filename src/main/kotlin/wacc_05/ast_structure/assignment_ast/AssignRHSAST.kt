package wacc_05.ast_structure.assignment_ast

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.AST
import wacc_05.code_generation.utilities.*
import wacc_05.graph_colouring.GraphNode
import wacc_05.graph_colouring.InterferenceGraph
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

abstract class AssignRHSAST(val ctx: ParserRuleContext) : AST() {

    private var dest: Register? = null

    private var graphNode: GraphNode? = null

    private var addr: Int = -1
//    private var operand: Operand? = null

    fun getLineNo(): Int {
        return ctx.getStart().line
    }

    fun getOperand(): Operand {
        return if (!getDestReg().equals(InterferenceGraph.DefaultReg)) {
            getDestReg()
        } else {
            val offset: Int = addr - st().getStackPtr()
            if (getStackSize() > 1) {
                AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
            } else {
                AddressingMode.AddressingMode3(Registers.sp, Immediate(offset))
            }
        }
//        return operand ?: getDestReg()
    }

    fun setAddr() {
        this.addr = st().getStackPtr() + st().getStackPtrOffset()
    }

    fun getAddr(): Int {
        return addr
    }

    fun setAddr(addr: Int) {
        this.addr = addr
    }

    abstract fun getType(): TypeIdentifier

    fun getStackSize(): Int {
        return getType().getStackSize()
    }

    fun getDestReg(): Register {
        return graphNode!!.getOperand() as Register
    }

    fun setDestReg(reg: Register) {
        this.dest = reg
    }

    fun clearDestReg() {
        this.dest = null
    }

    fun getGraphNode(): GraphNode {
        return graphNode!!
    }

    fun setGraphNode(graphNode: GraphNode) {
        this.graphNode = graphNode
    }

}