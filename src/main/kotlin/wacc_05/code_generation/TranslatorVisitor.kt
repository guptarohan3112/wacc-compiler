package wacc_05.code_generation

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.code_generation.instructions.*
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.code_generation.instructions.LabelInstruction.Companion.getUniqueLabel
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.ParamIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class TranslatorVisitor : ASTBaseVisitor() {

    // Helper method that makes sufficient space on the stack and returns the space that has been allocated
    private fun setUpScopeBegin(bodyInScope: StatementAST): Int {
        AssemblyRepresentation.addMainInstr(PushInstruction(Registers.lr))

        // Calculate stack size for scope and decrement the stack pointer accordingly
        val stackSizeCalculator = StackSizeVisitor()
        val stackSize: Int = stackSizeCalculator.getStackSize(bodyInScope)
        if (stackSize != 0) {
            AssemblyRepresentation.addMainInstr(
                SubtractInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(stackSize)
                )
            )
        }

        return stackSize
    }

    override fun visitProgramAST(prog: ProgramAST) {
        // Generate assembly code for each of the functions declared in the program
        for (func in prog.functionList) {
            visit(func)
        }

        // Generate code for the main body
        AssemblyRepresentation.addMainInstr(LabelInstruction("main"))
        val stackSize: Int = setUpScopeBegin(prog.stat)

        // Decrement the stack pointer value and update the symbol table with this sp
        prog.stat.st().setStackPtr(prog.stat.st().getStackPtr() - stackSize)

        // Generate assembly code for the body statement
        visit(prog.stat)

        // Restore the stack pointer
        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                Registers.sp,
                Registers.sp,
                Immediate(stackSize)
            )
        )

        // Return the exit code (assuming 0 upon success) and pop the program counter
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, Immediate(0)))
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
        // Put in the .ltorg directive?
    }

    // TODO: Loading local variables and deal with parameters and offsets
    override fun visitFunctionAST(func: FunctionAST) {
        // Create the label for the function
        val funcLabel = "f_${func.funcName}"
        AssemblyRepresentation.addMainInstr(LabelInstruction(funcLabel))

        // Generate code to allocate space on the stack for local variables in the function
        val stackSize: Int = setUpScopeBegin(func.body)

        // Store how amount of allocated space for local variables on the corresponding function identifier
        val symTab: SymbolTable = func.st()
        val funcIdent = symTab.lookup(func.funcName) as FunctionIdentifier
        funcIdent.setStackSize(stackSize)

        if (func.paramList != null) {
            visit(func.paramList)
        }

        // Generate assembly code for the body statement
        visit(func.body)

        // Restore the stack pointer
        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                Registers.sp,
                Registers.sp,
                Immediate(stackSize)
            )
        )

        // Restore the program counter
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
    }

    // TODO: Look above
    override fun visitParamListAST(list: ParamListAST) {
        var offset = 0
        val symbolTable: SymbolTable = list.st()

        // Store the offset of the parameter relative to the stack address of the first parameter
        for (param in list.paramList) {
            val paramIdent: ParamIdentifier = symbolTable.lookup(param.name) as ParamIdentifier
            paramIdent.setOffset(offset)
            offset += param.getType().getStackSize()
        }
    }

    // There is no assembly code that needs to be generated for parameters.
    // Setting the correct offset of the parameter is done in the list
    override fun visitParamAST(param: ParamAST) {
        return
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        return
    }

    // TODO: keep track of how much of the allocated stack space has been taken up
    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        // Generate code for the right hand side of the declaration
        visit(decl.assignment)
        val dest: Register = decl.assignment.getDestReg()

        val scope: SymbolTable = decl.st()

        // Store the value at the destination register at a particular offset to the stack pointer (bottom up)
        val currOffset = scope.getStackPtrOffset()
        AssemblyRepresentation.addMainInstr(
            StoreInstruction(
                dest,
                AddressingMode.AddressingMode2(Registers.sp, Immediate(currOffset))
            )
        )

        // Set the absolute stack address of the variable in the corresponding variable identifier
        val boundaryAddr = scope.getStackPtr()
        val varObj: VariableIdentifier = scope.lookupAll(decl.varName) as VariableIdentifier
        varObj.setAddr(boundaryAddr + currOffset)

        // Update the amount of space taken up on the stack relative to the boundary and the current stack frame
        val size = decl.type.getType().getStackSize()
        scope.updatePtrOffset(size)
    }

    // TODO:
    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        TODO("Not yet implemented")
    }

    // Store the address of the program counter (?) into the link registers so that a function can
    // return to this address when completing its functionality
    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        setUpScopeBegin(begin.stat)
    }

    // TODO: IO
    override fun visitReadAST(read: StatementAST.ReadAST) {
        TODO("Not yet implemented")
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        val reg: Register = Registers.allocate() // or is it r4?
        // Load the allocated register with the exit code. Insert solution below
        when (exit.expr) {
            is ExprAST.IdentAST -> {
                // Need to get the address of the variable stored on the stack
                // How do we know the offset?
            }
            is ExprAST.IntLiterAST -> {
                val exitCode: Int = exit.expr.getValue()
                // Add the load instruction with the correct parameters
            }
            else -> {
                visit(exit.expr)
            }
        }
        // Move the exit code into r0 and then call the C function exit (system call will be imported from a library)
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, reg))
        AssemblyRepresentation.addMainInstr(BranchInstruction("exit", Condition.L))

    }

    // TOOO: Heap memory
    override fun visitFreeAST(free: StatementAST.FreeAST) {
        TODO("Not yet implemented")
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

        // Otherwise enter the 'then' body
        visit(ifStat.thenStat)

        // Unconditionally jump to the label of whatever follows the if statement in the program
        val nextLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(BranchInstruction(nextLabel.getLabel()))

        // Label and assembly for the 'else' body
        AssemblyRepresentation.addMainInstr(elseLabel)
        visit(ifStat.elseStat)

        // Make label for whatever follows the if statement
        AssemblyRepresentation.addMainInstr(nextLabel)
    }

    // Call and add IO instructions
    override fun visitPrintAST(print: StatementAST.PrintAST) {
        visit(print.expr)
        AssemblyRepresentation.addIOInstr(IOInstruction.p_print_ln())
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, print.expr.getDestReg()))
        // TODO: Need to push appropriate part in data to print the type of expression
        if (print.expr.getType() == TypeIdentifier.INT_TYPE) {
            // Add %d placeholder
            AssemblyRepresentation.addIOInstr(IOInstruction.p_print_int())
            AssemblyRepresentation.addMainInstr(BranchInstruction("p_print_int", Condition.L))
        }
        if (print.expr.getType() == TypeIdentifier.BOOL_TYPE) {
        }
        if (print.expr.getType() == TypeIdentifier.CHAR_TYPE) {
        }
        if (print.expr.getType() == TypeIdentifier.STRING_TYPE) {
        }
        if (print.expr.getType() == TypeIdentifier.CHAR_TYPE) {
        }
        if (print.expr.getType() == TypeIdentifier.PAIR_LIT_TYPE) {
        }
