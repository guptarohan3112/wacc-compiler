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

    /* UTILITY METHODS USED BY DIFFERENT VISIT METHODS IN THIS VISITOR
       ---------------------------------------------------------------
     */
    private fun startNewBody(bodyInScope: StatementAST): Int {
        AssemblyRepresentation.addMainInstr(PushInstruction(Registers.lr))
        return calculateStackSize(bodyInScope)
    }

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

    private fun setUpInnerScope(stat: StatementAST, child: StatementAST): Int {
        val currSp: Int = stat.st().getStackPtr()
        child.st().setStackPtr(child.st().getStackPtr() + currSp)

        val stackSize: Int = calculateStackSize(child)
        child.st().setStackPtr(child.st().getStackPtr() - stackSize)
        return stackSize
    }

    // Restore the stack pointer and print out the relevant assembly code
    private fun restoreStackPointer(stat: AST, stackSize: Int) {
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
        // Not sure if this is needed
        stat.st().setStackPtr(stat.st().getStackPtr() + stackSize)
    }

    // a helper function for adding "B" to "STR" if the given
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

    private fun calculateIdentSpOffset(ident: ExprAST.IdentAST, scope: AST): Int {
        val identObj: IdentifierObject = scope.st().lookUpAllAndCheckAllocation(ident.value)!!

        var spOffset = 0
        if (identObj is VariableIdentifier) {
            // Calculate the stack space between the current stack pointer and the identifier
            val identOffset: Int = identObj.getAddr()
            val sp: Int = scope.st().getStackPtr()
            spOffset = identOffset - sp
        } else if (identObj is ParamIdentifier) {
            spOffset = identObj.getOffset() + scope.st().getStackSizeAllocated()
        }

        return spOffset
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
        prog.stat.st().setStackPtr(prog.stat.st().getStackPtr() - stackSize)

        // Generate assembly code for the body statement
        visit(prog.stat)

        restoreStackPointer(prog.stat, stackSize)

        // Return the exit code (assuming 0 upon success) and pop the program counter
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("0")
            )
        )
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
        // Put in the .ltorg directive?
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
        func.body.st().setStackSizeAllocated(stackSize)

        if (func.paramList != null) {
            visitAndUpdateParams(stackSize, func.paramList)
//            funcIdent.getSymbolTable().updatePtrOffset(-4)
        }

        // Generate assembly code for the body statement
        visit(func.body)

