package wacc_05.ast_structure.assignment_ast

import org.antlr.v4.runtime.ParserRuleContext
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.utilities.*
import wacc_05.graph_colouring.GraphNode
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

abstract class AssignRHSAST(val ctx: ParserRuleContext) : AST() {

    private var dest: Register? = null

    private var graphNode: GraphNode? = null

    private var addr: Int = -1

    fun hasGraphNode(): Boolean {
        return graphNode != null
    }

    fun getOperand(): Operand {
        return if (hasGraphNode() && getDestReg() != Register(-1)) {
            getDestReg()
        } else {
            if (graphNode != null) {
                val absAddr: Int? = this.getGraphNode()?.getAddr()
                if (absAddr == null) {
                    this.getGraphNode()?.setAddr(this.getStackPtr() + this.getStackPtrOffset())
                }
                val offset: Int = this.getGraphNode()?.getAddr()!! - this.getStackPtr()
                AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
            } else {
                // this ast is a param
                val node = this as ExprAST.IdentAST
                val param = st().lookupAll(node.value) as ParamIdentifier
                return AddressingMode.AddressingMode2(Registers.sp, Immediate(param.getOffset()))
            }
        }
    }

    fun setOperand(operand: Operand) {
        val graphNode: GraphNode? = this.getGraphNode()
        if (operand is Register) {
            graphNode?.setRegister(operand)
        } else {
            val btmStackFrame: Int = this.getStackPtr()
            val absAddr: Int = btmStackFrame + this.getStackPtrOffset()
            setAddr(absAddr)
        }
    }

    fun getAddr(): Int {
        return addr
    }

    fun setAddr(addr: Int) {
        this.addr = addr
        this.getGraphNode()?.setAddr(addr)
    }

    abstract fun getType(): TypeIdentifier

    fun getStackSize(): Int {
        return getType().getStackSize()
    }

    fun getDestReg(): Register {
        return graphNode!!.getRegister()
    }

    fun setDestReg(reg: Register) {
        this.dest = reg
    }

    fun clearDestReg() {
        this.dest = null
    }

    fun getGraphNode(): GraphNode? {
        return graphNode
    }

    fun setGraphNode(graphNode: GraphNode) {
        this.graphNode = graphNode
    }

}