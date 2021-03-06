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
) : ASTBaseVisitor() {

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
        val stackSizeCalculator = StackSizeVisitor(graph)
        stackSizeCalculator.visit(bodyInScope)
        val stackSize: Int = stackSizeCalculator.getStackSize()
        decrementAssemblySP(stackSize)
        bodyInScope.setStackPtr(bodyInScope.getStackPtr() - stackSize)
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
        return getStoreInstruction(reg, addr, type.getStackSize())
    }

    // A helper method for adding "B" to "STR" if given
    private fun getStoreInstruction(
        reg: Register,
        addr: AddressingMode.AddressingMode2,
        size: Int
    ): StoreInstruction {
        return if (size == 1) {
            StoreInstruction(reg, addr, Condition.B)
        } else {
            StoreInstruction(reg, addr)
        }
    }

    private fun getLoadInstruction(
        reg: Register,
        regN: Register,
        operand: Operand,
        size: Int
    ): LoadInstruction {
        return if (size == 1) {
            LoadInstruction(reg, AddressingMode.AddressingMode3(regN, operand))
        } else {
            LoadInstruction(reg, AddressingMode.AddressingMode2(regN, operand))
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
    protected fun visitInnerScope(stat: StatementAST, innerScope: StatementAST) {
        val stackSize = setUpInnerScope(stat, innerScope)

        // Indicate how much stack space has been allocated and visit the branch
        innerScopeStackAllocation(stat, innerScope, stackSize)
        visit(innerScope)

        // Restore the stack pointer for the 'else' branch
        restoreStackPointer(innerScope, stackSize)
    }

    // Takes relevant steps when evaluating the condition expression for a loop
    private fun loopConditionalTranslate(loopExpr: ExprAST, comp: Int) {
        visit(loopExpr)
        val operand: Operand = loopExpr.getOperand()
        val reg: Register = chooseRegisterFromOperand(operand)
        if (operand is AddressingMode) {
            representation.addMainInstr(LoadInstruction(reg, operand))
        }

        representation.addMainInstr(
            CompareInstruction(
                reg,
                Immediate(comp)
            )
        )

        if (operand is AddressingMode) {
            popIfNecessary(reg)
        }
    }

    // A method that gives the location for an ast node to be loaded/moved into
    private fun operandAllocation(ast: AssignRHSAST): Operand {
        return if (ast.hasGraphNode() && ast.getDestReg() != Register(-1)) {
            ast.getDestReg()
        } else {
            val size = ast.getStackSize()

            val offset = ast.getStackPtrOffset()
            val mode = if (ast.getStackSize() == 1) {
                AddressingMode.AddressingMode3(Registers.sp, Immediate(offset))
            } else {
                AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
            }

            ast.setAddr(ast.getStackPtr() + offset)

            ast.updatePtrOffset(size)

            mode
        }
    }

    fun tempRegAllocation(): Register {
        val r10 = Registers.r10
        val r11 = Registers.r11
        val r12 = Registers.r12
        if (r10.inUse()) {
            if (r11.inUse()) {
                if (r12.inUse()) {
                    val r10Uses = r10.getNoOfUses()
                    val r11Uses = r11.getNoOfUses()
                    val r12Uses = r12.getNoOfUses()
                    val reg: Register = if (r10Uses > r11Uses) {
                        r11
                    } else if (r10Uses == r11Uses && r11Uses > r12Uses) {
                        r12
                    } else {
                        r10
                    }
                    representation.addMainInstr(PushInstruction(reg))
                    reg.pushedNow()
                    return reg.occupiedNow()
                } else {
                    return r12.occupiedNow()
                }
            } else {
                return r11.occupiedNow()
            }
        } else {
            return r10.occupiedNow()
        }
    }

    // Helper method to determines whether the input operand is a register, and assigns a temporary
    // register if not
    fun chooseRegisterFromOperand(operand: Operand): Register {
        if (operand !is AddressingMode) {
            return operand as Register
        } else {
            return tempRegAllocation()
        }
    }

    // Not sure about this method
    private fun pushAndCompare(reg: AddressingMode, imm: Int) {
        val dest: Register = Registers.r11
//        representation.addMainInstr(PushInstruction(dest))
        moveOrLoad(dest, reg)
        representation.addMainInstr(CompareInstruction(dest, Immediate(imm)))
//        representation.addMainInstr(PopInstruction(dest))
    }

    // Loads the addressing mode into a register or in a temporary register to then put on the stack
    private fun placeInRegisterOrStack(
        destination: Operand,
        mode: AddressingMode,
        byteSize: Boolean
    ) {
        val destReg: Register = chooseRegisterFromOperand(destination)
        if (destination is AddressingMode) {
            representation.addMainInstr(LoadInstruction(destReg, mode))
            if (byteSize) {
                representation.addMainInstr(
                    StoreInstruction(
                        destReg,
                        AddressingMode.AddressingMode2(
                            destination.getDestReg(),
                            destination.getOperand()
                        ),
                        Condition.B
                    )
                )
            } else {
                representation.addMainInstr(
                    StoreInstruction(
                        destReg,
                        AddressingMode.AddressingMode2(
                            destination.getDestReg(),
                            destination.getOperand()
                        )
                    )
                )
            }
            popIfNecessary(destReg)
        } else {
            representation.addMainInstr(LoadInstruction(destReg, mode))
        }
    }

    // Stores the value on the right hand side of the assignment at the address of the left hand side
    // Note: this is in the case that the left hand side is a pair element or array element
    private fun storeValueInAddress(
        dest: Operand,
        structure: AssignRHSAST
    ) {
        val structureDest: Operand = structure.getOperand()

        val structureDestReg: Register = chooseRegisterFromOperand(structureDest)
        val destReg: Register = chooseRegisterFromOperand(dest)

        if (dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(destReg, dest))
            representation.addMainInstr(
                getStoreInstruction(
                    destReg,
                    AddressingMode.AddressingMode2(structureDestReg),
                    structure.getType()
                )
            )
            popIfNecessary(destReg)
        } else {
            representation.addMainInstr(
                getStoreInstruction(
                    destReg,
                    AddressingMode.AddressingMode2(structureDestReg),
                    structure.getType()
                )
            )
        }

        if (structureDest is AddressingMode) {
            popIfNecessary(structureDestReg)
        }
    }

    // Creates an addressing mode depending on the input operand
    private fun chooseAddressingMode(operand: Operand): AddressingMode {
        return if (operand is AddressingMode) {
            operand
        } else {
            AddressingMode.AddressingMode2(operand as Register)
        }
    }

    // Helper method that moves the location of a pair or an array into a specific register
    private fun moveLocation(elem: AssignRHSAST, location: Operand): Register {
        val dest: Operand = elem.getOperand()

        val reg: Register = chooseRegisterFromOperand(dest)

        moveOrLoad(reg, location)

        return reg
    }

    fun popIfNecessary(tempReg: Register) {
        tempReg.freedNow()
        if (tempReg.hasBeenPushed()) {
            representation.addMainInstr(PopInstruction(tempReg))
            tempReg.poppedNow()
        }
    }

    // Stores the result held in a temporary register back onto the stack and pops the temporary register
    private fun tempRegRestore(tempReg: Register, dest: AddressingMode.AddressingMode2) {
        representation.addMainInstr(StoreInstruction(tempReg, dest))
        popIfNecessary(tempReg)
    }

    // A method that puts some output into its correct location depending on whether the location
    // is on the stack or in a register
    private fun storeOrMove(expr: AssignRHSAST, reg: Register) {
        val dest = expr.getOperand()
        if (dest is AddressingMode) {
            representation.addMainInstr(
                getStoreInstruction(
                    reg,
                    dest as AddressingMode.AddressingMode2,
                    expr.getType()
                )
            )
        } else {
            representation.addMainInstr(MoveInstruction(dest as Register, reg))
        }
    }

    private fun popTempRegisterConditional(reg: Register, destReg: Register) {
        if (reg.hasBeenPushed() && destReg != reg) {
            representation.addMainInstr(PopInstruction(reg))
        }
    }

    private fun moveOrLoad(register: Register, operand: Operand) {
        if (operand is AddressingMode) {
            representation.addMainInstr(LoadInstruction(register, operand))
        } else {
            representation.addMainInstr(MoveInstruction(register, operand))
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
        val dest = decl.assignment.getOperand()

        if (decl.assignment is ExprAST.IdentAST) {
            // Move location of the identifier in the destination of the declaration
            val destDecl = if (decl.getGraphNode().getRegister() != Register(-1)) {
                decl.getGraphNode().getRegister()
            } else {
                val size = decl.getStackSize()

                val offset = decl.getStackPtrOffset()
                val mode = if (size == 1) {
                    AddressingMode.AddressingMode3(Registers.sp, Immediate(offset))
                } else {
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
                }

                decl.setAddr(decl.getStackPtr() + offset)
                decl.updatePtrOffset(size)

                mode
            }

            val destReg = chooseRegisterFromOperand(dest)
            val destDeclReg = chooseRegisterFromOperand(destDecl)

            if (dest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(destDeclReg, dest))
                if (destDecl is AddressingMode) {
                    representation.addMainInstr(
                        getStoreInstruction(
                            destDeclReg,
                            destDecl as AddressingMode.AddressingMode2,
                            decl.assignment.getType()
                        )
                    )
                    popIfNecessary(destDeclReg)
                }
            } else {
                if (destDecl is AddressingMode) {
                    representation.addMainInstr(
                        getStoreInstruction(
                            destReg,
                            destDecl as AddressingMode.AddressingMode2,
                            decl.assignment.getType()
                        )
                    )
                } else {
                    representation.addMainInstr(
                        MoveInstruction(
                            destDeclReg,
                            destReg
                        )
                    )
                }
            }

        }

        if (dest is AddressingMode) {
            val boundaryAddr = scope.getStackPtr()
            val varObj: VariableIdentifier = scope.lookupAll(decl.varName) as VariableIdentifier
            varObj.setAddr(boundaryAddr + currOffset)

            // Indicate that the variable identifier has now been allocated
            varObj.allocatedNow()
        }
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        // Generate code for the right hand side of the statement
        visit(assign.rhs)
        val dest: Operand = assign.rhs.getOperand()

        val lhs: AssignLHSAST = assign.lhs

        when {
            lhs.ident != null -> {
                val identifier = assign.st().lookupAll(lhs.ident.value)
                var lhsLocation: Operand? = null

                if (identifier is VariableIdentifier) {
                    lhsLocation = identifier.getGraphNode().getRegister()
                }

                if (lhsLocation != null && !lhsLocation.equals(InterferenceGraph.DefaultReg)) {
                    if (lhsLocation != dest) {
                        representation.addMainInstr(MoveInstruction(lhsLocation as Register, dest))
                    }
                } else {
                    val offset: Int = calculateIdentSpOffset(lhs.getStringValue(), assign, 0)
                    val destReg: Register = chooseRegisterFromOperand(dest)
                    if (dest is AddressingMode) {
                        representation.addMainInstr(MoveInstruction(destReg, dest))
                    }

                    representation.addMainInstr(
                        StoreInstruction(
                            destReg,
                            AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
                        )
                    )

                    if (dest is AddressingMode) {
                        popIfNecessary(destReg)
                    }
                }
            }
            lhs.arrElem != null -> {
                val arrElem = lhs.arrElem!!

                // load the address of the elem into a register
                visitArrayElemFstPhase(arrElem)

                storeValueInAddress(dest, arrElem)
            }
            // This needs to be changed to match refactoring
            lhs.pairElem != null -> {
                val pairElem: PairElemAST = lhs.pairElem!!

                // load the address of the pair elem into a register
                visitPairElemFstPhase(pairElem)

                storeValueInAddress(dest, pairElem)
            }
        }
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        visitInnerScope(begin, begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        var type: TypeIdentifier? = TypeIdentifier()
        var operand: Operand? = null

        val leftHand: AssignLHSAST = read.lhs
        val leftHandIdent: ExprAST.IdentAST? = leftHand.ident
        val leftHandPairElem: PairElemAST? = leftHand.pairElem

        // Set the destination register and the type
        if (leftHandIdent != null) {
            visitIdentForRead(leftHandIdent)
            operand = leftHandIdent.getOperand()
            type = leftHandIdent.getType()
        } else if (leftHandPairElem != null) {
            visitPairElemAST(leftHandPairElem)
            operand = leftHandPairElem.getOperand()
            type = leftHandPairElem.getType()
        }

        val reg = chooseRegisterFromOperand(operand!!)
        // Move the value in the destination register into r0
        representation.addMainInstr(AddInstruction(reg, Registers.sp, Immediate(0)))
        representation.addMainInstr(MoveInstruction(Registers.r0, reg))

        // Call the relevant primitive function (depending on the type)
        if (type == TypeIdentifier.INT_TYPE) {
            representation.addPInstr(PInstruction.p_read_int(representation))
        }
        if (type == TypeIdentifier.CHAR_TYPE) {
            representation.addPInstr(PInstruction.p_read_char(representation))
        }

        representation.addMainInstr(
            LoadInstruction(
                reg,
                AddressingMode.AddressingMode2(Registers.sp)
            )
        )

    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        // Evaluate the exiting instruction and get destination register
        visit(exit.expr)
        val dest: Operand = exit.expr.getOperand()

        // Move contents of the register in r0 for calling exit
        moveOrLoad(Registers.r0, dest)
        representation.addMainInstr(BranchInstruction("exit", Condition.L))
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        // Evaluate the expression that you are freeing and obtain the destination register
        visit(free.expr)
        val dest: Operand = free.expr.getOperand()

        // Move the contents of the destination register into r0
        moveOrLoad(Registers.r0, dest)

        // Add the primitive instruction corresponding to the type of the expression
        if (free.expr.getType() is TypeIdentifier.ArrayIdentifier) {
            representation.addPInstr(PInstruction.p_free_array(representation))
        } else {
            representation.addPInstr(PInstruction.p_free_pair(representation))
        }
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Evaluation of the condition expression and condition checking
        val condition: ExprAST = ifStat.condExpr
        visit(condition)
        val destination: Operand = condition.getOperand()
        val destReg: Register = chooseRegisterFromOperand(destination)

        if (destination is AddressingMode) {
            representation.addMainInstr(LoadInstruction(destReg, destination))
        }

        representation.addMainInstr(CompareInstruction(destReg, Immediate(0)))

        if (destination is AddressingMode) {
            popIfNecessary(destReg)
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
        visitInnerScope(ifStat, ifStat.elseStat)

        // Make label for whatever follows the if statement
        representation.addMainInstr(nextLabel)
    }

    // Call and add IO instructions
    override fun visitPrintAST(print: StatementAST.PrintAST) {
        // Evaluate expression to be printed and obtain the register where the result is held
        visit(print.expr)
        val reg: Operand = print.expr.getOperand()

        moveOrLoad(Registers.r0, reg)

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
        val dest: Operand = ret.expr.getOperand()

        // Move the value into r0 and pop the program counter
        moveOrLoad(Registers.r0, dest)

        // Restore the stack pointer depending on how much stack space has been allocated thus far
        restoreStackPointer(ret, ret.getStackSizeAllocated())
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

        loopConditionalTranslate(whileStat.loopExpr, 1)

        representation.addMainInstr(BranchInstruction(bodyLabel.getLabel(), Condition.EQ))
    }

    override fun visitForAST(forLoop: StatementAST.ForAST) {
        val body: StatementAST = forLoop.body

        // Ensure that the stack pointer for the loop body is in the right place initially
        val currSp: Int = forLoop.getStackPtr()
        body.setStackPtr(body.getStackPtr() + currSp)

        // Allocate stack space for local variables that are not in registers. Update stack pointer accordingly
        val stackSizeCalculator = StackSizeVisitor(graph)
        stackSizeCalculator.visit(forLoop)
        val stackSize: Int = stackSizeCalculator.getStackSize()
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

        loopConditionalTranslate(forLoop.loopExpr, 0)

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

        val dest: Operand = operandAllocation(liter)

        placeInRegisterOrStack(dest, mode, false)
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        val intValue = liter.getValue()

        val dest: Operand = operandAllocation(liter)

        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel("$intValue"), true)
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        val dest: Operand = operandAllocation(liter)

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
        val dest: Operand = operandAllocation(liter)

        representation.addDataInstr(label)
        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel(label.getLabel()), false)
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        /* a pair liter is null so will have address zero
         * so we load the value zero into a destination register */
        val dest: Operand = operandAllocation(liter)
        placeInRegisterOrStack(dest, AddressingMode.AddressingLabel("0"), false)
    }

    private fun visitIdentForRead(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, true)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, false)
    }

    private fun visitIdentGeneral(ident: ExprAST.IdentAST, read: Boolean) {
        return
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        // load the address of the array elem
        visitArrayElemFstPhase(arrayElem)
        val operand: Operand = arrayElem.getOperand()
        val dest: Register = chooseRegisterFromOperand(operand)

        representation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(dest)
            )
        )

        if (operand is AddressingMode) {
            popIfNecessary(dest)
        }
    }

    private fun visitArrayElemFstPhase(arrayElem: ExprAST.ArrayElemAST) {
        var arrLocation: Operand = if (arrayElem.getArrayLocation() != null) {
            arrayElem.getArrayLocation()!!.getRegister()
        } else {
            val ident = arrayElem.st().lookupAll(arrayElem.ident) as ParamIdentifier
            AddressingMode.AddressingMode2(
                Registers.sp,
                Immediate(arrayElem.getStackSizeAllocated() + ident.getOffset())
            )
        }

        if (arrLocation.equals(InterferenceGraph.DefaultReg)) {
            val offset = calculateIdentSpOffset(arrayElem.ident, arrayElem, 0)
            arrLocation = AddressingMode.AddressingMode2(Registers.sp, Immediate(offset))
        }

        // Move the start of the array into dest register
        val reg: Register = moveLocation(arrayElem, arrLocation)

        for ((i, expr) in arrayElem.exprs.withIndex()) {
            visit(expr)
            val exprDest: Operand = expr.getOperand()

            moveOrLoad(Registers.r0, exprDest)
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
            val exprDestReg: Register = chooseRegisterFromOperand(exprDest)
            when (type.getStackSize()) {
                FOUR_BYTES -> {
                    if (exprDest is AddressingMode) {
                        moveOrLoad(exprDestReg, exprDest)
                    }
                    representation.addMainInstr(
                        AddInstruction(
                            reg,
                            reg,
                            ShiftOperand(exprDestReg, ShiftOperand.Shift.LSL, 2)
                        )
                    )
                    if (exprDest is AddressingMode) {
                        popIfNecessary(exprDestReg)
                    }

                }
                else -> {
                    representation.addMainInstr(AddInstruction(reg, reg, exprDest))
                }
            }

            if (i < arrayElem.exprs.size - 1) {
                representation.addMainInstr(
                    LoadInstruction(
                        reg,
                        AddressingMode.AddressingMode2(reg)
                    )
                )
            }

            if (arrayElem.getOperand() is AddressingMode) {
                popIfNecessary(reg)
            }
        }
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        // Evaluate the single operand and get the register holding the result
        visit(unop.expr)

        when (unop.operator.operator) {
            "-" -> visitNeg(unop.getOperand(), unop.expr.getOperand())
            "!" -> visitNot(unop.getOperand(), unop.expr.getOperand())
            "len" -> visitLen(unop.getOperand(), unop.expr.getOperand())
            else -> {
                val unopReg: Register = chooseRegisterFromOperand(unop.getOperand())
                moveOrLoad(unopReg, unop.expr.getOperand())
                if (unop.getOperand() is AddressingMode) {
                    popIfNecessary(unopReg)
                }
            }
        }
    }

    private fun visitLen(dest: Operand, arrLocation: Operand) {
        val destReg: Register = chooseRegisterFromOperand(dest)
        val source: AddressingMode = chooseAddressingMode(arrLocation)

        representation.addMainInstr(LoadInstruction(destReg, source))

        if (arrLocation is AddressingMode) {
            representation.addMainInstr(
                LoadInstruction(
                    destReg,
                    AddressingMode.AddressingMode2(destReg)
                )
            )
        }

        if (dest is AddressingMode) {
            popIfNecessary(destReg)
        }
    }

    private fun visitNot(dest: Operand, exprDest: Operand) {
        val destReg: Register = chooseRegisterFromOperand(dest)
        val source: AddressingMode = chooseAddressingMode(exprDest)

        if (dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(destReg, source))
            representation.addMainInstr(EorInstruction(destReg, destReg, Immediate(1)))
            tempRegRestore(destReg, dest as AddressingMode.AddressingMode2)
        } else {
            if (exprDest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(destReg, source))
                representation.addMainInstr(EorInstruction(destReg, destReg, Immediate(1)))
            } else {
                representation.addMainInstr(
                    EorInstruction(
                        destReg,
                        exprDest as Register,
                        Immediate(1)
                    )
                )
            }
        }
    }

    private fun visitNeg(dest: Operand, exprDest: Operand) {
        val destReg: Register = chooseRegisterFromOperand(dest)
        val exprDestReg: Register = chooseRegisterFromOperand(exprDest)

        negHelper(exprDest, exprDestReg, destReg, destReg)
        if (dest is AddressingMode) {
            tempRegRestore(destReg, dest as AddressingMode.AddressingMode2)
        }

        representation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )
        representation.addPInstr(PInstruction.p_throw_overflow_error(representation))
    }

    private fun negHelper(
        exprDest: Operand,
        exprDestReg: Register,
        destReg: Register,
        regToLoad: Register
    ) {
        if (exprDest is AddressingMode) {
            representation.addMainInstr(PushInstruction(exprDestReg))
            representation.addMainInstr(LoadInstruction(exprDestReg, exprDest))
            negation(destReg, exprDestReg)
            popIfNecessary(exprDestReg)
        } else {
            negation(destReg, exprDestReg)
        }
    }

    private fun negation(destReg: Register, exprDestReg: Register) {
        representation.addMainInstr(
            ReverseSubtractInstruction(
                destReg,
                exprDestReg,
                Immediate(0)
            )
        )
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (Registers.full()) {
            visit(binop.expr2)
            representation.addMainInstr(PushInstruction(Registers.r10))
            visit(binop.expr1)
            visitBinOpStack(binop)
        } else {
            if (binop.expr1 !is ExprAST.BinOpAST && binop.expr2 is ExprAST.BinOpAST) {
                visit(binop.expr2)
                visit(binop.expr1)
            } else {
                visit(binop.expr1)
                visit(binop.expr2)
            }
            visitBinOp(binop)
        }
    }

    private fun visitBinOpStack(binop: ExprAST.BinOpAST) {
        representation.addMainInstr(PopInstruction(Registers.r11))
        visitBinOp(binop)
    }

    private fun visitBinOp(binop: ExprAST.BinOpAST) {
        when (binop.operator) {
            "+", "-", "*" -> binopRegisterOrStack(binop)
            "/", "%" -> visitDivMod(binop)
            "&&", "||" -> visitAndOr(binop)
            ">", ">=", "<", "<=", "==", "!=" -> visitCompareEquals(binop)
        }
    }

    private fun binopRegisterOrStack(binop: ExprAST.BinOpAST) {
        val dest: Operand = binop.getOperand()
        val expr1Dest: Operand = binop.expr1.getOperand()
        val expr2Dest: Operand = binop.expr2.getOperand()

        val reg: Register = chooseRegisterFromOperand(dest)

        val expr1Reg: Register = chooseRegisterFromOperand(expr1Dest)
        if (expr1Dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(expr1Reg, expr1Dest))
        }

        val expr2Reg: Register = chooseRegisterFromOperand(expr2Dest)
        if (expr2Dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(expr2Reg, expr2Dest))
        }

        when (binop.operator) {
            "+" -> {
                representation.addMainInstr(AddInstruction(reg, expr1Reg, expr2Reg, Condition.S))
                checkOverflow(Condition.LVS)
            }
            "-" -> {
                representation.addMainInstr(
                    SubtractInstruction(
                        reg,
                        expr1Reg,
                        expr2Reg,
                        Condition.S
                    )
                )
                checkOverflow(Condition.LVS)
            }
            "*" -> {
                val dest2: Operand = binop.getOperand2()
                val reg2: Register = chooseRegisterFromOperand(dest2)
                representation.addMainInstr(SMultiplyInstruction(reg, reg2, expr1Reg, expr2Reg))
                representation.addMainInstr(
                    CompareInstruction(
                        reg2,
                        ShiftOperand(reg, ShiftOperand.Shift.ASR, 31)
                    )
                )
                checkOverflow(Condition.LNE)
                if (dest2 is AddressingMode) {
                    popIfNecessary(reg2)
                }
            }
        }

        if (expr2Dest is AddressingMode) {
            popIfNecessary(expr2Reg)
        }

        if (dest is AddressingMode) {
            representation.addMainInstr(
                getStoreInstruction(
                    reg,
                    dest as AddressingMode.AddressingMode2,
                    binop.getType()
                )
            )
        }

        if (dest is AddressingMode) {
            popIfNecessary(reg)
        }

        if (expr1Dest is AddressingMode) {
            popIfNecessary(expr1Reg)
        }
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
        val expr1Dest: Operand = binop.expr1.getOperand()
        val expr2Dest: Operand = binop.expr2.getOperand()

        // get result from expr1 and move into param register 1
        moveOrLoad(Registers.r0, expr1Dest)

        // get result from expr2 and move into param register 2
        moveOrLoad(Registers.r1, expr2Dest)

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
            storeOrMove(binop, Registers.r0)
        } else {
            storeOrMove(binop, Registers.r1)
        }
    }

    private fun visitAndOr(binop: ExprAST.BinOpAST) {

        val dest: Operand = binop.getOperand()
        val destReg: Register = chooseRegisterFromOperand(dest)

        val expr1Dest: Operand = binop.expr1.getOperand()
        val expr2Dest: Operand = binop.expr2.getOperand()

        val reg: Register
        val operand: Operand

        if (expr1Dest is AddressingMode) {
            if (expr2Dest is AddressingMode) {
                reg = tempRegAllocation()
                operand = expr2Dest
                representation.addMainInstr(LoadInstruction(reg, expr1Dest))
            } else {
                reg = expr2Dest as Register
                operand = expr1Dest
            }
        } else {
            reg = expr1Dest as Register
            operand = expr2Dest
        }

        if (binop.operator == "&&") {
            representation.addMainInstr(AndInstruction(destReg, reg, operand))
        } else {
            representation.addMainInstr(OrInstruction(destReg, reg, operand))
        }

        if (dest is AddressingMode) {
            representation.addMainInstr(
                getStoreInstruction(
                    destReg,
                    dest as AddressingMode.AddressingMode2,
                    binop.getType()
                )
            )
        }

        if (expr1Dest is AddressingMode && expr2Dest is AddressingMode) {
            popIfNecessary(reg)
        }

        if (dest is AddressingMode) {
            popIfNecessary(destReg)
        }
    }

    private fun getCondition(binop: ExprAST.BinOpAST): Pair<Condition?, Condition?> {
        var cond1: Condition? = null
        var cond2: Condition? = null
        if (binop.operator in arrayListOf("==", "!=")) {
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
        } else {
            when (binop.expr1.getType()) {
                is TypeIdentifier.IntIdentifier -> {
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
        }

        return Pair(cond1, cond2)
    }

    private fun visitCompareEquals(binop: ExprAST.BinOpAST) {
        val dest: Operand = binop.getOperand()
        val destReg = chooseRegisterFromOperand(dest)
        val expr1Dest: Operand = binop.expr1.getOperand()

        val expr1Reg: Register = chooseRegisterFromOperand(expr1Dest)
        if (expr1Dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(expr1Reg, expr1Dest))
        }

        val expr2Dest: Operand = binop.expr2.getOperand()
        val expr2Reg: Register = chooseRegisterFromOperand(expr2Dest)
        if (expr2Dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(expr2Reg, expr2Dest))
        }

        representation.addMainInstr(CompareInstruction(expr1Reg, expr2Reg))

        val pair: Pair<Condition?, Condition?> = getCondition(binop)

        conditionalMove(destReg, pair.first, pair.second)

        popTempRegisterConditional(expr1Reg, destReg)

        if (dest is AddressingMode) {
            tempRegRestore(destReg, dest as AddressingMode.AddressingMode2)
        }
    }

    private fun conditionalMove(destReg: Register, cond1: Condition?, cond2: Condition?) {
        representation.addMainInstr(
            MoveInstruction(
                destReg,
                Immediate(1),
                cond1
            )
        )

        representation.addMainInstr(
            MoveInstruction(
                destReg,
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
        val arrLocation: Operand = arrayLiter.getOperand()
        val reg: Register = chooseRegisterFromOperand(arrLocation)

        representation.addMainInstr(MoveInstruction(reg, Registers.r0))

        // we start the index at +4 so we can store the size of the array at +0
        var arrIndex = TypeIdentifier.INT_SIZE
        for (elem in arrayLiter.elems) {
            visit(elem)
            val dest: Operand = elem.getOperand()

            val elemReg = chooseRegisterFromOperand(dest)
            if (dest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(elemReg, dest))
            }

            // store the value of elem at the current index
            representation.addMainInstr(
                getStoreInstruction(
                    elemReg,
                    AddressingMode.AddressingMode2(reg, Immediate(arrIndex)),
                    elem.getType()
                )
            )

            if (dest is AddressingMode) {
                popIfNecessary(elemReg)
            }

            arrIndex += elemsSize
        }

        var sizeRegister: Register = arrayLiter.getSizeGraphNode().getRegister()
        if (sizeRegister == Register(-1)) {
            sizeRegister = tempRegAllocation()
        }

        // store the length of the array at arrLocation +0
        representation.addMainInstr(
            LoadInstruction(
                sizeRegister,
                AddressingMode.AddressingLabel("${arrayLiter.elemsLength()}")
            )
        )

        // store the length of the array at the front of its allocated space
        representation.addMainInstr(
            StoreInstruction(
                sizeRegister,
                AddressingMode.AddressingMode2(reg)
            )
        )

        if (arrLocation is AddressingMode) {
            tempRegRestore(reg, arrLocation as AddressingMode.AddressingMode2)
        }
    }

    // handled by visitAssignAST
    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        return
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        // Variable for the amount of stack space that the arguments take up
        var argsSize = 0
        val symTab: SymbolTable = funcCall.st()

        // Find all registers that are in use at this point of time and push them on the stack
        val regsInUse: ArrayList<Pair<Register, Int>> = graph.regsInUse(funcCall.ctx)
        for (reg in regsInUse) {
            representation.addMainInstr(
                getStoreInstruction(
                    reg.first,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * reg.second), true),
                    reg.second
                )
            )
        }

        for (arg in funcCall.args.reversed()) {
            // Account for the number of bytes that already have been allocated
            arg.setParamOffset(arg.getParamOffset() + argsSize)
            visit(arg)
            val dest: Operand = arg.getOperand()

            val destReg: Register = chooseRegisterFromOperand(dest)
            if (dest is AddressingMode) {
                representation.addMainInstr(LoadInstruction(destReg, dest))
            }

            // Get the size (in bytes) that this argument will take
            val size: Int = arg.getStackSize()

            representation.addMainInstr(
                getStoreInstruction(
                    destReg,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * size), true),
                    arg.getType()
                )
            )

            val currStackPtr: Int = symTab.getStackPtr()
            symTab.setStackPtr(currStackPtr - size)

            // Free the destination register for future use and update argsSize
            argsSize += size

            if (dest is AddressingMode) {
                popIfNecessary(destReg)
            }
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

        // Pop all of the registers that were pushed on the stack
        for (reg in regsInUse.reversed()) {
            representation.addMainInstr(
                getLoadInstruction(
                    reg.first,
                    Registers.sp,
                    Immediate(0),
                    reg.second
                )
            )
            representation.addMainInstr(
                AddInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(reg.second)
                )
            )
        }

        // Move the result of the function into the correct destination
        storeOrMove(funcCall, Registers.r0)
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

        val pairLocation = newPair.getOperand()
        val reg: Register = chooseRegisterFromOperand(pairLocation)

        representation.addMainInstr(MoveInstruction(reg, Registers.r0))

        // visit & allocate the pair's children using the helper function
        allocatePairElem(newPair.fst, reg, 0)
        allocatePairElem(newPair.snd, reg, TypeIdentifier.ADDR_SIZE)

        if (pairLocation is AddressingMode) {
            tempRegRestore(reg, pairLocation as AddressingMode.AddressingMode2)
        }
    }

    /* a helper function which will allocate a pair's element and move it into the register where the pair has
     * been allocated, at the given offset. For an element that is first in a pair, offset should be 0, and for
     * an element that is second in a pair, it should be ADDR_SIZE (4).
     */
    private fun allocatePairElem(elem: ExprAST, pairLocation: Register, offset: Int) {
        // visit elem and get its destination, which may need to be a temporary register for the instructions below
        visit(elem)
        val dest: Operand = elem.getOperand()
        val reg: Register = chooseRegisterFromOperand(dest)
        if (dest is AddressingMode) {
            representation.addMainInstr(LoadInstruction(reg, dest))
        }

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
                reg,
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

        if (dest is AddressingMode) {
            popIfNecessary(reg)
        }
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        // load the address of the pair elem into a register using the helper method
        visitPairElemFstPhase(pairElem)
        val dest: Operand = pairElem.getOperand()
        val destReg: Register = chooseRegisterFromOperand(dest)

        // load the value at the given address into the same register
        representation.addMainInstr(
            LoadInstruction(
                destReg,
                AddressingMode.AddressingMode2(destReg)
            )
        )

        if (dest is AddressingMode) {
            tempRegRestore(destReg, dest as AddressingMode.AddressingMode2)
        }
    }

    /* Completes visiting pair elements to the point that the address is in the dest register,
     * which we can load again to get the value or write to.
     * Sets the destination register for the given pair elem
     */
    private fun visitPairElemFstPhase(pairElem: PairElemAST) {
        visit(pairElem.elem)
        // location of the pair
        val pairLocation: Operand = pairElem.elem.getOperand()

        // set param and branch to check for null dereference
        moveOrLoad(Registers.r0, pairLocation)
        representation.addPInstr(PInstruction.p_check_null_pointer(representation))

        val reg: Register = moveLocation(pairElem, pairLocation)

        if (!pairElem.isFst) {
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

    override fun visitMapAST(mapAST: ExprAST.MapAST) {
        visit(mapAST.assignRHS)
        // Have left out the push and pop, but allocation scheme for temporary registers is needed here
        val rhsDest: Operand = mapAST.assignRHS.getOperand()
        val rhsDestReg: Register = chooseRegisterFromOperand(rhsDest)
        val elemsSize: Int = mapAST.assignRHS.getType().getStackSize()

        val lengthDest = mapAST.getOperand(mapAST.lengthReg)
        val spaceDest = mapAST.getOperand(mapAST.spaceReg)

        val lengthReg: Register = chooseRegisterFromOperand(lengthDest)
        val spaceReg: Register = chooseRegisterFromOperand(spaceDest)

        // store length of array in lengthReg
        representation.addMainInstr(
            LoadInstruction(lengthReg, AddressingMode.AddressingMode2(rhsDestReg))
        )

        // store elemsSize in spaceReg for multiplication
        representation.addMainInstr(
            MoveInstruction(spaceReg, Immediate(elemsSize))
        )

        representation.addMainInstr(
            MultiplyInstruction(spaceReg, lengthReg, spaceReg)
        )

        representation.addMainInstr(
            AddInstruction(spaceReg, spaceReg, Immediate(TypeIdentifier.INT_SIZE))
        )

        // load allocation into param register for malloc and branch
        representation.addMainInstr(
            MoveInstruction(
                Registers.r0,
                spaceReg
            )
        )

        representation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // store the array address in an allocated register
        val arrLocationDest = mapAST.getOperand(mapAST.arrLocation)
        val arrLocation: Register = chooseRegisterFromOperand(arrLocationDest)
        representation.addMainInstr(MoveInstruction(arrLocation, Registers.r0))

        // we start the index at +4 so we can store the size of the array at +0
        val arrIndexDest = mapAST.getOperand(mapAST.arrIndexReg)
        val arrIndexReg: Register = chooseRegisterFromOperand(arrIndexDest)
        representation.addMainInstr(
            MoveInstruction(
                arrIndexReg,
                Immediate(TypeIdentifier.INT_SIZE)
            )
        )

        val arrayElemDest = mapAST.getOperand(mapAST.arrayElemReg)
        val arrayElemReg: Register = chooseRegisterFromOperand(arrayElemDest)
        val condLabel = getUniqueLabel()

        representation.addMainInstr(condLabel)
        representation.addMainInstr(
            LoadInstruction(arrayElemReg, AddressingMode.AddressingMode2(rhsDestReg, arrIndexReg))
        )
        when (mapAST.operator.operator) {
            "-" -> visitNeg(arrayElemReg, arrayElemReg)
            "!" -> visitNot(arrayElemReg, arrayElemReg)
            "len" -> visitLen(arrayElemReg, arrayElemReg)
        }

        // store the value of elem at the current index
        representation.addMainInstr(
            StoreInstruction(
                arrayElemReg,
                AddressingMode.AddressingMode2(arrLocation, arrIndexReg)
            )
        )

        representation.addMainInstr(
            AddInstruction(arrIndexReg, arrIndexReg, Immediate(elemsSize))
        )

        representation.addMainInstr(
            SubtractInstruction(lengthReg, lengthReg, Immediate(1))
        )

        representation.addMainInstr(
            CompareInstruction(lengthReg, Immediate(0))
        )

        representation.addMainInstr(
            BranchInstruction(condLabel.getLabel(), Condition.NE)
        )

        // store the length of the array at arrLocation +0
        val sizeDest = mapAST.getOperand(mapAST.sizeDest)
        val sizeDestReg: Register = chooseRegisterFromOperand(sizeDest)
        representation.addMainInstr(
            LoadInstruction(
                sizeDestReg,
                AddressingMode.AddressingMode2(rhsDestReg)
            )
        )

        // store the length of the array at the front of its allocated space
        representation.addMainInstr(
            StoreInstruction(
                sizeDestReg,
                AddressingMode.AddressingMode2(arrLocation)
            )
        )

        if (sizeDest is AddressingMode) {
            popIfNecessary(sizeDestReg)
        }

    }

    override fun visitOperatorAST(operatorAST: ExprAST.OperatorAST) {
        return
    }

}
