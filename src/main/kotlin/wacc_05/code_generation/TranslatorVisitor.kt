package wacc_05.code_generation

import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*
import wacc_05.ast_structure.ASTVisitor
import wacc_05.code_generation.instructions.*
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.code_generation.instructions.LabelInstruction.Companion.getUniqueLabel
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class TranslatorVisitor : ASTVisitor<Unit> {
    override fun visitProgramAST(prog: ProgramAST) {
        TODO("Not yet implemented")
    }

    override fun visitFunctionAST(func: FunctionAST) {
        TODO("Not yet implemented")
    }

    override fun visitParamListAST(list: ParamListAST) {
        TODO("Not yet implemented")
    }

    override fun visitParamAST(param: ParamAST) {
        TODO("Not yet implemented")
    }

    override fun visitSkipAST(skip: StatementAST.SkipAST) {
        return
    }

    override fun visitDeclAST(decl: StatementAST.DeclAST) {
        TODO("Not yet implemented")
    }

    override fun visitAssignAST(assign: StatementAST.AssignAST) {
        TODO("Not yet implemented")
    }

    override fun visitBeginAST(begin: StatementAST.BeginAST) {
        TODO("Not yet implemented")
    }

    override fun visitReadAST(read: StatementAST.ReadAST) {
        TODO("Not yet implemented")
    }

    override fun visitExitAST(exit: StatementAST.ExitAST) {
        TODO("Not yet implemented")
    }

    override fun visitFreeAST(free: StatementAST.FreeAST) {
        TODO("Not yet implemented")
    }

    override fun visitIfAST(ifStat: StatementAST.IfAST) {
        // Evaluation of the condition expression
        visit(ifStat.condExpr)

        // Condition checking
        val destination: Register = ifStat.condExpr.dest!!
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

    override fun visitPrintAST(print: StatementAST.PrintAST) {
        TODO("Not yet implemented")
    }

    override fun visitReturnAST(ret: StatementAST.ReturnAST) {
        TODO("Not yet implemented")
    }

    override fun visitSequentialAST(seq: StatementAST.SequentialAST) {
        visit(seq.stat1)
        visit(seq.stat2)
    }

    override fun visitWhileAST(whileStat: StatementAST.WhileAST) {
        // Label for loop body
        val bodyLabel: LabelInstruction = getUniqueLabel()
        AssemblyRepresentation.addMainInstr(bodyLabel)

        // Loop body
        visit(whileStat.body)

        // Label for condition checking
        val condLabel: LabelInstruction = getUniqueLabel()

        // Comparison and jump if equal
        visit(whileStat.loopExpr)
        AssemblyRepresentation.addMainInstr(CompareInstruction(whileStat.loopExpr.dest!!, Immediate(1)))
        AssemblyRepresentation.addMainInstr(BranchInstruction(bodyLabel.getLabel(), Condition.EQ))
    }

    override fun visitIntLiterAST(liter: ExprAST.IntLiterAST) {
        val intValue = Integer.parseInt(liter.sign + liter.value)
        val register = Registers.allocate()
        val mode: AddressingMode = AddressingMode.AddressingMode2(register, Immediate(intValue))
        liter.dest = register
        AssemblyRepresentation.addMainInstr(LoadInstruction(register, mode))
    }

    override fun visitBoolLiterAST(liter: ExprAST.BoolLiterAST) {
        val intValue = if (liter.value == "true") 1 else 0
        val register = Registers.allocate()
        liter.dest = register
        AssemblyRepresentation.addMainInstr(MoveInstruction(register, Immediate(intValue)))
    }

    override fun visitCharLiterAST(liter: ExprAST.CharLiterAST) {
        val register = Registers.allocate()
        liter.dest = register
        if (liter.value == "'\\0'") {
            AssemblyRepresentation.addMainInstr(MoveInstruction(register, Immediate(0)))
        } else {
            AssemblyRepresentation.addMainInstr(MoveInstruction(register, ImmediateChar(liter.value)))
        }
    }

    override fun visitStrLiterAST(liter: ExprAST.StrLiterAST) {
        val register = Registers.allocate()
        liter.dest = register
        val label = MessageLabelInstruction.getUniqueLabel(liter.value)
        AssemblyRepresentation.addDataInstr(label)
        AssemblyRepresentation.addMainInstr(LoadInstruction(register, AddressingMode.AddressingMode2(register, label.getLabel())))
    }

    override fun visitPairLiterAST(liter: ExprAST.PairLiterAST) {
        val register = Registers.allocate()
        liter.dest = register
        AssemblyRepresentation.addMainInstr(MoveInstruction(register, AddressingMode.AddressingMode2(register, Immediate(0))))
    }

    override fun visitIdentAST(ident: ExprAST.IdentAST) {
        val register = Registers.allocate()
        // offset relative to the top of the stack
        val identObj: VariableIdentifier = ident.st?.lookupAll(ident.value) as VariableIdentifier
        val identOffset: Int = identObj.getAddr()
        val sp: Int = ident.st!!.getStackPtr()
        val spOffset: Int = identOffset - sp
        AssemblyRepresentation.addMainInstr(LoadInstruction(register, AddressingMode.AddressingMode2(Registers.sp, Immediate(spOffset))))
    }

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
        val dest: Register = unop.expr.dest!!
        AssemblyRepresentation.addMainInstr(LoadInstruction(dest, AddressingMode.AddressingMode2(Registers.sp, null)))
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
                val dest: Register = expr2.dest!!
                AssemblyRepresentation.addMainInstr(AddInstruction(dest, dest, Immediate(expr1.getValue())))

                binop.dest = dest
            }
            expr2 is ExprAST.IntLiterAST -> {
                visit(expr1)
                val dest: Register = expr2.dest!!
                AssemblyRepresentation.addMainInstr(AddInstruction(dest, dest, Immediate(expr2.getValue())))

                binop.dest = dest
            }
            else -> {
                visit(expr1)
                visit(expr2)

                val dest1: Register = expr1.dest!!
                val dest2: Register = expr2.dest!!

                AssemblyRepresentation.addMainInstr(AddInstruction(dest1, dest1, dest2))

                Registers.free(dest2)
                binop.dest = dest1
            }
        }
    }

    private fun translateSub(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)

        val dest: Register = binop.expr1.dest!!

        /* we can only optimise for expr2 being int liter since we have
             * SUB rd, rn, op -> rd = rn - op, so expr1 must always be placed
             * in a register */
        when (binop.expr2) {
            is ExprAST.IntLiterAST -> {
                AssemblyRepresentation.addMainInstr(SubtractInstruction(dest, dest, Immediate(binop.expr2.getValue())))
            }
            else -> {
                visit(binop.expr2)
                val dest2: Register = binop.expr2.dest!!

                AssemblyRepresentation.addMainInstr(SubtractInstruction(dest, dest, dest2))
                Registers.free(dest2)
            }
        }

        binop.dest = dest
    }

    private fun translateMultiply(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        val dest1: Register = binop.expr1.dest!!
        val dest2: Register = binop.expr2.dest!!

        AssemblyRepresentation.addMainInstr(MultiplyInstruction(dest1, dest1, dest2))
        Registers.free(dest2)

        binop.dest = dest1
    }

    private fun translateDivMod(binop: ExprAST.BinOpAST) {
        visit(binop.expr1)
        visit(binop.expr2)

        // get result from expr1 and move into param register 1
        val dest1: Register = binop.expr1.dest!!
        AssemblyRepresentation.addMainInstr(MoveInstruction(Registers.r0, dest1))
        Registers.free(dest1)

        // get result from expr2 and mvoe into param register 2
        val dest2: Register = binop.expr2.dest!!
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

        binop.dest = dest
    }

    private fun translateAndOr(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        when {
            expr1 is ExprAST.BoolLiterAST -> {
                visit(expr2)
                val dest: Register = expr2.dest!!

                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest, dest, Immediate(expr1.getValue()))
                    } else {
                        OrInstruction(dest, dest, Immediate(expr1.getValue()))
                    }
                )

                binop.dest = dest
            }

            expr2 is ExprAST.BoolLiterAST -> {
                visit(expr1)
                val dest: Register = expr1.dest!!
                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest, dest, Immediate(expr2.getValue()))
                    } else {
                        OrInstruction(dest, dest, Immediate(expr2.getValue()))
                    }
                )

                binop.dest = dest
            }

            else -> {
                visit(expr1)
                visit(expr2)

                val dest1: Register = expr1.dest!!
                val dest2: Register = expr2.dest!!

                AssemblyRepresentation.addMainInstr(
                    if (binop.operator == "&&") {
                        AndInstruction(dest1, dest1, dest2)
                    } else {
                        OrInstruction(dest1, dest1, dest2)
                    }
                )

                Registers.free(dest2)

                binop.dest = dest1
            }
        }
    }

    private fun translateCompare(binop: ExprAST.BinOpAST) {
        val expr1 = binop.expr1
        val expr2 = binop.expr2

        when (expr1.getType()) {
            is TypeIdentifier.IntIdentifier -> {
                visit(expr1)
                val dest: Register = expr1.dest!!

                when (expr2) {
                    is ExprAST.IntLiterAST -> {
                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, Immediate(expr2.getValue())))
                    }

                    else -> {
                        visit(expr2)
                        val dest2: Register = expr2.dest!!
                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, dest2))
                        Registers.free(dest2)
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.GT))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.LE))
                    }
                    ">=" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.GE))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.LT))
                    }
                    "<" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.LT))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.GE))
                    }
                    "<=" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.LE))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.GT))
                    }
                }

                binop.dest = dest
            }

            is TypeIdentifier.CharIdentifier -> {
                visit(expr1)
                val dest: Register = expr1.dest!!

                when (expr2) {
                    is ExprAST.CharLiterAST -> {
                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, ImmediateChar(expr2.value)))
                    }

                    else -> {
                        visit(expr2)
                        val dest2: Register = expr2.dest!!

                        AssemblyRepresentation.addMainInstr(CompareInstruction(dest, dest2))
                        Registers.free(dest2)
                    }
                }

                when (binop.operator) {
                    ">" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.HI))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.LS))
                    }
                    ">=" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.HS))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.LO))
                    }
                    "<" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.LO))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.HS))
                    }
                    "<=" -> {
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(1), Condition.LS))
                        AssemblyRepresentation.addMainInstr(MoveInstruction(dest, Immediate(0), Condition.HI))
                    }
                }
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun visitNewPairAST(newPair: NewPairAST) {
        TODO("Not yet implemented")
    }

    override fun visitPairElemAST(pairElem: PairElemAST) {
        TODO("Not yet implemented")
    }
}