//        // Restore the stack pointer
//        restoreStackPointer(func.body, stackSize)
//
//        // Restore the program counter
//        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
    }

    private fun visitAndUpdateParams(stackSize: Int, list: ParamListAST) {
        val symbolTable: SymbolTable = list.st()
        var offset = 4

        // Store the offset of the parameter relative to the stack address of the first parameter
        for (param in list.paramList) {
//            val currOffset: Int = symbolTable.getStackPtrOffset()
            val paramIdent: ParamIdentifier = symbolTable.lookup(param.name) as ParamIdentifier
            paramIdent.setOffset(offset)
//            paramIdent.allocatedNow()
            offset += param.getType(symbolTable).getStackSize()
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

        if (currOffset != 0 && scope.getStackPtrOffset() == 0) {
            scope.updatePtrOffset(currOffset)
        }

        val dest: Register = decl.assignment.getDestReg()

        // Store the value at the destination register at a particular offset to the stack pointer (bottom up)
//        val currOffset = scope.getStackPtrOffset()
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
        varObj.allocatedNow()

        // Update the amount of space taken up on the stack relative to the boundary and the current stack frame
        val size = decl.type.getType().getStackSize()
        scope.updatePtrOffset(size)
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
                val diff = calculateIdentSpOffset(lhs.ident, assign)
                // Find the corresponding variable identifier
                val varIdent = assign.st().lookUpAllAndCheckAllocation(lhs.ident.value)!!

//                var diff = 0
//                when (varIdent) {
//                    is ParamIdentifier -> {
//                        diff = varIdent.getOffset()
//                    }
//                    is VariableIdentifier -> {
//                        val offset: Int = varIdent.getAddr()
//                        val sp: Int = assign.st().getStackPtr()
//                        diff = offset - sp
//                    }
//                    else -> {
//                    }
//                }

                val mode: AddressingMode.AddressingMode2 = if (diff == 0) {
                    AddressingMode.AddressingMode2(Registers.sp)
                } else {
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(diff))
                }

                // Store the value at the destination register at the calculated offset to the sp
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
                visitArrayElemFstPhase(arrElem)
                val arrDest: Register = arrElem.getDestReg()

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

                visitPairElemFstPhase(pairElem)
                val pairLocation: Register = pairElem.getDestReg()

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


//      TODO: Do we need to free? Is this the correct register?
        Registers.free(dest)
//        assign.lhs.setDestReg(dest)
    }

    // Store the address of the program counter (?) into the link registers so that a function can
    // return to this address when completing its functionality
    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        val stackSize: Int = setUpInnerScope(begin, begin.stat)

        begin.stat.st().setStackSizeAllocated(begin.st().getStackSizeAllocated() + stackSize)

        // Generate assembly code for the body statement
        visit(begin.stat)

        // Restore the stack pointer
        restoreStackPointer(begin.stat, stackSize)
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        var type: TypeIdentifier? = TypeIdentifier()
        var reg: Register? = null

        if (read.lhs.ident != null) {
            visitIdentForRead(read.lhs.ident)
            reg = read.lhs.ident.getDestReg()
            type = read.lhs.ident.getType()
        } else if (read.lhs.pairElem != null) {
            visitPairElemAST(read.lhs.pairElem!!)
            reg = read.lhs.pairElem!!.getDestReg()
            type = read.lhs.pairElem!!.getType()
        }

        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, reg!!))

        if (type == TypeIdentifier.INT_TYPE) {
            AssemblyRepresentation.addPInstr(PInstruction.p_read_int())
        }

        if (type == TypeIdentifier.CHAR_TYPE) {
            AssemblyRepresentation.addPInstr(PInstruction.p_read_char())
        }

        Registers.free(reg)
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        // Evaluate the exiting instruction and get destination register
        visit(exit.expr)
        val dest: Register = exit.expr.getDestReg()

        // Move contents of the register in r0 for calling exit
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))
        AssemblyRepresentation.addMainInstr(BranchInstruction("exit", Condition.L))
        Registers.free(dest)
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        // Evaluate the expression that you are freeing and obtain the destination register
        visit(free.expr)
        val dest: Register = free.expr.getDestReg()

        // Move the contents of the destination register into r0
        // Move the contents of the destination register into r0
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))

        // Add the IO instruction and branch instruction corresponding to the type of the expression
        if (free.expr.getType() is TypeIdentifier.ArrayIdentifier) {
            AssemblyRepresentation.addPInstr(PInstruction.p_free_array())
        } else {
            AssemblyRepresentation.addPInstr(PInstruction.p_free_pair())
        }

        Registers.free(dest)
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Evaluation of the condition expression
        visit(ifStat.condExpr)

        // Condition checking
        val destination: Register = ifStat.condExpr.getDestReg()
        AssemblyRepresentation.addMainInstr(CompareInstruction(destination, Immediate(0)))

        // Branch off to the 'else' body if the condition evaluated to false
        val elseLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(elseLabel.getLabel(), Condition.EQ))

        // Free the destination register
        Registers.free(destination)

        // Update the stack pointer of the inner scope and allocate any stack space for the 'then' branch
        val stackSizeThen = setUpInnerScope(ifStat, ifStat.thenStat)

        ifStat.thenStat.st()
            .setStackSizeAllocated(ifStat.st().getStackSizeAllocated() + stackSizeThen)

        // Otherwise enter the 'then' body
        visit(ifStat.thenStat)

        // Restore the stack pointer for the 'then' branch
        restoreStackPointer(ifStat.thenStat, stackSizeThen)

        // Unconditionally jump to the label of whatever follows the if statement in the program
        val nextLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(nextLabel.getLabel()))

        // Label and assembly for the 'else' body, starting with updating stack pointer and allocating any stack space
        AssemblyRepresentation.addMainInstr(elseLabel)
        val stackSizeElse = setUpInnerScope(ifStat, ifStat.elseStat)

        ifStat.elseStat.st()
            .setStackSizeAllocated(ifStat.st().getStackSizeAllocated() + stackSizeElse)

        visit(ifStat.elseStat)

        // Restore the stack pointer for the 'else' branch
        restoreStackPointer(ifStat.elseStat, stackSizeElse)

        // Make label for whatever follows the if statement
        AssemblyRepresentation.addMainInstr(nextLabel)

        Registers.free(destination)
    }

    // Call and add IO instructions
    override fun visitPrintAST(print: StatementAST.PrintAST) {
        visit(print.expr)
        val reg: Register = print.expr.getDestReg()
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, reg))

        val type = if (print.expr is ExprAST.ArrayElemAST) {
            print.expr.getType().getType() // double call to get type to get the elems type
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

        Registers.free(reg)
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        // Evaluate the expression you want to return and access the register that holds the value
        visit(ret.expr)
        val dest: Register = ret.expr.getDestReg()

        // Move the value into r0 and pop the program counter
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))

        restoreStackPointer(ret, ret.st().getStackSizeAllocated())
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))

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

        val stackSize: Int = setUpInnerScope(whileStat, whileStat.body)

        whileStat.body.st()
            .setStackSizeAllocated(whileStat.st().getStackSizeAllocated() + stackSize)

        // Loop body
        visit(whileStat.body)

        // Restore the stack pointer
        restoreStackPointer(whileStat.body, stackSize)

        // Label for condition checking
        AssemblyRepresentation.addMainInstr(condLabel)

        visit(whileStat.loopExpr)
        val reg: Register = whileStat.loopExpr.getDestReg()

        // Comparison and jump if equal
        AssemblyRepresentation.addMainInstr(
            CompareInstruction(
                reg,
                Immediate(1)
            )
        )
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
        val intValue = if (liter.value == "true") 1 else 0
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
        val register = Registers.allocate()
        liter.setDestReg(register)
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                register,
                AddressingMode.AddressingLabel("0")
            )
        )
    }

    private fun visitIdentForRead(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, true)
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        visitIdentGeneral(ident, false)
    }

    private fun visitIdentGeneral(ident: ExprAST.IdentAST, read: Boolean) {
        val spOffset: Int = calculateIdentSpOffset(ident, ident)

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
            // Initialise the mode to be 2 or 3 depending on the type of the identifier
            var mode: AddressingMode =
                AddressingMode.AddressingMode2(Registers.sp, Immediate(spOffset))
            if (type is TypeIdentifier.BoolIdentifier || type is TypeIdentifier.CharIdentifier) {
                mode = AddressingMode.AddressingMode3(Registers.sp, Immediate(spOffset))
            }
            // Obtain an available register and load stack value into this register
            AssemblyRepresentation.addMainInstr(LoadInstruction(register, mode))
        }

        ident.setDestReg(register)
    }

    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        visitArrayElemFstPhase(arrayElem)
        val dest: Register = arrayElem.getDestReg()

        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(dest)
            )
        )
    }

    private fun visitArrayElemFstPhase(arrayElem: ExprAST.ArrayElemAST) {
        val ident: VariableIdentifier =
            arrayElem.st().lookupAll(arrayElem.ident) as VariableIdentifier
        val sp = arrayElem.st().getStackPtr()
        val offset: Int = ident.getAddr()

        // move the start of the array into dest register
        val dest: Register = Registers.allocate()
        arrayElem.setDestReg(dest)
        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                dest,
                Registers.sp,
                Immediate(offset - sp)
            )
        )

        // for each element, change dest to the location of that index
        // (this may be the address of another
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

            // set parameters and branch to check the index
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

            when (expr.getType().getStackSize()) {
                4 -> {
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
            Registers.free(exprDest)

        }
    }


    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
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

        when {
//            expr1 is ExprAST.IntLiterAST -> {
//                visit(expr2)
//                val dest: Register = expr2.getDestReg()
//
//                AssemblyRepresentation.addMainInstr(
//                    AddInstruction(
//                        dest,
//                        dest,
//                        Immediate(expr1.getValue()),
//                        Condition.S
//                    )
//                )
//
//                binop.setDestReg(dest)
//            }
//            expr2 is ExprAST.IntLiterAST -> {
//                visit(expr1)
//                val dest: Register = expr1.getDestReg()
//                AssemblyRepresentation.addMainInstr(
//                    AddInstruction(
//                        dest,
//                        dest,
//                        Immediate(expr2.getValue()),
//                        Condition.S
//                    )
//                )
//
//                binop.setDestReg(dest)
//            }
            else -> {
                visit(expr1)
                visit(expr2)

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
            }
        }

        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "p_throw_overflow_error",
                Condition.LVS
            )
        )
        AssemblyRepresentation.addPInstr(PInstruction.p_throw_overflow_error())
    }

    private fun visitSub(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)

        val dest: Register = binop.expr1.getDestReg()

        /* we can only optimise for expr2 being int liter since we have
             * SUB rd, rn, op -> rd = rn - op, so expr1 must always be placed
             * in a register */
        when (binop.expr2) {
//            is ExprAST.IntLiterAST -> {
//                AssemblyRepresentation.addMainInstr(
//                    SubtractInstruction(
//                        dest,
//                        dest,
//                        Immediate(binop.expr2.getValue()),
//                        Condition.S
//                    )
//                )
//            }
            else -> {
                visit(binop.expr2)
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
            }
        }

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
        visit(binop.expr1)
        visit(binop.expr2)

        val dest1: Register = binop.expr1.getDestReg()
        val dest2: Register = binop.expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(SMultiplyInstruction(dest1, dest2))
        Registers.free(dest2)

        binop.setDestReg(dest1)
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
    }

    private fun visitDivMod(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

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
                visit(expr2)
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
                visit(expr1)
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
                visit(expr1)
                visit(expr2)

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
                visit(expr1)
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
                        visit(expr2)
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
                visit(expr1)
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
                        visit(expr2)
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
        visit(binop.expr1)
        visit(binop.expr2)

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
            arrayLiter.elems[0].getType().getStackSize()
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
        var argsSize = 0
        val funcIdent = FunctionST.lookupAll(funcCall.funcName)!!

        for (arg in funcCall.args.reversed()) {
            visit(arg)
            val dest: Register = arg.getDestReg()

            val size: Int = arg.getType().getStackSize()

            AssemblyRepresentation.addMainInstr(
                getStoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * size), true),
                    arg.getType()
                )
            )
            funcCall.st().setStackPtr(funcCall.st().getStackPtr() - size)

            Registers.free(dest)

            argsSize += size
        }

