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
import java.io.InputStream
import java.lang.StringBuilder
import java.nio.file.Paths
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
        val file = File(filePath)
        val inputStream = file.inputStream()
        val dir = file.parentFile.path
        val waccString: String = addImports(inputStream, dir)
        val input = CharStreams.fromString(waccString)
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

        println(ast)

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
            val fileName = file.nameWithoutExtension
            println("Generating assembly file : $fileName.s")
            representation.buildAssembly(fileName)
            println("Generation of assembly file complete")
        }

        if (debug) {
            println("FINISHED")
        }

        return ErrorCode.SUCCESS
    }


    private fun addImports(inputStream: InputStream, dir: String): String {

        // Find all imports inside file
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().forEachLine { lineList.add(it) }
        var lineNum = 0
        val imports: ArrayList<String> = ArrayList()
        val outputString = StringBuilder()

        while (lineNum < lineList.size) {
            val line = lineList[lineNum].trimStart()
            val words = line.split(" ")

            if (words.size >= 2 && words[0] == "import") {
                // Duplicate imports
                if (imports.contains(words[1])) {
                    println("Syntax Error 100:\n Duplicate import Of file ${words[1]}.h" +
                                " on line $lineNum")
                    exitProcess(ErrorCode.SYNTAX_ERROR)
                }
                imports.add(words[1])
            } else {
                outputString.append("$line\n")
            }
            lineNum += 1
        }

        // split parent file around main "begin" and add imported code in between
        val split = outputString.split("begin", limit = 2)
        var header = split[0] + "\nbegin\n"

        for (import in imports) {
            val path: String = Paths.get(dir, "$import.h").toString()
            val file = File(path)
            val importInputStream = file.inputStream()
            val inputString = importInputStream.bufferedReader().use { it.readText() }
            header += inputString
        }
        val waccString = header + split[1]

        var chainedImport = false
        val afterLines = waccString.split("\n").toTypedArray()
        for (line in afterLines) {
            if (line.trimStart().split(" ")[0] == "import") {
                chainedImport = true
            }
        }

        return if (!chainedImport) waccString else addImports(waccString.byteInputStream(), dir)
    }
}
