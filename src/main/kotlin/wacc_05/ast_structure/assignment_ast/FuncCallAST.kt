package wacc_05.ast_structure.assignment_ast

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.ast_structure.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier

class FuncCallAST(val ctx: WaccParser.FuncCallContext, val funcName: String, val args: ArrayList<ExprAST>) :
    AssignRHSAST() {

    override fun getType(st: SymbolTable): TypeIdentifier {
        return (st.lookupAll(funcName) as FunctionIdentifier).getReturnType()
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        when (val funcIdentifier: IdentifierObject? = st.lookupAll(funcName)) {
            null -> {
                errorHandler.invalidIdentifier(ctx, funcName)
            }
            !is FunctionIdentifier -> {
                errorHandler.invalidFunction(ctx, funcName)
            }
            else -> {
                // Check that the number of args is as expected
                val noOfArgs: Int = funcIdentifier.getParams().size
                if (noOfArgs != args.size) {
                    errorHandler.argNumberError(ctx, funcName, noOfArgs, args.size)
                }

                // Check that arg type match up with corresponding parameter type
                for (i in 0 until args.size.coerceAtMost(noOfArgs)) {
                    args[i].check(st, errorHandler)
                    val expectedType: TypeIdentifier = funcIdentifier.getParams()[i].getType()
                    val actualType: TypeIdentifier = args[i].getType(st)
                    if (expectedType != actualType) {
                        errorHandler.typeMismatch(ctx, expectedType, actualType)
                    }
                }
            }
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFuncCallAST(this)
    }

}