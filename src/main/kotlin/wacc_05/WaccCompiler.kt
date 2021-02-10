package wacc_05

import antlr.WaccLexer
import antlr.WaccParser

import org.antlr.v4.runtime.*
import wacc_05.ast_structure.AST
import wacc_05.symbol_table.SymbolTable
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Reason For Error : Please specify the path to a file as the argument of the program")
        exitProcess(-1)
    }
    val ret = WaccCompiler.runCompiler(args[0])
    exitProcess(ret)
}

object WaccCompiler {

    @JvmStatic
    fun runCompiler(filePath: String): Int {
//        println("Welcome to WACC: Please enter your input below")
//        println("Remember to click enter at the end of input in this temporary solution")
        val inputStream = File(filePath).inputStream()
        val input = CharStreams.fromStream(inputStream)
        val lexer = WaccLexer(input)
        val errorListener = ErrorListener()

        lexer.removeErrorListeners()   // remove default ConsoleErrorListener
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)

        val parser = WaccParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val tree = parser.prog()

        if (parser.numberOfSyntaxErrors > 0) {
            return Error.SYNTAX_ERROR
        }

        println('\n' + tree.toStringTree(parser) + '\n')

        val visitor = Visitor()
        val ast: AST = visitor.visit(tree)

        val seh = SemanticErrorHandler()
        semanticErrorCheck(ast, SymbolTable(null), seh)

        println("FINISHED")
        return seh.err
    }

    private fun semanticErrorCheck(ast: AST, st: SymbolTable, errorHandler: SemanticErrors) {
        SymbolTable.makeTopLevel(st)
        // semantic checks
        ast.check(st, errorHandler)
    }

}
