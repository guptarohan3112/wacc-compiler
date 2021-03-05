package wacc_05.code_generation

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.code_generation.instructions.*
import wacc_05.code_generation.instructions.LabelInstruction.Companion.getUniqueLabel
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class TranslatorVisitor : ASTBaseVisitor() {

    private val MAX_STACK_SIZE: Int = 1024
    private val START_OFFSET: Int = 4
    private val ONE_BYTE: Int = 1
    private val FOUR_BYTES: Int = 4


    /* UTILITY METHODS USED BY DIFFERENT VISIT METHODS IN THIS VISITOR
       ---------------------------------------------------------------
     */

    // Push the linking register and calculate the space on the stack needed
    private fun startNewBody(bodyInScope: StatementAST): Int {
        AssemblyRepresentation.addMainInstr(PushInstruction(Registers.lr))
        return calculateStackSize(bodyInScope)
    }

    // Uses a stackSizeVisitor to calculate the space needed on the stack
    private fun calculateStackSize(bodyInScope: StatementAST): Int {
        // Calculate stack size for scope and decrement the stack pointer accordingly
        val stackSizeCalculator = StackSizeVisitor()
        val stackSize: Int = stackSizeCalculator.getStackSize(bodyInScope)
        var tmp: Int = stackSize
        while (tmp > 0) {
            AssemblyRepresentation.addMainInstr(
                SubtractInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(tmp.coerceAtMost(MAX_STACK_SIZE))
                )
            )
            tmp -= MAX_STACK_SIZE
        }
        return stackSize
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
            AssemblyRepresentation.addMainInstr(
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

    /* MAIN VISIT METHODS (OVERIDDEN FROM THE BASE VISITOR CLASS)
       ---------------------------------------------------------
     */

    override fun visitProgramAST(prog: ProgramAST) {
        // Generate assembly code for each of the functions declared in the program
        for (func in prog.functionList) {
            visit(func)
        }

        // Generate code for the main body
        AssemblyRepresentation.addMainInstr(LabelInstruction("main"))
        val stackSize: Int = startNewBody(prog.stat)

        // Decrement the stack pointer value and update the symbol table with this sp
        val currStkPtr: Int = prog.stat.getStackPtr()
        prog.stat.setStackPtr(currStkPtr - stackSize)

        // Generate assembly code for the body statement
        visit(prog.stat)

        // Increment internal representation of the stack pointer and increment the stack pointer on assembly
        restoreStackPointer(prog.stat, stackSize)

        // Return the exit code and pop the program counter
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("0")
            )
        )
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
    }

    override fun visitFunctionAST(func: FunctionAST) {
        // Create the label for the function
        val funcLabel = "f_${func.funcName}"
        AssemblyRepresentation.addMainInstr(LabelInstruction(funcLabel))

        // Generate code to allocate space on the stack for local variables in the function
        val stackSize: Int = startNewBody(func.body)

        // Store how amount of allocated space for local variables on the corresponding function identifier
        val funcIdent = FunctionST.lookupAll(func.funcName)!!
        funcIdent.setStackSize(stackSize)

        // Record the space that has been allocated on the stack for the function scope
        func.body.setStackSizeAllocated(stackSize)

        if (func.paramList != null) {
            visitAndUpdateParams(func.paramList)
        }

        // Generate assembly code for the body statement
        visit(func.body)

        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
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
        val dest: Register = decl.assignment.getDestReg()

        // Store the value at an available register
        val mode = if (currOffset == 0) {
            AddressingMode.AddressingMode2(Registers.sp)
        } else {
            AddressingMode.AddressingMode2(Registers.sp, Immediate(currOffset))
        }

        AssemblyRepresentation.addMainInstr(
            getStoreInstruction(
                dest,
                mode,
                decl.assignment.getType()
            )
        )

        // Set the absolute stack address of the variable in the corresponding variable identifier
        val boundaryAddr = scope.getStackPtr()
        val varObj: VariableIdentifier = scope.lookupAll(decl.varName) as VariableIdentifier
        varObj.setAddr(boundaryAddr + currOffset)

        // Indicate that the variable identifier has now been allocated
        varObj.allocatedNow()

        // Update the amount of space taken up on the stack relative to the boundary and the current stack frame
        val size = decl.type.getStackSize()
        scope.updatePtrOffset(size)

        // Free the destination register for future use
        Registers.free(dest)
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        // Generate code for the right hand side of the statement
        visit(assign.rhs)
        val dest: Register = assign.rhs.getDestReg()

        val lhs: AssignLHSAST = assign.lhs
        when {
            lhs.ident != null -> {
                // Find the corresponding identifier and calculate the offset relative to the current sp
                val diff = calculateIdentSpOffset(lhs.ident.value, assign, 0)

                // Find the corresponding variable identifier
                val varIdent = assign.st().lookUpAllAndCheckAllocation(lhs.ident.value)!!

                // Store the value at the destination register at the calculated offset to the sp
                val mode: AddressingMode.AddressingMode2 = if (diff == 0) {
                    AddressingMode.AddressingMode2(Registers.sp)
                } else {
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(diff))
                }

                AssemblyRepresentation.addMainInstr(
                    getStoreInstruction(
                        dest,
                        mode,
                        varIdent.getType()
                    )
                )
            }
            lhs.arrElem != null -> {
                val arrElem = lhs.arrElem!!

                // load the address of the elem into a register
                visitArrayElemFstPhase(arrElem)
                val arrDest: Register = arrElem.getDestReg()

                // write to this address to update the value
                AssemblyRepresentation.addMainInstr(
                    getStoreInstruction(
                        dest,
                        AddressingMode.AddressingMode2(arrDest),
                        arrElem.getType()
                    )
                )

                Registers.free(arrDest)
            }
            lhs.pairElem != null -> {
                val pairElem: PairElemAST = lhs.pairElem!!

                // load the address of the pair elem into a register
                visitPairElemFstPhase(pairElem)
                val pairLocation: Register = pairElem.getDestReg()

                // write to this address to update the value
                AssemblyRepresentation.addMainInstr(
                    StoreInstruction(
                        dest,
                        AddressingMode.AddressingMode2(pairLocation)
                    )
                )

                Registers.free(pairLocation)
            }
            else -> {
                // Do nothing
            }
        }

        Registers.free(dest)
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        visitInnerScope(begin, begin.stat)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        var type: TypeIdentifier? = TypeIdentifier()
        var reg: Register? = null

        val leftHand: AssignLHSAST = read.lhs
        val leftHandIdent: ExprAST.IdentAST? = leftHand.ident
        val leftHandPairElem: PairElemAST? = leftHand.pairElem

        // Set the destination register and the type
        if (leftHandIdent != null) {
            visitIdentForRead(leftHandIdent)
            reg = leftHandIdent.getDestReg()
            type = leftHandIdent.getType()
        } else if (leftHandPairElem != null) {
            visitPairElemAST(leftHandPairElem)
            reg = leftHandPairElem.getDestReg()
            type = leftHandPairElem.getType()
        }

        // Move the value in the destination register into r0
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, reg!!))

        // Call the relevant primitive function (depending on the type)
        if (type == TypeIdentifier.INT_TYPE) {
            AssemblyRepresentation.addPInstr(PInstruction.p_read_int())
        }
        if (type == TypeIdentifier.CHAR_TYPE) {
            AssemblyRepresentation.addPInstr(PInstruction.p_read_char())
        }

        // Free the destination register for future use
        Registers.free(reg)
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        // Evaluate the exiting instruction and get destination register
        visit(exit.expr)
        val dest: Register = exit.expr.getDestReg()

        // Move contents of the register in r0 for calling exit
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))
        AssemblyRepresentation.addMainInstr(BranchInstruction("exit", Condition.L))

        // Free the destination register for future use
        Registers.free(dest)
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        // Evaluate the expression that you are freeing and obtain the destination register
        visit(free.expr)
        val dest: Register = free.expr.getDestReg()

        // Move the contents of the destination register into r0
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))

        // Add the primitive instruction corresponding to the type of the expression
        if (free.expr.getType() is TypeIdentifier.ArrayIdentifier) {
            AssemblyRepresentation.addPInstr(PInstruction.p_free_array())
        } else {
            AssemblyRepresentation.addPInstr(PInstruction.p_free_pair())
        }

        // Free the destination register for future use
        Registers.free(dest)
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Evaluation of the condition expression
        val condition: ExprAST = ifStat.condExpr
        visit(condition)

        // Condition checking
        val destination: Register = condition.getDestReg()
        AssemblyRepresentation.addMainInstr(CompareInstruction(destination, Immediate(0)))

        // Branch off to the 'else' body if the condition evaluated to false
        val elseLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(elseLabel.getLabel(), Condition.EQ))

        // Free the destination register
        Registers.free(destination)

        // Visit the 'then' branch
        visitInnerScope(ifStat, ifStat.thenStat)

        // Unconditionally jump to the label of whatever follows the if statement in the program
        val nextLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(nextLabel.getLabel()))

        // Label and assembly for the 'else' body, starting with updating stack pointer and allocating any stack space
        AssemblyRepresentation.addMainInstr(elseLabel)
        // Visit the 'else' branch
        visitInnerScope(ifStat, ifStat.elseStat)

        // Make label for whatever follows the if statement
        AssemblyRepresentation.addMainInstr(nextLabel)

        // Free the destination register for future use
        Registers.free(destination)
    }

    // Call and add IO instructions
    override fun visitPrintAST(print: StatementAST.PrintAST) {
        // Evaluate expression to be printed and obtain the register where the result is held
        visit(print.expr)
        val reg: Register = print.expr.getDestReg()
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, reg))

        val type = if (print.expr is ExprAST.ArrayElemAST) {
            print.expr.getElemType()
        } else {
            print.expr.getType()
        }

        when (type) {
            is TypeIdentifier.IntIdentifier -> {
                AssemblyRepresentation.addPInstr(PInstruction.p_print_int())
            }
            is TypeIdentifier.BoolIdentifier -> {
                AssemblyRepresentation.addPInstr(PInstruction.p_print_bool())
            }
            is TypeIdentifier.CharIdentifier -> {
                AssemblyRepresentation.addMainInstr(BranchInstruction("putchar", Condition.L))
            }
            is TypeIdentifier.StringIdentifier, TypeIdentifier.ArrayIdentifier(
                TypeIdentifier.CHAR_TYPE,
                0
            ) -> {
                AssemblyRepresentation.addPInstr(PInstruction.p_print_string())
            }
            is TypeIdentifier.PairIdentifier, is TypeIdentifier.PairLiterIdentifier, is TypeIdentifier.ArrayIdentifier -> {
                AssemblyRepresentation.addPInstr(PInstruction.p_print_reference())
            }
        }

        if (print.newLine) {
            AssemblyRepresentation.addPInstr(PInstruction.p_print_ln())
        }

        // Free the register for future use
        Registers.free(reg)
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        // Evaluate the expression you want to return and access the register that holds the value
        visit(ret.expr)
        val dest: Register = ret.expr.getDestReg()

        // Move the value into r0 and pop the program counter
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))

        // Restore the stack pointer depending on how much stack space has been allocated thus far
        restoreStackPointer(ret, ret.getStackSizeAllocated())
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))

        // Free the destination register for future use
        Registers.free(dest)
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        visit(seq.stat1)
        visit(seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        // Unconditional branch to check the loop condition
        val condLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(condLabel.getLabel()))

        // Label for loop body
        val bodyLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(bodyLabel)

        visitInnerScope(whileStat, whileStat.body)

        // Label for condition checking
        AssemblyRepresentation.addMainInstr(condLabel)

        // Evaluate the looping conditional expression
        visit(whileStat.loopExpr)
        val reg: Register = whileStat.loopExpr.getDestReg()

        // Comparison and jump if equal
        AssemblyRepresentation.addMainInstr(
            CompareInstruction(
                reg,
                Immediate(1)
            )
        )

        // Free the register for future use
        Registers.free(reg)
        AssemblyRepresentation.addMainInstr(BranchInstruction(bodyLabel.getLabel(), Condition.EQ))
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        val intValue = Integer.parseInt(liter.sign + liter.value)
        val register = Registers.allocate()
        val mode: AddressingMode = AddressingMode.AddressingLabel("$intValue")
        liter.setDestReg(register)
        AssemblyRepresentation.addMainInstr(LoadInstruction(register, mode))
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        val intValue = liter.getValue()
        val register = Registers.allocate()
        liter.setDestReg(register)
        AssemblyRepresentation.addMainInstr(MoveInstruction(register, Immediate(intValue)))
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        val register = Registers.allocate()
        liter.setDestReg(register)
        if (liter.value == "'\\0'") {
            AssemblyRepresentation.addMainInstr(MoveInstruction(register, Immediate(0)))
        } else {
            AssemblyRepresentation.addMainInstr(
                MoveInstruction(
                    register,
                    ImmediateChar(liter.value)
                )
            )
        }
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        val register = Registers.allocate()
        liter.setDestReg(register)
        val label = MessageLabelInstruction.getUniqueLabel(liter.value)
        AssemblyRepresentation.addDataInstr(label)
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                register,
                AddressingMode.AddressingLabel(label.getLabel())
            )
        )
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        /* a pair liter is null so will have address zero
         * so we load the value zero into a destination register */
        val register = Registers.allocate()

        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                register,
                AddressingMode.AddressingLabel("0")
            )
        )

        liter.setDestReg(register)
    }

    private fun visitIdentForRead(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, true)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, false)
    }

    private fun visitIdentGeneral(ident: ExprAST.IdentAST, read: Boolean) {
        val paramOffset = ident.getParamOffset()
        val spOffset: Int = calculateIdentSpOffset(ident.value, ident, paramOffset)
        val register: Register = Registers.allocate()

        if (read) {
            AssemblyRepresentation.addMainInstr(
                AddInstruction(
                    register,
                    Registers.sp,
                    Immediate(spOffset)
                )
            )
        } else {
            val type = ident.getType()
            var mode: AddressingMode =
                AddressingMode.AddressingMode2(Registers.sp, Immediate(spOffset))

            if (type is TypeIdentifier.BoolIdentifier || type is TypeIdentifier.CharIdentifier) {
                mode = AddressingMode.AddressingMode3(Registers.sp, Immediate(spOffset))
            }
            AssemblyRepresentation.addMainInstr(LoadInstruction(register, mode))
        }

        // Set the destination register to the register that was allocated
        ident.setDestReg(register)
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        // load the address of the array elem
        visitArrayElemFstPhase(arrayElem)
        val dest: Register = arrayElem.getDestReg()

        // load the value at the address into the destination register
        val type: TypeIdentifier = arrayElem.getElemType()
        when (type.getStackSize()) {
            ONE_BYTE -> {
                AssemblyRepresentation.addMainInstr(
                    LoadInstruction(
                        dest,
                        AddressingMode.AddressingMode3(dest, Immediate(0))
                    )
                )
            }
            else -> {
                AssemblyRepresentation.addMainInstr(
                    LoadInstruction(
                        dest,
                        AddressingMode.AddressingMode2(dest)
                    )
                )
            }
        }
    }

    private fun visitArrayElemFstPhase(arrayElem: ExprAST.ArrayElemAST) {
        val offset: Int = calculateIdentSpOffset(arrayElem.ident, arrayElem, 0)

        // Move the start of the array into dest register
        val dest: Register = Registers.allocate()
        arrayElem.setDestReg(dest)
        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                dest,
                Registers.sp,
                Immediate(offset)
            )
        )

        // For each element, change dest to the location of that index
        // (this may be the address of another element)
        for (expr in arrayElem.exprs) {
            // do LDR rX [rX] in case rX represents the address of an array
            AssemblyRepresentation.addMainInstr(
                LoadInstruction(
                    dest,
                    AddressingMode.AddressingMode2(dest)
                )
            )

            visit(expr)
            val exprDest: Register = expr.getDestReg()

            // Set parameters and branch to check the index
            AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, exprDest))
            AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r1, dest))
            AssemblyRepresentation.addPInstr(PInstruction.p_check_array_bounds())

            AssemblyRepresentation.addMainInstr(
                AddInstruction(
                    dest,
                    dest,
                    Immediate(TypeIdentifier.INT_SIZE)
                )
            )

            val type: TypeIdentifier = arrayElem.getElemType()
            when (type.getStackSize()) {
                FOUR_BYTES -> {
                    AssemblyRepresentation.addMainInstr(
                        AddInstruction(
                            dest,
                            dest,
                            ShiftOperand(exprDest, ShiftOperand.Shift.LSL, 2)
                        )
                    )
                }
                else -> {
                    AssemblyRepresentation.addMainInstr(AddInstruction(dest, dest, exprDest))
                }
            }
            // Free the destination register for future use
            Registers.free(exprDest)
        }
    }


    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        // Evaluate the single operand and get the register holding the result
        visit(unop.expr)
        unop.setDestReg(unop.expr.getDestReg())

        when (unop.operator) {
            "-" -> visitNeg(unop)
            "!" -> visitNot(unop)
            "len" -> visitLen(unop)
        }
    }

    private fun visitLen(unop: ExprAST.UnOpAST) {
        val dest: Register = unop.getDestReg()

        // load the value of the length into the destination register
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(dest)
            )
        )
    }

    private fun visitNot(unop: ExprAST.UnOpAST) {
        val dest: Register = unop.getDestReg()
        AssemblyRepresentation.addMainInstr(
            EorInstruction(
                dest,
                dest,
                Immediate(1)
            )
        )
    }

    private fun visitNeg(unop: ExprAST.UnOpAST) {
        val dest: Register = unop.expr.getDestReg()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(Registers.sp)
            )
        )
        AssemblyRepresentation.addMainInstr(ReverseSubtractInstruction(dest, dest, Immediate(0)))

        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )
        AssemblyRepresentation.addPInstr(PInstruction.p_throw_overflow_error())
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        if (Registers.full()) {
            visit(binop.expr2)
            AssemblyRepresentation.addMainInstr(PushInstruction(Registers.r10))
            Registers.free(Registers.r10)
            visit(binop.expr1)
            visitBinOpStack(binop)
        } else {
            visit(binop.expr1)
            visit(binop.expr2)
            visitBinOp(binop)
        }
    }

    private fun visitBinOpStack(binop: ExprAST.BinOpAST) {
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.r11))
        binop.expr2.setDestReg(Registers.r11)
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
            else -> {
            }
        }
    }


    private fun visitAdd(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        val dest1: Register = expr1.getDestReg()
        val dest2: Register = expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                dest1,
                dest1,
                dest2,
                Condition.S
            )
        )

        Registers.free(dest2)
        binop.setDestReg(dest1)

        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )
        AssemblyRepresentation.addPInstr(PInstruction.p_throw_overflow_error())
    }

    private fun visitSub(binop: ExprAST.BinOpAST) {
        val dest: Register = binop.expr1.getDestReg()
        val dest2: Register = binop.expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(
            SubtractInstruction(
                dest,
                dest,
                dest2,
                Condition.S
            )
        )

        Registers.free(dest2)

        binop.setDestReg(dest)
        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )

        AssemblyRepresentation.addPInstr(PInstruction.p_throw_overflow_error())
    }

    private fun visitMultiply(binop: ExprAST.BinOpAST) {
        val dest1: Register = binop.expr1.getDestReg()
        val dest2: Register = binop.expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(SMultiplyInstruction(dest1, dest2))
        Registers.free(dest2)

        AssemblyRepresentation.addMainInstr(
            CompareInstruction(
                dest2,
                ShiftOperand(dest1, ShiftOperand.Shift.ASR, 31)
            )
        )

        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LNE
            )
        )

        AssemblyRepresentation.addPInstr(PInstruction.p_throw_overflow_error())

        binop.setDestReg(dest1)
    }

    private fun visitDivMod(binop: ExprAST.BinOpAST) {
        // get result from expr1 and move into param register 1
        val dest1: Register = binop.expr1.getDestReg()
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest1))
        Registers.free(dest1)

        // get result from expr2 and mvoe into param register 2
        val dest2: Register = binop.expr2.getDestReg()
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r1, dest2))
        Registers.free(dest2)

        AssemblyRepresentation.addPInstr(PInstruction.p_check_divide_by_zero())

        AssemblyRepresentation.addMainInstr(
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
        val dest: Register = Registers.allocate()

        if (binop.operator == "/") {
            AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Registers.r0))
        } else {
            AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Registers.r1))
        }


        binop.setDestReg(dest)
    }

    private fun visitAndOr(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        when {
            expr1 is ExprAST.BoolLiterAST -> {
                val dest: Register = expr2.getDestReg()

                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest, dest, Immediate(expr1.getValue()))
                    } else {
                        OrInstruction(dest, dest, Immediate(expr1.getValue()))
                    }
                )

                binop.setDestReg(dest)
            }

            expr2 is ExprAST.BoolLiterAST -> {
                val dest: Register = expr1.getDestReg()
                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest, dest, Immediate(expr2.getValue()))
                    } else {
                        OrInstruction(dest, dest, Immediate(expr2.getValue()))
                    }
                )

                binop.setDestReg(dest)
            }

            else -> {
                val dest1: Register = expr1.getDestReg()
                val dest2: Register = expr2.getDestReg()

                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest1, dest1, dest2)
                    } else {
                        OrInstruction(dest1, dest1, dest2)
                    }
                )

                Registers.free(dest2)

                binop.setDestReg(dest1)
            }
        }
    }

    private fun visitCompare(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        when (expr1.getType()) {
            is TypeIdentifier.IntIdentifier -> {
                val dest: Register = expr1.getDestReg()

                when (expr2) {
                    is ExprAST.IntLiterAST -> {
                        AssemblyRepresentation.addMainInstr(
                            CompareInstruction(
                                dest,
                                Immediate(expr2.getValue())
                            )
                        )
                    }

                    else -> {
                        val dest2: Register = expr2.getDestReg()
                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, dest2))
                        Registers.free(dest2)
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.GT
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.LE
                            )
                        )
                    }
                    ">=" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.GE
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.LT
                            )
                        )
                    }
                    "<" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.LT
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.GE
                            )
                        )
                    }
                    "<=" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.LE
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.GT
                            )
                        )
                    }
                }

                binop.setDestReg(dest)
            }

            is TypeIdentifier.CharIdentifier -> {
                val dest: Register = expr1.getDestReg()

                when (expr2) {
                    is ExprAST.CharLiterAST -> {
                        AssemblyRepresentation.addMainInstr(
                            CompareInstruction(
                                dest,
                                ImmediateChar(expr2.value)
                            )
                        )
                    }

                    else -> {
                        val dest2: Register = expr2.getDestReg()

                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, dest2))
                        Registers.free(dest2)
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.HI
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.LS
                            )
                        )
                    }
                    ">=" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.HS
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.LO
                            )
                        )
                    }
                    "<" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.LO
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.HS
                            )
                        )
                    }
                    "<=" -> {
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(1),
                                Condition.LS
                            )
                        )
                        AssemblyRepresentation.addMainInstr(
                            MoveInstruction(
                                dest,
                                Immediate(0),
                                Condition.HI
                            )
                        )
                    }
                }
                binop.setDestReg(dest)

            }
        }
    }

    private fun visitEquality(binop: ExprAST.BinOpAST) {
        val dest1: Register = binop.expr1.getDestReg()
        val dest2: Register = binop.expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(CompareInstruction(dest1, dest2))

        Registers.free(dest2)

        when (binop.operator) {
            "==" -> {
                AssemblyRepresentation.addMainInstr(
                    MoveInstruction(
                        dest1,
                        Immediate(1),
                        Condition.EQ
                    )
                )
                AssemblyRepresentation.addMainInstr(
                    MoveInstruction(
                        dest1,
                        Immediate(0),
                        Condition.NE
                    )
                )
            }

            "!=" -> {
                AssemblyRepresentation.addMainInstr(
                    MoveInstruction(
                        dest1,
                        Immediate(1),
                        Condition.NE
                    )
                )
                AssemblyRepresentation.addMainInstr(
                    MoveInstruction(
                        dest1,
                        Immediate(0),
                        Condition.EQ
                    )
                )
            }
        }

        binop.setDestReg(dest1)
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
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("$arrAllocation")
            )
        )

        AssemblyRepresentation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // store the array address in an allocated register
        val arrLocation: Register = Registers.allocate()
        AssemblyRepresentation.addMainInstr(MoveInstruction(arrLocation, Registers.r0))

        // we start the index at +4 so we can store the size of the array at +0
        var arrIndex = TypeIdentifier.INT_SIZE
        for (elem in arrayLiter.elems) {
            visit(elem)
            val dest: Register = elem.getDestReg()

            // store the value of elem at the current index
            AssemblyRepresentation.addMainInstr(
                getStoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(arrLocation, Immediate(arrIndex)),
                    elem.getType()
                )
            )

            arrIndex += elemsSize
            Registers.free(dest)
        }

        // store the length of the array at arrLocation +0
        val sizeDest: Register = Registers.allocate()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                sizeDest,
                AddressingMode.AddressingLabel("${arrayLiter.elemsLength()}")
            )
        )

        // store the length of the array at the front of its allocated space
        AssemblyRepresentation.addMainInstr(
            StoreInstruction(
                sizeDest,
                AddressingMode.AddressingMode2(arrLocation)
            )
        )
        Registers.free(sizeDest)

        arrayLiter.setDestReg(arrLocation)
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

            AssemblyRepresentation.addMainInstr(
                getStoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * size), true),
                    arg.getType()
                )
            )

            val currStackPtr: Int = symTab.getStackPtr()
            symTab.setStackPtr(currStackPtr - size)

            // Free the destination register for future use and update argsSize
            Registers.free(dest)
            argsSize += size
        }

        // Reset the parameter offset for future use of the symbol table
        symTab.setParamOffset(0)
        symTab.updatePtrOffset(-1 * argsSize)

        // Branch to the function label in the assembly code
        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "f_${funcCall.funcName}",
                Condition.L
            )
        )

        // Restore the stack pointer
        restoreStackPointer(funcCall, argsSize)

        // Move the result of the function into an available register
        val reg: Register = Registers.allocate()
        AssemblyRepresentation.addMainInstr(MoveInstruction(reg, Registers.r0))

        // Set the destination register for future use
        funcCall.setDestReg(reg)
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        // allocate 8 bytes total for the pair object
        // load value PAIR_SIZE into r0 as param for malloc
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("${2 * TypeIdentifier.ADDR_SIZE}")
            )
        )

        // branch to malloc
        AssemblyRepresentation.addMainInstr(
            BranchInstruction("malloc", Condition.L)
        )

        // move malloc result into allocated register
        val pairLocation = Registers.allocate()
        AssemblyRepresentation.addMainInstr(MoveInstruction(pairLocation, Registers.r0))

        // visit & allocate the pair's children using the helper function
        allocatePairElem(newPair.fst, pairLocation, 0)
        allocatePairElem(newPair.snd, pairLocation, TypeIdentifier.ADDR_SIZE)

        // set destination register
        newPair.setDestReg(pairLocation)
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
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0, AddressingMode.AddressingLabel("$allocation")
            )
        )

        AssemblyRepresentation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // move value for dest into the address given by malloc
        AssemblyRepresentation.addMainInstr(
            getStoreInstruction(
                dest,
                AddressingMode.AddressingMode2(Registers.r0),
                elem.getType()
            )
        )

        Registers.free(dest)

        // store value in r0 at pairLocation (+ offset)
        AssemblyRepresentation.addMainInstr(
            StoreInstruction(
                Registers.r0,
                AddressingMode.AddressingMode2(pairLocation, Immediate(offset))
            )
        )
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        // load the address of the pair elem into a register using the helper method
        visitPairElemFstPhase(pairElem)
        val dest: Register = pairElem.getDestReg()

        // load the value at the given address into the same register
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(dest)
            )
        )

        // (we don't free the dest register here as it contains the value we want to use elsewhere)
    }

    /* completes visiting pair elems to the point that the address of it is in the dest register,
     * which we can load again to get the value or write to
     * Sets the destination register for the given pair elem
     */
    private fun visitPairElemFstPhase(pairElem: PairElemAST) {
        visit(pairElem.elem)
        val dest: Register = pairElem.elem.getDestReg()

        // set param and branch to check for null dereference
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))
        AssemblyRepresentation.addPInstr(PInstruction.p_check_null_pointer())

        /* if the pair elem is snd then we want to add ADDR_SIZE (+4) as an offset, otherwise it is fst and we
         * don't need an offset */
        if (!pairElem.isFst) {
            AssemblyRepresentation.addMainInstr(
                LoadInstruction(
                    dest,
                    AddressingMode.AddressingMode2(dest, Immediate(TypeIdentifier.ADDR_SIZE))
                )
            )
        } else {
            AssemblyRepresentation.addMainInstr(
                LoadInstruction(
                    dest,
                    AddressingMode.AddressingMode2(dest)
                )
            )
        }

        // set the destination register
        pairElem.setDestReg(dest)
    }
}
