package wacc_05.code_generation

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.code_generation.instructions.*
import wacc_05.code_generation.instructions.LabelInstruction.Companion.getUniqueLabel
import wacc_05.code_generation.utilities.*
import wacc_05.graph_colouring.InterferenceGraph
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

open class TranslatorVisitor(
    private val representation: AssemblyRepresentation,
    private val graph: InterferenceGraph
) :
    ASTBaseVisitor() {

    private val MAX_STACK_SIZE: Int = 1024
    private val START_OFFSET: Int = 4
    private val ONE_BYTE: Int = 1
    private val FOUR_BYTES: Int = 4

    /* UTILITY METHODS USED BY DIFFERENT VISIT METHODS IN THIS VISITOR
       ---------------------------------------------------------------
     */

    // Push the linking register and calculate the space on the stack needed
    private fun startNewBody(bodyInScope: StatementAST): Int {
        representation.addMainInstr(PushInstruction(Registers.lr))
        return calculateStackSize(bodyInScope)
    }

    // Uses a stackSizeVisitor to calculate the space needed on the stack
    private fun calculateStackSize(bodyInScope: StatementAST): Int {
        // Calculate stack size for scope and decrement the stack pointer accordingly
        val stackSizeCalculator = StackSizeVisitor()
        val stackSize: Int = stackSizeCalculator.getStackSize(bodyInScope, graph)
        decrementAssemblySP(stackSize)
        return stackSize
    }

    private fun decrementAssemblySP(stackSize: Int) {
        var tmp: Int = stackSize
        while (tmp > 0) {
            representation.addMainInstr(
                SubtractInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(tmp.coerceAtMost(MAX_STACK_SIZE))
                )
            )
            tmp -= MAX_STACK_SIZE
        }
    }

    // Sets up the internal representation of the stack pointer when moving into the inner scope
    private fun setUpInnerScope(stat: StatementAST, child: StatementAST): Int {
        val currSp: Int = stat.getStackPtr()
        child.setStackPtr(child.getStackPtr() + currSp)

        val stackSize: Int = calculateStackSize(child)
        child.setStackPtr(child.getStackPtr() - stackSize)
        return stackSize
    }

    // Increment and adjust the stack pointer
    private fun restoreStackPointer(node: AST, stackSize: Int) {
        var tmp: Int = stackSize
        while (tmp > 0) {
            representation.addMainInstr(
                AddInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(tmp.coerceAtMost(MAX_STACK_SIZE))
                )
            )
            tmp -= MAX_STACK_SIZE
        }
        node.setStackPtr(node.getStackPtr() + stackSize)
    }

    // A helper method for adding "B" to "STR" if given
    private fun getStoreInstruction(
        reg: Register,
        addr: AddressingMode.AddressingMode2,
        type: TypeIdentifier
    ): StoreInstruction {
        return if (type.getStackSize() == 1) {
            StoreInstruction(reg, addr, Condition.B)
        } else {
            StoreInstruction(reg, addr)
        }
    }

    // Calculate the stack pointer to find an identifier
    private fun calculateIdentSpOffset(identValue: String, scope: AST, paramOffset: Int): Int {
        val identObj: IdentifierObject = scope.st().lookUpAllAndCheckAllocation(identValue)!!

        var spOffset = 0
        if (identObj is VariableIdentifier) {
            val identOffset: Int = identObj.getAddr()
            val sp: Int = scope.getStackPtr()
            spOffset = identOffset - sp
        } else if (identObj is ParamIdentifier) {
            spOffset = identObj.getOffset() + scope.getStackSizeAllocated() + paramOffset
        }

        return spOffset
    }

    // A method that sets the stackSizeAllocated field of an inner scope
    private fun innerScopeStackAllocation(stat: AST, child: AST, stackSize: Int) {
        val currentStackSpace: Int = stat.getStackSizeAllocated()
        child.setStackSizeAllocated(currentStackSpace + stackSize)
    }

    // Helper method that visits anything that requires making a new inner scope
    private fun visitInnerScope(stat: StatementAST, innerScope: StatementAST) {
        val stackSize = setUpInnerScope(stat, innerScope)

        // Indicate how much stack space has been allocated and visit the branch
        innerScopeStackAllocation(stat, innerScope, stackSize)
        visit(innerScope)

        // Restore the stack pointer for the 'else' branch
        restoreStackPointer(innerScope, stackSize)
    }

    private fun operandAllocation(register: Register, ast: AssignRHSAST): Operand {
        return if (!register.equals(InterferenceGraph.DefaultReg)) {
            register
        } else {
            val size = ast.getStackSize()

            val mode = if (size == 1) {
                AddressingMode.AddressingMode3(Registers.sp, Immediate(ast.getStackPtrOffset()))
            } else {
                AddressingMode.AddressingMode2(Registers.sp, Immediate(ast.getStackPtrOffset()))
            }

            ast.updatePtrOffset(size)
//            ast.setOperand(mode)

            mode
        }
    }

    private fun pushAndCompare(reg: AddressingMode, imm: Int) {
        val dest: Register = Registers.r11
        representation.addMainInstr(PushInstruction(dest))
        representation.addMainInstr(MoveInstruction(dest, reg))
        representation.addMainInstr(CompareInstruction(dest, Immediate(imm)))
        representation.addMainInstr(PopInstruction(dest))
    }

    private fun placeInRegisterOrStack(
        destination: Operand,
        mode: AddressingMode,
        byteSize: Boolean
    ) {
        if (destination is AddressingMode) {
            val reg: Register = Registers.r11
            representation.addMainInstr(PushInstruction(reg))
            representation.addMainInstr(LoadInstruction(reg, mode))
            if (byteSize) {
                representation.addMainInstr(
                    StoreInstruction(
                        reg,
                        destination as AddressingMode.AddressingMode2,
                        Condition.B
                    )
                )
            } else {
                representation.addMainInstr(
                    StoreInstruction(
                        reg,
                        destination as AddressingMode.AddressingMode2
                    )
                )
            }
            representation.addMainInstr(PopInstruction(reg))
        } else {
            representation.addMainInstr(LoadInstruction(destination as Register, mode))
        }
    }

    /* MAIN VISIT METHODS (OVERIDDEN FROM THE BASE VISITOR CLASS)
       ---------------------------------------------------------
     */

    override fun visitProgramAST(prog: ProgramAST) {
        // Generate assembly code for each of the functions declared in the program
        for (func in prog.functionList) {
            visit(func)
        }

        // Generate code for the main body
        representation.addMainInstr(LabelInstruction("main"))
        val stackSize: Int = startNewBody(prog.stat)

        // Decrement the stack pointer value and update the symbol table with this sp
        val currStkPtr: Int = prog.stat.getStackPtr()
        prog.stat.setStackPtr(currStkPtr - stackSize)

        // Generate assembly code for the body statement
        visit(prog.stat)

        // Increment internal representation of the stack pointer and increment the stack pointer on assembly
        restoreStackPointer(prog.stat, stackSize)

        // Return the exit code and pop the program counter
        representation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("0")
            )
        )
        representation.addMainInstr(PopInstruction(Registers.pc))
    }

    override fun visitFunctionAST(func: FunctionAST) {
        // Create the label for the function
        val funcLabel = "f_${func.funcName}"
        representation.addMainInstr(LabelInstruction(funcLabel))

        // Generate code to allocate space on the stack for local variables in the function
        val stackSize: Int = startNewBody(func.body)

        // Store how amount of allocated space for local variables on the corresponding function identifier
        val funcIdent = func.lookupFunction(func.funcName)!!
        funcIdent.setStackSize(stackSize)

        // Record the space that has been allocated on the stack for the function scope
        func.body.setStackSizeAllocated(stackSize)

        if (func.paramList != null) {
            visitAndUpdateParams(func.paramList)
        }

        // Generate assembly code for the body statement
        visit(func.body)

        representation.addMainInstr(PopInstruction(Registers.pc))
    }

    private fun visitAndUpdateParams(list: ParamListAST) {
        val symbolTable: SymbolTable = list.st()
        var offset = START_OFFSET

        // Store the offset of the parameter relative to the stack address of the first parameter
        for (param in list.paramList) {
            val paramIdent: ParamIdentifier = symbolTable.lookup(param.name) as ParamIdentifier
            paramIdent.setOffset(offset)
            offset += param.getStackSize(symbolTable)
        }
    }

    // There is no assembly code that needs to be generated for parameters.
    // Setting the correct offset of the parameter is done in visitAndUpdateParams
    override fun visitParamListAST(list: ParamListAST) {
        return
    }

    // There is no assembly code that needs to be generated for parameters.
    // Setting the correct offset of the parameter is done in visitAndUpdateParams
    override fun visitParamAST(param: ParamAST) {
        return
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        return
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        // Generate code for the right hand side of the declaration
        val scope: SymbolTable = decl.st()
        val currOffset = scope.getStackPtrOffset()
        visit(decl.assignment)

        // Reset the stack pointer offset if a function call overwrote this
        if (decl.assignment is FuncCallAST) {
            scope.updatePtrOffset(currOffset)
        }

        // Obtain the register where the evaluation of the right hand side lies
        val dest: Operand = decl.assignment.getOperand()

        // set the operand on the decl ast

        // Store the value at an available register
//        val mode = if (currOffset == 0) {
//            AddressingMode.AddressingMode2(Registers.sp)
//        } else {
//            AddressingMode.AddressingMode2(Registers.sp, Immediate(currOffset))
//        }

//        representation.addMainInstr(
//            getStoreInstruction(
//                dest,
//                mode,
//                decl.assignment.getType()
//            )
//        )

        // Set the absolute stack address of the variable in the corresponding variable identifier
        if (dest is AddressingMode) {
            val boundaryAddr = scope.getStackPtr()
            val varObj: VariableIdentifier = scope.lookupAll(decl.varName) as VariableIdentifier
            varObj.setAddr(boundaryAddr + currOffset)

            // Indicate that the variable identifier has now been allocated
            varObj.allocatedNow()
        }
//
//        varObj.setAddr(boundaryAddr + currOffset)
//

//
//        // Update the amount of space taken up on the stack relative to the boundary and the current stack frame
//        val size = decl.type.getStackSize()
//        scope.updatePtrOffset(size)
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        // Generate code for the right hand side of the statement
        visit(assign.rhs)
        val dest: Operand = assign.rhs.getOperand()

        val lhs: AssignLHSAST = assign.lhs

        when {
            lhs.ident != null -> {
                val lhsLocation = lhs.getGraphNode().getOperand()

                if (!lhsLocation.equals(InterferenceGraph.DefaultReg)) {
                    representation.addMainInstr(MoveInstruction(lhsLocation as Register, dest))
                } else {
                    val offset: Int = calculateIdentSpOffset(lhs.getStringValue(), assign, 0)
                    if (dest is AddressingMode) {
                        representation.addMainInstr(PushInstruction(Registers.r11))
                        representation.addMainInstr(MoveInstruction(Registers.r11, dest))
                        // TODO: Update to check for size of lhs
                        representation.addMainInstr(
                            StoreInstruction(
                                Registers.r11,
                                AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
                            )
                        )
                        representation.addMainInstr(PopInstruction(Registers.r11))
                    } else {
                        representation.addMainInstr(
                            StoreInstruction(
                                dest as Register,
                                lhsLocation as AddressingMode.AddressingMode2
                            )
                        )
                    }
                }
            }
            lhs.arrElem != null -> {
                val arrElem = lhs.arrElem!!

                // load the address of the elem into a register
                visitArrayElemFstPhase(arrElem)
                val arrDest: Operand = arrElem.getOperand()

                // store value into address
                if (arrDest is AddressingMode) {
                    if (dest is AddressingMode) {
                        representation.addMainInstr(PushInstruction(Registers.r12))
                        representation.addMainInstr(LoadInstruction(Registers.r12, dest))
                        representation.addMainInstr(
                            StoreInstruction(
                                Registers.r12,
                                AddressingMode.AddressingMode2(Registers.r11)
                            )
                        )
                        representation.addMainInstr(PopInstruction(Registers.r12))
                        representation.addMainInstr(PopInstruction(Registers.r11))
                    } else {
                        representation.addMainInstr(
                            StoreInstruction(
                                dest as Register,
                                AddressingMode.AddressingMode2(Registers.r11)
                            )
                        )
                        representation.addMainInstr(PopInstruction(Registers.r11))
                    }
                } else {
                    if (dest is AddressingMode) {
                        representation.addMainInstr(PushInstruction(Registers.r12))
                        representation.addMainInstr(LoadInstruction(Registers.r12, dest))
                        representation.addMainInstr(
                            StoreInstruction(
                                Registers.r12,
                                AddressingMode.AddressingMode2(arrDest as Register)
                            )
                        )
                        representation.addMainInstr(PopInstruction(Registers.r11))
                    } else {
                        representation.addMainInstr(
                            StoreInstruction(
                                dest as Register,
                                AddressingMode.AddressingMode2(arrDest as Register)
                            )
                        )
                    }
                }
            }
            lhs.pairElem != null -> {
                val pairElem: PairElemAST = lhs.pairElem!!

                // load the address of the pair elem into a register
                visitPairElemFstPhase(pairElem)
                val pairLocation: Register = pairElem.getDestReg()

                // write to this address to update the value
                if (dest is AddressingMode) {
                    representation.addMainInstr(
                        LoadInstruction(
                            Registers.r3,
                            dest
                        )
                    )
                } else {
                    representation.addMainInstr(
                        StoreInstruction(
                            dest as Register,
                            AddressingMode.AddressingMode2(pairLocation)
                        )
                    )
                }
            }
            else -> {
                // Do nothing
            }
        }
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        visitInnerScope(begin, begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        var type: TypeIdentifier? = TypeIdentifier()
        var reg: Operand? = null

        val leftHand: AssignLHSAST = read.lhs
        val leftHandIdent: ExprAST.IdentAST? = leftHand.ident
        val leftHandPairElem: PairElemAST? = leftHand.pairElem

        // Set the destination register and the type
        if (leftHandIdent != null) {
            visitIdentForRead(leftHandIdent)
            reg = operandAllocation(leftHandIdent.getDestReg(), leftHandIdent)
            type = leftHandIdent.getType()
        } else if (leftHandPairElem != null) {
            visitPairElemAST(leftHandPairElem)
            reg = operandAllocation(leftHandPairElem.getDestReg(), leftHandPairElem)
            type = leftHandPairElem.getType()
        }

        // Move the value in the destination register into r0
        representation.addMainInstr(MoveInstruction(Registers.r0, reg!!))

        // Call the relevant primitive function (depending on the type)
        if (type == TypeIdentifier.INT_TYPE) {
            representation.addPInstr(PInstruction.p_read_int(representation))
        }
        if (type == TypeIdentifier.CHAR_TYPE) {
            representation.addPInstr(PInstruction.p_read_char(representation))
        }
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        // Evaluate the exiting instruction and get destination register
        visit(exit.expr)
        val dest: Operand = operandAllocation(exit.expr.getDestReg(), exit.expr)

        // Move contents of the register in r0 for calling exit
        representation.addMainInstr(MoveInstruction(Registers.r0, dest))
        representation.addMainInstr(BranchInstruction("exit", Condition.L))
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        // Evaluate the expression that you are freeing and obtain the destination register
        visit(free.expr)
        val dest: Operand = operandAllocation(free.expr.getDestReg(), free.expr)

        // Move the contents of the destination register into r0
        representation.addMainInstr(MoveInstruction(Registers.r0, dest))

        // Add the primitive instruction corresponding to the type of the expression
        if (free.expr.getType() is TypeIdentifier.ArrayIdentifier) {
            representation.addPInstr(PInstruction.p_free_array(representation))
        } else {
            representation.addPInstr(PInstruction.p_free_pair(representation))
        }
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Evaluation of the condition expression
        val condition: ExprAST = ifStat.condExpr
        visit(condition)

        // Condition checking
        val destination: Operand = operandAllocation(condition.getDestReg(), condition)
        if (destination is AddressingMode) {
            representation.addMainInstr(PushInstruction(Registers.r11))
            representation.addMainInstr(MoveInstruction(Registers.r11, destination))
            representation.addMainInstr(CompareInstruction(Registers.r11, Immediate(0)))
            representation.addMainInstr(PopInstruction(Registers.r11))
        } else {
            representation.addMainInstr(CompareInstruction(destination as Register, Immediate(0)))
        }

        // Branch off to the 'else' body if the condition evaluated to false
        val elseLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(BranchInstruction(elseLabel.getLabel(), Condition.EQ))

        // Visit the 'then' branch
        visitInnerScope(ifStat, ifStat.thenStat)

        // Unconditionally jump to the label of whatever follows the if statement in the program
        val nextLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(BranchInstruction(nextLabel.getLabel()))

        // Label and assembly for the 'else' body, starting with updating stack pointer and allocating any stack space
        representation.addMainInstr(elseLabel)
        // Visit the 'else' branch
        visitInnerScope(ifStat, ifStat.elseStat)

        // Make label for whatever follows the if statement
        representation.addMainInstr(nextLabel)
    }

    // Call and add IO instructions
    override fun visitPrintAST(print: StatementAST.PrintAST) {
        // Evaluate expression to be printed and obtain the register where the result is held
        visit(print.expr)
        val reg: Operand = operandAllocation(print.expr.getDestReg(), print.expr)
        representation.addMainInstr(MoveInstruction(Registers.r0, reg))

        val type = if (print.expr is ExprAST.ArrayElemAST) {
            print.expr.getElemType()
        } else {
            print.expr.getType()
        }

        when (type) {
            is TypeIdentifier.IntIdentifier -> {
                representation.addPInstr(PInstruction.p_print_int(representation))
            }
            is TypeIdentifier.BoolIdentifier -> {
                representation.addPInstr(PInstruction.p_print_bool(representation))
            }
            is TypeIdentifier.CharIdentifier -> {
                representation.addMainInstr(BranchInstruction("putchar", Condition.L))
            }
            is TypeIdentifier.StringIdentifier, TypeIdentifier.ArrayIdentifier(
                TypeIdentifier.CHAR_TYPE,
                0
            ) -> {
                representation.addPInstr(PInstruction.p_print_string(representation))
            }
            is TypeIdentifier.PairIdentifier, is TypeIdentifier.PairLiterIdentifier, is TypeIdentifier.ArrayIdentifier -> {
                representation.addPInstr(PInstruction.p_print_reference(representation))
            }
        }

        if (print.newLine) {
            representation.addPInstr(PInstruction.p_print_ln(representation))
        }
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        // Evaluate the expression you want to return and access the register that holds the value
        visit(ret.expr)
        val dest: Operand = operandAllocation(ret.expr.getDestReg(), ret.expr)

        // Move the value into r0 and pop the program counter
        representation.addMainInstr(MoveInstruction(Registers.r0, dest))

        // Restore the stack pointer depending on how much stack space has been allocated thus far
        restoreStackPointer(ret, ret.getStackSizeAllocated())
        representation.addMainInstr(PopInstruction(Registers.pc))
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        visit(seq.stat1)
        visit(seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        // Unconditional branch to check the loop condition
        val condLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(BranchInstruction(condLabel.getLabel()))

        // Label for loop body
        val bodyLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(bodyLabel)

        visitInnerScope(whileStat, whileStat.body)

        // Label for condition checking
        representation.addMainInstr(condLabel)

        // Evaluate the looping conditional expression
        visit(whileStat.loopExpr)
        val reg: Operand = operandAllocation(whileStat.loopExpr.getDestReg(), whileStat.loopExpr)

        if (reg is AddressingMode) {
            pushAndCompare(reg, 1)
        } else {
            // Comparison and jump if equal
            representation.addMainInstr(
                CompareInstruction(
                    reg as Register,
                    Immediate(1)
                )
            )
        }

        representation.addMainInstr(BranchInstruction(bodyLabel.getLabel(), Condition.EQ))
    }

    override fun visitForAST(forLoop: StatementAST.ForAST) {
        val body: StatementAST = forLoop.body

        // Ensure that the stack pointer for the loop body is in the right place initially
        val currSp: Int = forLoop.getStackPtr()
        body.setStackPtr(body.getStackPtr() + currSp)

        // Allocate stack space for all of the local variables and looping variable. Update stack pointer accordingly
        val stackSizeCalculator = StackSizeVisitor()
        val stackSize: Int = stackSizeCalculator.getStackSize(body, graph)
        val updatedStackSize: Int = stackSize + FOUR_BYTES
        decrementAssemblySP(updatedStackSize)
        body.setStackPtr(body.getStackPtr() - updatedStackSize)

        // Update how much stack space has been allocated so far
        innerScopeStackAllocation(forLoop, body, updatedStackSize)

        // Generate assembly code for the declaration of the looping variable
        visit(forLoop.decl)

        // Testing of loop expression, branch off to whatever is next if there is failure
        val condLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(condLabel)

        visit(forLoop.loopExpr)
        val reg: Operand = operandAllocation(forLoop.loopExpr.getDestReg(), forLoop.loopExpr)

        if (reg is AddressingMode) {
            pushAndCompare(reg, 0)
        } else {
            representation.addMainInstr(
                CompareInstruction(
                    reg as Register,
                    Immediate(0)
                )
            )
        }

        val nextLabel: LabelInstruction = getUniqueLabel()
        representation.addMainInstr(BranchInstruction(nextLabel.getLabel(), Condition.EQ))

        // Generate assembly code for the body. Update looping variable
        visit(body)
        visit(forLoop.update)

        // Update looping variable
        representation.addMainInstr(BranchInstruction(condLabel.getLabel()))

        representation.addMainInstr(nextLabel)

        restoreStackPointer(body, updatedStackSize)
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        val intValue = Integer.parseInt(liter.sign + liter.value)
        val mode: AddressingMode = AddressingMode.AddressingLabel("$intValue")

        val dest: Operand = operandAllocation(liter.getDestReg(), liter)

        placeInRegisterOrStack(dest, mode, false)
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        val intValue = liter.getValue()

        val dest: Operand = operandAllocation(liter.getDestReg(), liter)

        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel("$intValue"), true)
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        val dest: Operand = operandAllocation(liter.getDestReg(), liter)

        if (liter.value == "'\\0'") {
            placeInRegisterOrStack(dest, AddressingMode.AddressingLabel("0"), true)
        } else {
            placeInRegisterOrStack(
                dest,
                AddressingMode.AddressingLabel("\'${liter.getValue()}\'"),
                true
            )
        }
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        val label = MessageLabelInstruction.getUniqueLabel(liter.value)
        val dest: Operand = operandAllocation(liter.getDestReg(), liter)

        representation.addDataInstr(label)

        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel(label.getLabel()), false)
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        /* a pair liter is null so will have address zero
         * so we load the value zero into a destination register */
        val dest: Operand = operandAllocation(liter.getDestReg(), liter)
        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel("0"), false)
    }

    private fun visitIdentForRead(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, true)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, false)
    }

    private fun visitIdentGeneral(ident: ExprAST.IdentAST, read: Boolean) {
//        val dest: Operand = operandAllocation(ident.getDestReg(), ident)
//
//        if (dest is AddressingMode) {
//            representation.addMainInstr(PushInstruction(Registers.r11))
//
//            val paramOffset = ident.getParamOffset()
//            val spOffset: Int = calculateIdentSpOffset(ident.value, ident, paramOffset)
//
//            if (read) {
//                representation.addMainInstr(
//                    AddInstruction(
//                        ident.getDestReg(),
//                        Registers.sp,
//                        Immediate(spOffset)
//                    )
//                )
//            } else {
//                val type = ident.getType()
//                var mode: AddressingMode =
//                    AddressingMode.AddressingMode2(Registers.sp, Immediate(spOffset))
//
//                if (type is TypeIdentifier.BoolIdentifier || type is TypeIdentifier.CharIdentifier) {
//                    mode = AddressingMode.AddressingMode3(Registers.sp, Immediate(spOffset))
//                }
//                representation.addMainInstr(LoadInstruction(ident.getDestReg(), mode))
//            }
//        }
        return
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        // load the address of the array elem
        visitArrayElemFstPhase(arrayElem)
        val operand: Operand = arrayElem.getOperand()

        val dest: Register = if (operand is AddressingMode) {
            Registers.r11
        } else {
            operand as Register
        }

        // load the value at the address into the destination register
        val type: TypeIdentifier = arrayElem.getElemType()
        when (type.getStackSize()) {
            ONE_BYTE -> {
                representation.addMainInstr(
                    LoadInstruction(
                        dest,
                        AddressingMode.AddressingMode3(dest, Immediate(0))
                    )
                )
            }
            else -> {
                representation.addMainInstr(
                    LoadInstruction(
                        dest,
                        AddressingMode.AddressingMode2(dest)
                    )
                )
            }
        }

        if (operand is AddressingMode) {
            representation.addMainInstr(PopInstruction(Registers.r11))
        }
    }

    private fun visitArrayElemFstPhase(arrayElem: ExprAST.ArrayElemAST) {
        // Move the start of the array into dest register
        val dest: Operand = arrayElem.getOperand()

        var arrLocation: Operand = arrayElem.getArrayLocation().getOperand()

        if (arrLocation.equals(InterferenceGraph.DefaultReg)) {
            val offset = calculateIdentSpOffset(arrayElem.ident, arrayElem, 0)
            arrLocation = AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
        }

        val reg: Register

        if (dest is AddressingMode) {
            reg = Registers.r11
            representation.addMainInstr(PushInstruction(reg))
        } else {
            reg = dest as Register
        }

        representation.addMainInstr(MoveInstruction(reg, arrLocation))

        for (expr in arrayElem.exprs) {
            representation.addMainInstr(LoadInstruction(reg, AddressingMode.AddressingMode2(reg)))

            visit(expr)
            val exprDest: Operand = expr.getOperand()

            representation.addMainInstr(MoveInstruction(Registers.r0, exprDest))
            representation.addMainInstr(MoveInstruction(Registers.r1, reg))
            representation.addPInstr(PInstruction.p_check_array_bounds(representation))

            representation.addMainInstr(
                AddInstruction(
                    reg,
                    reg,
                    Immediate(TypeIdentifier.ADDR_SIZE)
                )
            )

            val type: TypeIdentifier = arrayElem.getElemType()
            when (type.getStackSize()) {
                FOUR_BYTES -> {
                    if (exprDest is AddressingMode) {
                        val temp: Register = Registers.r12
                        representation.addMainInstr(PushInstruction(temp))
                        representation.addMainInstr(MoveInstruction(temp, exprDest))
                        representation.addMainInstr(
                            AddInstruction(
                                reg,
                                reg,
                                ShiftOperand(temp, ShiftOperand.Shift.LSL, 2)
                            )
                        )
                        representation.addMainInstr(PopInstruction(temp))
                    } else {
                        representation.addMainInstr(
                            AddInstruction(
                                reg,
                                reg,
                                ShiftOperand(exprDest as Register, ShiftOperand.Shift.LSL, 2)
                            )
                        )
                    }
                }
                else -> {
                    representation.addMainInstr(AddInstruction(reg, reg, exprDest))
                }
            }
        }
    }


    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        // Evaluate the single operand and get the register holding the result
        visit(unop.expr)

        when (unop.operator) {
            "-" -> visitNeg(unop)
            "!" -> visitNot(unop)
            "len" -> visitLen(unop)
        }
    }

    private fun visitLen(unop: ExprAST.UnOpAST) {
        val dest: Operand = unop.getOperand()
        val arrLocation = unop.expr.getOperand()

        // load the value of the length into the destination register
        if (dest is AddressingMode) {
            val reg = Registers.r11
            representation.addMainInstr(PushInstruction(Registers.r11))

            if (arrLocation is AddressingMode) {
                representation.addMainInstr(LoadInstruction(reg, arrLocation))
            } else {
                representation.addMainInstr(
                    LoadInstruction(
                        reg,
                        AddressingMode.AddressingMode2(arrLocation as Register)
                    )
                )
            }

            representation.addMainInstr(PopInstruction(Registers.r11))
        } else {
            if (arrLocation is AddressingMode) {
                representation.addMainInstr(
                    LoadInstruction(dest as Register, arrLocation)
                )
            } else {
                representation.addMainInstr(
                    LoadInstruction(
                        dest as Register,
                        AddressingMode.AddressingMode2(arrLocation as Register)
                    )
                )
            }
        }
    }

    private fun visitNot(unop: ExprAST.UnOpAST) {
        val dest: Operand = unop.getOperand()
        val exprDest: Operand = unop.expr.getOperand()


        if (dest is AddressingMode) {
            representation.addMainInstr(PushInstruction(Registers.r11))

            if (exprDest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(Registers.r11, exprDest))
            } else {
                representation.addMainInstr(
                    LoadInstruction(
                        Registers.r11,
                        AddressingMode.AddressingMode2(exprDest as Register)
                    )
                )
            }

            representation.addMainInstr(EorInstruction(Registers.r11, Registers.r11, Immediate(1)))
            representation.addMainInstr(StoreInstruction(Registers.r11, dest as AddressingMode.AddressingMode2))
            representation.addMainInstr(PopInstruction(Registers.r11))
        } else {
            if (exprDest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(dest as Register, exprDest))
                representation.addMainInstr(EorInstruction(dest, dest, Immediate(1)))
            } else {
                representation.addMainInstr(EorInstruction(dest as Register, exprDest as Register, Immediate(1)))
            }
        }
    }

    private fun visitNeg(unop: ExprAST.UnOpAST) {
        val exprDest: Operand = unop.expr.getOperand()
        val dest: Operand = unop.getOperand()

        if (dest is AddressingMode) {
            representation.addMainInstr(PushInstruction(Registers.r11))
            if (exprDest is AddressingMode) {
                representation.addMainInstr(PushInstruction(Registers.r12))
                representation.addMainInstr(LoadInstruction(Registers.r12, exprDest))
                representation.addMainInstr(
                    LoadInstruction(
                        Registers.r11,
                        AddressingMode.AddressingMode2(Registers.sp)
                    )
                )
                representation.addMainInstr(
                    ReverseSubtractInstruction(
                        Registers.r11,
                        Registers.r12,
                        Immediate(0)
                    )
                )
                representation.addMainInstr(PopInstruction(Registers.r12))
            } else {
                representation.addMainInstr(
                    LoadInstruction(
                        Registers.r11,
                        AddressingMode.AddressingMode2(Registers.sp)
                    )
                )
                representation.addMainInstr(
                    ReverseSubtractInstruction(
                        Registers.r11,
                        exprDest as Register,
                        Immediate(0)
                    )
                )
            }

            representation.addMainInstr(StoreInstruction(Registers.r11, dest as AddressingMode.AddressingMode2))
            representation.addMainInstr(PopInstruction(Registers.r11))
        } else {
            if (exprDest is AddressingMode) {
                representation.addMainInstr(PushInstruction(Registers.r11))
                representation.addMainInstr(LoadInstruction(Registers.r11, exprDest))
                representation.addMainInstr(
                    LoadInstruction(
                        Registers.r11,
                        AddressingMode.AddressingMode2(Registers.sp)
                    )
                )
                representation.addMainInstr(
                    ReverseSubtractInstruction(
                        dest as Register,
                        Registers.r11,
                        Immediate(0)
                    )
                )
                representation.addMainInstr(PopInstruction(Registers.r11))
            } else {
                representation.addMainInstr(
                    LoadInstruction(
                        exprDest as Register,
                        AddressingMode.AddressingMode2(Registers.sp)
                    )
                )
                representation.addMainInstr(
                    ReverseSubtractInstruction(
                        dest as Register,
                        exprDest,
                        Immediate(0)
                    )
                )
            }
        }



        representation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )
        representation.addPInstr(PInstruction.p_throw_overflow_error(representation))
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (Registers.full()) {
            visit(binop.expr2)
            representation.addMainInstr(PushInstruction(Registers.r10))
            visit(binop.expr1)
            visitBinOpStack(binop)
        } else {
            visit(binop.expr1)
            visit(binop.expr2)
            visitBinOp(binop)
        }
    }

    private fun visitBinOpStack(binop: ExprAST.BinOpAST) {
        representation.addMainInstr(PopInstruction(Registers.r11))
        visitBinOp(binop)
    }

    private fun visitBinOp(binop: ExprAST.BinOpAST) {
        when (binop.operator) {
            "+" -> visitAdd(binop)
            "-" -> visitSub(binop)
            "*" -> visitMultiply(binop)
            "/", "%" -> visitDivMod(binop)
            "&&", "||" -> visitAndOr(binop)
            ">", ">=", "<", "<=" -> visitCompare(binop)
            "==", "!=" -> visitEquality(binop)
        }
    }

    private fun visitAdd(binop: ExprAST.BinOpAST) {


        // push r11
        // push r12


        representation.addMainInstr(
            AddInstruction(
                binop.getDestReg(),
                binop.expr1.getDestReg(),
                binop.expr2.getDestReg(),
                Condition.S
            )
        )

        checkOverflow(Condition.LVS)

    }

    private fun visitSub(binop: ExprAST.BinOpAST) {

        representation.addMainInstr(
            SubtractInstruction(
                binop.getDestReg(),
                binop.expr1.getDestReg(),
                binop.expr2.getDestReg(),
                Condition.S
            )
        )

        checkOverflow(Condition.LVS)
    }

    private fun visitMultiply(binop: ExprAST.BinOpAST) {

        representation.addMainInstr(
            SMultiplyInstruction(
                binop.getDestReg(),
                binop.expr1.getDestReg(),
                binop.expr2.getDestReg()
            )
        )
//        representation.addMainInstr(
//            CompareInstruction(
//                binop.expr1,
//                ShiftOperand(dest1, ShiftOperand.Shift.ASR, 31)
//            )
//        )

        checkOverflow(Condition.LNE)

    }

    private fun checkOverflow(cond: Condition) {
        representation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                cond
            )
        )
        representation.addPInstr(PInstruction.p_throw_overflow_error(representation))
    }

    private fun visitDivMod(binop: ExprAST.BinOpAST) {

        // get result from expr1 and move into param register 1
        representation.addMainInstr(MoveInstruction(Registers.r0, binop.expr1.getDestReg()))

        // get result from expr2 and move into param register 2
        representation.addMainInstr(MoveInstruction(Registers.r1, binop.expr2.getDestReg()))

        representation.addPInstr(PInstruction.p_check_divide_by_zero(representation))
        representation.addMainInstr(
            BranchInstruction(
                if (binop.operator == "/") {
                    "__aeabi_idiv"
                } else {
                    "__aeabi_idivmod"
                },
                Condition.L
            )
        )

        // allocate a register to move the result into
        if (binop.operator == "/") {
            representation.addMainInstr(MoveInstruction(binop.getDestReg(), Registers.r0))
        } else {
            representation.addMainInstr(MoveInstruction(binop.getDestReg(), Registers.r1))
        }
    }

    private fun visitAndOr(binop: ExprAST.BinOpAST) {

        val dest: Register
        val operand: Operand

        when {
            binop.expr1 is ExprAST.BoolLiterAST -> {
                dest = binop.expr2.getDestReg()
                operand = Immediate(binop.expr1.getValue())
            }
            binop.expr2 is ExprAST.BoolLiterAST -> {
                dest = binop.expr1.getDestReg()
                operand = Immediate(binop.expr2.getValue())
            }
            else -> {
                dest = binop.expr1.getDestReg()
                operand = binop.expr2.getDestReg()
            }
        }

        representation.addMainInstr(
            if (binop.operator == "&&") {
                AndInstruction(binop.getDestReg(), dest, operand)
            } else {
                OrInstruction(binop.getDestReg(), dest, operand)
            }
        )

    }

    private fun visitCompare(binop: ExprAST.BinOpAST) {

        var cond1: Condition? = null
        var cond2: Condition? = null

        when (binop.expr1.getType()) {
            is TypeIdentifier.IntIdentifier -> {

                when (binop.expr2) {
                    is ExprAST.IntLiterAST -> {
                        representation.addMainInstr(
                            CompareInstruction(
                                binop.expr1.getDestReg(),
                                Immediate(binop.expr2.getValue())
                            )
                        )
                    }

                    else -> {
                        representation.addMainInstr(
                            CompareInstruction(
                                binop.expr1.getDestReg(),
                                binop.expr2.getDestReg()
                            )
                        )
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        cond1 = Condition.GT
                        cond2 = Condition.LE
                    }
                    ">=" -> {
                        cond1 = Condition.GE
                        cond2 = Condition.LT
                    }
                    "<" -> {
                        cond1 = Condition.LT
                        cond2 = Condition.GE
                    }
                    "<=" -> {
                        cond1 = Condition.LE
                        cond2 = Condition.GT
                    }
                }
            }

            is TypeIdentifier.CharIdentifier -> {

                when (binop.expr2) {
                    is ExprAST.CharLiterAST -> {
                        representation.addMainInstr(
                            CompareInstruction(
                                binop.expr1.getDestReg(),
                                ImmediateChar(binop.expr2.value)
                            )
                        )
                    }

                    else -> {
                        representation.addMainInstr(
                            CompareInstruction(
                                binop.expr1.getDestReg(),
                                binop.expr2.getDestReg()
                            )
                        )
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        cond1 = Condition.HI
                        cond2 = Condition.LS
                    }
                    ">=" -> {
                        cond1 = Condition.HS
                        cond2 = Condition.LO
                    }
                    "<" -> {
                        cond1 = Condition.LO
                        cond2 = Condition.HS
                    }
                    "<=" -> {
                        cond1 = Condition.LS
                        cond2 = Condition.HI
                    }
                }
            }
        }

        representation.addMainInstr(
            MoveInstruction(
                binop.getDestReg(),
                Immediate(1),
                cond1
            )
        )

        representation.addMainInstr(
            MoveInstruction(
                binop.getDestReg(),
                Immediate(0),
                cond2
            )
        )
    }

    private fun visitEquality(binop: ExprAST.BinOpAST) {

        val cond1: Condition
        val cond2: Condition

        representation.addMainInstr(
            CompareInstruction(
                binop.expr1.getDestReg(),
                binop.expr2.getDestReg()
            )
        )

        when (binop.operator) {
            "==" -> {
                cond1 = Condition.EQ
                cond2 = Condition.NE
            }
            else -> {
                cond1 = Condition.NE
                cond2 = Condition.EQ

            }
        }

        representation.addMainInstr(
            MoveInstruction(
                binop.getDestReg(),
                Immediate(1),
                cond1
            )
        )
        representation.addMainInstr(
            MoveInstruction(
                binop.getDestReg(),
                Immediate(0),
                cond2
            )
        )
    }

    // types do not require any assembly code
    override fun visitBaseTypeAST(type: TypeAST.BaseTypeAST) {
        return
    }

    // types do not require any assembly code
    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST) {
        return
    }

    // types do not require any assembly code
    override fun visitPairTypeAST(type: TypeAST.PairTypeAST) {
        return
    }

    // types do not require any assembly code
    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST) {
        return
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        // we want to allocate (length * size of elem) + INT_SIZE
        val elemsSize: Int = if (arrayLiter.elemsLength() > 0) {
            arrayLiter.elems[0].getStackSize()
        } else {
            0
        }

        val arrAllocation: Int = arrayLiter.elemsLength() * elemsSize + TypeIdentifier.INT_SIZE

        // load allocation into param register for malloc and branch
        representation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("$arrAllocation")
            )
        )

        representation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // store the array address in an allocated register
        val arrLocation: Register = arrayLiter.getDestReg()
        representation.addMainInstr(MoveInstruction(arrLocation, Registers.r0))

        // we start the index at +4 so we can store the size of the array at +0
        var arrIndex = TypeIdentifier.INT_SIZE
        for (elem in arrayLiter.elems) {
            visit(elem)
            val dest: Register = elem.getDestReg()

            // store the value of elem at the current index
            representation.addMainInstr(
                getStoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(arrLocation, Immediate(arrIndex)),
                    elem.getType()
                )
            )

            arrIndex += elemsSize
        }

        // store the length of the array at arrLocation +0
        representation.addMainInstr(
            LoadInstruction(
                arrayLiter.getSizeGraphNode().getOperand() as Register,
                AddressingMode.AddressingLabel("${arrayLiter.elemsLength()}")
            )
        )

        // store the length of the array at the front of its allocated space
        representation.addMainInstr(
            StoreInstruction(
                arrayLiter.getSizeGraphNode().getOperand() as Register,
                AddressingMode.AddressingMode2(arrLocation)
            )
        )
    }

    // handled by visitAssignAST
    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        return
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        // Variable for the amount of stack space that the arguments take up
        var argsSize = 0
        val symTab: SymbolTable = funcCall.st()

        for (arg in funcCall.args.reversed()) {
            // Account for the number of bytes that already have been allocated
            arg.setParamOffset(arg.getParamOffset() + argsSize)
            visit(arg)
            val dest: Register = arg.getDestReg()

            // Get the size (in bytes) that this argument will take
            val size: Int = arg.getStackSize()

            representation.addMainInstr(
                getStoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * size), true),
                    arg.getType()
                )
            )

            val currStackPtr: Int = symTab.getStackPtr()
            symTab.setStackPtr(currStackPtr - size)

            // Free the destination register for future use and update argsSize
            argsSize += size
        }

        // Reset the parameter offset for future use of the symbol table
        symTab.setParamOffset(0)
        symTab.updatePtrOffset(-1 * argsSize)

        // Branch to the function label in the assembly code
        representation.addMainInstr(
            BranchInstruction(
                "f_${funcCall.funcName}",
                Condition.L
            )
        )

        // Restore the stack pointer
        restoreStackPointer(funcCall, argsSize)

        // Move the result of the function into an available register
        val reg: Register = funcCall.getDestReg()
        representation.addMainInstr(MoveInstruction(reg, Registers.r0))
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        // allocate 8 bytes total for the pair object
        // load value PAIR_SIZE into r0 as param for malloc
        representation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("${2 * TypeIdentifier.ADDR_SIZE}")
            )
        )

        // branch to malloc
        representation.addMainInstr(
            BranchInstruction("malloc", Condition.L)
        )

        // move malloc result into allocated register
        val pairLocation = newPair.getDestReg()
        representation.addMainInstr(MoveInstruction(pairLocation, Registers.r0))

        // visit & allocate the pair's children using the helper function
        allocatePairElem(newPair.fst, pairLocation, 0)
        allocatePairElem(newPair.snd, pairLocation, TypeIdentifier.ADDR_SIZE)
    }

    /* a helper function which will allocate a pair's element and move it into the register where the pair has
     * been allocated, at the given offset. For an element that is first in a pair, offset should be 0, and for
     * an element that is second in a pair, it should be ADDR_SIZE (4).
     */
    private fun allocatePairElem(elem: ExprAST, pairLocation: Register, offset: Int) {
        // visit elem and get its destination register
        visit(elem)
        val dest: Register = elem.getDestReg()

        // do allocation - move size of elem into param register then branch into malloc
        val allocation: Int = elem.getType().getStackSize()
        representation.addMainInstr(
            LoadInstruction(
                Registers.r0, AddressingMode.AddressingLabel("$allocation")
            )
        )

        representation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // move value for dest into the address given by malloc
        representation.addMainInstr(
            getStoreInstruction(
                dest,
                AddressingMode.AddressingMode2(Registers.r0),
                elem.getType()
            )
        )

        // store value in r0 at pairLocation (+ offset)
        representation.addMainInstr(
            StoreInstruction(
                Registers.r0,
                AddressingMode.AddressingMode2(pairLocation, Immediate(offset))
            )
        )
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        // load the address of the pair elem into a register using the helper method
        visitPairElemFstPhase(pairElem)
        val dest: Operand = pairElem.getOperand()

        if(dest is AddressingMode) {
            // load the value at the given address into the same register
            representation.addMainInstr(
                LoadInstruction(
                    Registers.r11,
                    AddressingMode.AddressingMode2(Registers.r11)
                )
            )

            representation.addMainInstr(StoreInstruction(Registers.r11, dest as AddressingMode.AddressingMode2))
            representation.addMainInstr(PopInstruction(Registers.r11))
        } else {
            representation.addMainInstr(
                LoadInstruction(
                    dest as Register,
                    AddressingMode.AddressingMode2(dest)
                )
            )
        }

        // (we don't free the dest register here as it contains the value we want to use elsewhere)
    }

    /* completes visiting pair elems to the point that the address of it is in the dest register,
     * which we can load again to get the value or write to
     * Sets the destination register for the given pair elem
     */
    private fun visitPairElemFstPhase(pairElem: PairElemAST) {
        visit(pairElem.elem)
        // location of the pair
        val pairLocation: Operand = pairElem.elem.getOperand()
        val dest: Operand = pairElem.getOperand()

        // set param and branch to check for null dereference
        representation.addMainInstr(MoveInstruction(Registers.r0, pairLocation))
        representation.addPInstr(PInstruction.p_check_null_pointer(representation))

        val reg: Register

        if(dest is AddressingMode) {
            reg = Registers.r11
            representation.addMainInstr(PushInstruction(reg))
        } else {
            reg = dest as Register
        }

        representation.addMainInstr(MoveInstruction(reg, pairLocation))

        if(!pairElem.isFst) {
            representation.addMainInstr(
                LoadInstruction(
                    reg,
                    AddressingMode.AddressingMode2(reg, Immediate(TypeIdentifier.ADDR_SIZE))
                )
            )
        } else {
            representation.addMainInstr(
                LoadInstruction(
                    reg,
                    AddressingMode.AddressingMode2(reg)
                )
            )
        }
    }
}
