package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    private val ctx: WaccParser.FuncContext,
    private val returnType: TypeAST,
    private val funcName: String,
    private val paramList: ParamListAST?,
    private val body: StatementAST
) : AST {

    fun preliminaryCheck(st: SymbolTable, errorHandler: SemanticErrors) {
        returnType.check(st, errorHandler)

        // Check to make sure function has not already been defined
        val func: IdentifierObject? = st.lookup(funcName)
        if (func != null && func is FunctionIdentifier) {
            errorHandler.repeatVariableDeclaration(ctx, funcName)
        } else {
            // Create function identifier and add to symbol table
            val funcST = SymbolTable(st)
            val returnTypeIdent: TypeIdentifier = returnType.getType(st)

            // create the param identifier array list
            val params: ArrayList<ParamIdentifier> = paramList?.getParams(st) ?: ArrayList()

            val funcIdent =
                FunctionIdentifier(returnTypeIdent, params, funcST)

            // Check parameter list and function body
            paramList?.check(funcST, errorHandler)

            funcST.add("returnType", returnTypeIdent)

            // Check parameter list and function body
            paramList?.check(funcST, errorHandler)

            // add self to higher level symbol table
            st.add(funcName, funcIdent)
        }
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        val funcIdentifier = st.lookupAll(funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            body.check((funcIdentifier as FunctionIdentifier).getSymbolTable(), errorHandler)
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }
}
