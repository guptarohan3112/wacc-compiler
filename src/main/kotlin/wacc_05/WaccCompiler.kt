package wacc_05

import antlr.WaccLexer
import antlr.WaccParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import wacc_05.ast_structure.AST
import wacc_05.code_generation.AssemblyRepresentation
import wacc_05.code_generation.O1TranslatorVisitor
import wacc_05.code_generation.TranslatorVisitor
import wacc_05.front_end.*
import wacc_05.symbol_table.FunctionST
import wacc_05.symbol_table.SymbolTable
import java.io.File
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    if (args.size <= 1) {
        println("Reason For Error : Please specify the path to a file as the argument of the program")
        exitProcess(ErrorCode.GENERAL_ERROR)
    }

    val filePath: String = args[0]
    val optimisation: Int = args[1].toInt()
    val debug: Boolean = args[2] == "true"
    val validOnly: Boolean = args[3] == "true"

    val ret: Int
    val time = measureTimeMillis {
        ret = WaccCompiler.runCompiler(filePath, optimisation, debug, validOnly)
    }

    if (ret == ErrorCode.SUCCESS) {
        println("Compilation Successful in $time milliseconds")
    } else {
        println("Compilation failed with error in $time milliseconds")
    }

    println("Exited with exit code: $ret")

    exitProcess(ret)
}

object WaccCompiler {

    @JvmStatic
    fun runCompiler(filePath: String, optimisation: Int, debug: Boolean, validOnly: Boolean): Int {
        val inputStream = File(filePath).inputStream()
        val input = CharStreams.fromStream(inputStream)
        val lexer = WaccLexer(input)
        val errorListener = SyntaxErrorListener()

        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)

        val parser = WaccParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val tree = parser.prog()

        if (parser.numberOfSyntaxErrors > 0) {
            errorListener.getSyntaxErrors().forEach(::println)
            return ErrorCode.SYNTAX_ERROR
        }

        if (debug)
            println('\n' + tree.toStringTree(parser) + '\n')

        val visitor = Visitor()
        val ast: AST = visitor.visit(tree)

        val symTab = SymbolTable(null)
        val funcTab = FunctionST()
        SymbolTable.makeTopLevel(symTab)
        val seh = SemanticErrorHandler()

        val semanticChecker = if (optimisation == 1) {
            O1SemanticVisitor(symTab, funcTab, seh)
        } else {
            SemanticVisitor(symTab, funcTab, seh)
        }

        semanticChecker.visit(ast)

        if (seh.hasErrors()) {
            seh.printErrors()
            return ErrorCode.SEMANTIC_ERROR
        }

        if (!validOnly) {
            val representation = AssemblyRepresentation()

            val translatorVisitor = if (optimisation == 1) {
                O1TranslatorVisitor(representation)
            } else {
                TranslatorVisitor(representation)
            }

            translatorVisitor.visit(ast)
            val fileName = File(filePath).nameWithoutExtension
            println("Generating assembly file : $fileName.s")
            representation.buildAssembly(fileName)
            println("Generation of assembly file complete")
        }

        if (debug) {
            println("FINISHED")
        }

        return ErrorCode.SUCCESS
    }
}
