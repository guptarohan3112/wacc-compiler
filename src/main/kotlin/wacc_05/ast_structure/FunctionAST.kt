package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.SemanticErrors
import wacc_05.code_generation.Registers
import wacc_05.code_generation.instructions.Instruction
import wacc_05.front_end.ASTVisitor
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

class FunctionAST(
    val ctx: WaccParser.FuncContext,
    val returnType: TypeAST,
    val funcName: String,
    val paramList: ParamListAST?,
    val body: StatementAST
) : AST() {

    fun preliminaryCheck(st: SymbolTable, errorHandler: SemanticErrors) {
        returnType.check(st, errorHandler)

        // Check to make sure function has not already been defined
        val func: IdentifierObject? = st.lookup(funcName)
        if (func != null && func is FunctionIdentifier) {
            errorHandler.repeatVariableDeclaration(ctx, funcName)
            return
        }

        // Create function identifier and add to symbol table
        val funcST = SymbolTable(st)
        this.st = funcST
        val returnTypeIdent: TypeIdentifier = returnType.getType(st)

        val funcIdent =
            FunctionIdentifier(returnTypeIdent, paramList?.getParams(st) ?: ArrayList(), funcST)

        funcST.add("returnType", returnTypeIdent)

        // add self to higher level symbol table
        st.add(funcName, funcIdent)
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        val funcIdentifier = st.lookupAll(funcName)

        // we don't give another semantic error if this is not null as it will be a semantic error
        // caused by the inner specifics of the compiler
        if (funcIdentifier != null) {
            // Check parameter list and function body
            paramList?.check(this.st!!, errorHandler)
            body.check(this.st!!, errorHandler)
        }
    }

    override fun translate(regs: Registers): ArrayList<Instruction> {
        return ArrayList()
    }

    override fun <T> accept(visitor: ASTVisitor<T>): T {
        return visitor.visitFunctionAST(this)
    }
}