//        if (print.expr.getType() == TypeIdentifier.ARRAY) {
//        }


        if (print.newLine) {
            AssemblyRepresentation.addMainInstr(BranchInstruction("p_print_ln", Condition.L))
        }
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        // Evaluate the expression you want to return and access the register that holds the value
        visit(ret.expr)
        val dest: Register = ret.expr.getDestReg()

        // Move the value into r0 and pop the program counter
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest))
        AssemblyRepresentation.addMainInstr(PopInstruction(Registers.pc))
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

        // Loop body
        visit(whileStat.body)

        // Label for condition checking
        AssemblyRepresentation.addMainInstr(condLabel)

        // Comparison and jump if equal
        visit(whileStat.loopExpr)
        AssemblyRepresentation.addMainInstr(
            CompareInstruction(
                whileStat.loopExpr.getDestReg(),
                Immediate(1)
            )
        )
        AssemblyRepresentation.addMainInstr(BranchInstruction(bodyLabel.getLabel(), Condition.EQ))
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        val intValue = Integer.parseInt(liter.sign + liter.value)
        val register = Registers.allocate()
        val mode: AddressingMode = AddressingMode.AddressingMode2(register, Immediate(intValue))
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
                AddressingMode.AddressingMode2(register, label.getLabel())
            )
        )
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        val register = Registers.allocate()
        liter.setDestReg(register)
        AssemblyRepresentation.addMainInstr(
            MoveInstruction(
                register,
                AddressingMode.AddressingMode2(register, Immediate(0))
            )
        )
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        // Find the stack address of the identifier (relative to the top of the stack, 0 for us)
        val identObj: VariableIdentifier = ident.st().lookupAll(ident.value) as VariableIdentifier
        val identOffset: Int = identObj.getAddr()

        // Calculate the stack space between the current stack pointer and the identifier
        val sp: Int = ident.st().getStackPtr()
        val spOffset: Int = identOffset - sp

        // Obtain an available register and load stack value into this register
        val register = Registers.allocate()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                register,
                AddressingMode.AddressingMode2(Registers.sp, Immediate(spOffset))
            )
        )
    }

    // TODO: Heap memory
    override fun visitArrayElemAST(arrayElem: ExprAST.ArrayElemAST) {
        TODO("Not yet implemented")
    }

    override fun visitUnOpAST(unop: ExprAST.UnOpAST) {
        when (unop.operator) {
            "-" -> translateNeg(unop)
            // TODO
            else -> {
            }
        }
    }

    private fun translateNeg(unop: ExprAST.UnOpAST) {
        visit(unop.expr)
        val dest: Register = unop.expr.getDestReg()
        AssemblyRepresentation.addMainInstr(
            LoadInstruction(
                dest,
                AddressingMode.AddressingMode2(Registers.sp, null)
            )
        )
        AssemblyRepresentation.addMainInstr(ReverseSubtractInstruction(dest, dest, Immediate(0)))
    }

    override fun visitBinOpAST(binop: ExprAST.BinOpAST) {
        when (binop.operator) {
            "+" -> translateAdd(binop)
            "-" -> translateSub(binop)
            "*" -> translateMultiply(binop)
            "/", "%" -> translateDivMod(binop)
            "&&", "||" -> translateAndOr(binop)
            ">", ">=", "<", "<=" -> translateCompare(binop)
            "==", "!=" -> translateEquality(binop)
            else -> {
            }
        }
    }

    private fun translateAdd(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        when {
            expr1 is ExprAST.IntLiterAST -> {
                visit(expr2)
                val dest: Register = expr2.getDestReg()
                AssemblyRepresentation.addMainInstr(
                    AddInstruction(
                        dest,
                        dest,
                        Immediate(expr1.getValue())
                    )
                )

                binop.setDestReg(dest)
            }
            expr2 is ExprAST.IntLiterAST -> {
                visit(expr1)
                val dest: Register = expr2.getDestReg()
                AssemblyRepresentation.addMainInstr(
                    AddInstruction(
                        dest,
                        dest,
                        Immediate(expr2.getValue())
                    )
                )

                binop.setDestReg(dest)
            }
            else -> {
                visit(expr1)
                visit(expr2)

                val dest1: Register = expr1.getDestReg()
                val dest2: Register = expr2.getDestReg()

                AssemblyRepresentation.addMainInstr(AddInstruction(dest1, dest1, dest2))

                Registers.free(dest2)
                binop.setDestReg(dest1)
            }
        }
    }

    private fun translateSub(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)

        val dest: Register = binop.expr1.getDestReg()

        /* we can only optimise for expr2 being int liter since we have
             * SUB rd, rn, op -> rd = rn - op, so expr1 must always be placed
             * in a register */
        when (binop.expr2) {
            is ExprAST.IntLiterAST -> {
                AssemblyRepresentation.addMainInstr(
                    SubtractInstruction(
                        dest,
                        dest,
                        Immediate(binop.expr2.getValue())
                    )
                )
            }
            else -> {
                visit(binop.expr2)
                val dest2: Register = binop.expr2.getDestReg()

                AssemblyRepresentation.addMainInstr(SubtractInstruction(dest, dest, dest2))
                Registers.free(dest2)
            }
        }

        binop.setDestReg(dest)
    }

    private fun translateMultiply(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        val dest1: Register = binop.expr1.getDestReg()
        val dest2: Register = binop.expr2.getDestReg()

        AssemblyRepresentation.addMainInstr(MultiplyInstruction(dest1, dest1, dest2))
        Registers.free(dest2)

        binop.setDestReg(dest1)
    }

    private fun translateDivMod(binop: ExprAST.BinOpAST) {
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
        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Registers.r3))

        binop.setDestReg(dest)
    }

    private fun translateAndOr(binop: ExprAST.BinOpAST) {
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

    private fun translateCompare(binop: ExprAST.BinOpAST) {
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
            }
        }
    }

    private fun translateEquality(binop: ExprAST.BinOpAST) {
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

    override fun visitBaseTypeAST(type: TypeAST.BaseTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayTypeAST(type: TypeAST.ArrayTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairTypeAST(type: TypeAST.PairTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairElemTypeAST(elemType: TypeAST.PairElemTypeAST) {
        TODO("Not yet implemented")
    }

    override fun visitArrayLiterAST(arrayLiter: ArrayLiterAST) {
        TODO("Not yet implemented")
    }

    override fun visitAssignLHSAST(lhs: AssignLHSAST) {
        TODO("Not yet implemented")
    }

    override fun visitFuncCallAST(funcCall: FuncCallAST) {
        var totalSize = 0
        for (arg in funcCall.args.reversed()) {
            // Generate code to evaluate each argument and obtain the register that has the value
            visit(arg)
            val dest: Register = arg.getDestReg()

            // Store the value obtained below the current stack pointer
            // Decrement the stack pointer to account for this- pre-indexing with auto indexing
            val size = arg.getType().getStackSize()
            AssemblyRepresentation.addMainInstr(
                StoreInstruction(
                    dest,
                    AddressingMode.AddressingMode2(Registers.sp, Immediate(-1 * size))
                )
            )
            AssemblyRepresentation.addMainInstr(
                AddInstruction(
                    Registers.sp,
                    Registers.sp,
                    Immediate(-1 * size)
                )
            )

            // Calculate the total size of stack space allocated for arguments here. Free the register
            totalSize += size
            Registers.free(dest)
        }

        // Obtain the size of stack space required for the local variables of the function being called
        val funcIdent: FunctionIdentifier =
            funcCall.st().lookupAll(funcCall.funcName) as FunctionIdentifier
        val funcStackSize: Int = funcIdent.getStackSize()

        // Store the value for the stack pointer (keeping in mind the arguments and local variables)
        val symTab: SymbolTable = funcIdent.getSymbolTable()
        symTab.setStackPtr(funcCall.st().getStackPtr() - totalSize - funcStackSize)

        // Branch to the function label in the assembly code
        AssemblyRepresentation.addMainInstr(BranchInstruction(funcCall.funcName, Condition.L))

        // Restore the stack pointer
        AssemblyRepresentation.addMainInstr(
            AddInstruction(
                Registers.sp,
                Registers.sp,
                Immediate(totalSize)
            )
        )

        // Move the result of the function into an available register
        val reg: Register = Registers.allocate()
        AssemblyRepresentation.addMainInstr(MoveInstruction(reg, Registers.r0))

        // Set the destination register for future use
        funcCall.setDestReg(reg)
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        TODO("Not yet implemented")
    }
}