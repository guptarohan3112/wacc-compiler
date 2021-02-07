package wacc_05
import antlr.WaccLexer
import antlr.WaccParser

import org.antlr.v4.runtime.*
import kotlin.system.exitProcess

fun main() {
    App.runCompiler()
}

object App {

    @JvmStatic
    fun runCompiler() {

        println("Welcome to WACC: Please enter your input below")
        println("Remember to click enter at the end of input in this temporary solution")
        val inputStream = System.`in`
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
        println(tree.toStringTree(parser))

        val visitor = Visitor()
        visitor.visit(tree)
        println("FINISHED")
    }

}