//        val stackForLocalVar: Int = funcIdent.getStackSize()

//        funcCall.st().setStackPtr(funcCall.st().getStackPtr() - stackForLocalVar)

//        funcCall.st().setStackPtr(funcCall.st().getStackPtr() + argsSize)

        // Branch to the function label in the assembly code
        AssemblyRepresentation.addMainInstr(
            BranchInstruction(
                "f_${funcCall.funcName}",
                Condition.L
            )
        )

        //Restore the stack pointer
        restoreStackPointer(funcCall, argsSize)
//        if (argsSize > 0) {
//            AssemblyRepresentation.addMainInstr(
//                AddInstruction(
//                    Registers.sp,
//                    Registers.sp,
//                    Immediate(argsSize)
//                )
//            )
//        }

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
                AddressingMode.AddressingLabel("${2 * TypeIdentifier.PAIR_SIZE}")
            )
        )

        // branch to malloc
        AssemblyRepresentation.addMainInstr(
            BranchInstruction("malloc", Condition.L)
        )

        // move malloc result into allocated register
        val pairLocation = Registers.allocate()
        AssemblyRepresentation.addMainInstr(MoveInstruction(pairLocation, Registers.r0))

        // allocate fstSize bytes on heap for fst element, store in register somewhere
        visit(newPair.fst)
        val fstDest: Register = newPair.fst.getDestReg()

        // do allocation - move size of fst into param register then branch into malloc
        val allocation: Int = newPair.fst.getType().getStackSize()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0,
                AddressingMode.AddressingLabel("$allocation")
            )
        )

        AssemblyRepresentation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // move the value for fst into the address given by malloc
        AssemblyRepresentation.addMainInstr(
            getStoreInstruction(
                fstDest,
                AddressingMode.AddressingMode2(Registers.r0),
                newPair.fst.getType()
            )
        )
        Registers.free(fstDest)
        // store value in r0 at pairLocation (+0)
        AssemblyRepresentation.addMainInstr(
            StoreInstruction(
                Registers.r0,
                AddressingMode.AddressingMode2(pairLocation)
            )
        )

        // allocate sndSize bytes on heap for snd element, store in register somewhere
        visit(newPair.snd)
        val sndDest: Register = newPair.fst.getDestReg()

        // do allocation - move size of snd into param register then branch into malloc
        val allocation2: Int = newPair.snd.getType().getStackSize()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                Registers.r0, AddressingMode.AddressingLabel("$allocation2")
            )
        )

        AssemblyRepresentation.addMainInstr(BranchInstruction("malloc", Condition.L))

        // move value for snd into the address given by malloc
        AssemblyRepresentation.addMainInstr(
            getStoreInstruction(
                sndDest,
                AddressingMode.AddressingMode2(Registers.r0),
                newPair.snd.getType()
            )
        )
        Registers.free(sndDest)
        // store value in r0 at pairLocation + allocation
        AssemblyRepresentation.addMainInstr(
            StoreInstruction(
                Registers.r0,
                AddressingMode.AddressingMode2(pairLocation, Immediate(TypeIdentifier.ADDR_SIZE))
            )
        )

        newPair.setDestReg(pairLocation)
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        visitPairElemFstPhase(pairElem)
        val dest: Register = pairElem.getDestReg()

        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(dest)
            )
        )
    }

    /* completes visiting pair elems to the point that the address of it is in the dest register,
     * which we can load again to get the value or write to
     * returns the destination register
     */
    private fun visitPairElemFstPhase(pairElem: PairElemAST) {
        visit(pairElem.elem)
        val dest: Register = pairElem.elem.getDestReg()

        // set param and branch to check for null dereference
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))
        AssemblyRepresentation.addPInstr(PInstruction.p_check_null_pointer())

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

        pairElem.setDestReg(dest)
    }
}