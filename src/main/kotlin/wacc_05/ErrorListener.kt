package wacc_05

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import kotlin.system.exitProcess

class ErrorListener : BaseErrorListener() {

    // define constants in companion objects
    companion object {
        val SYNTAX_ERROR = 100
    }

    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
        println("Syntax Error (Error 100)\n - Error at Line $line : $charPositionInLine")
        if (msg != null && !msg.isEmpty()) {
            println(msg)
        }
//        exitProcess(SYNTAX_ERROR)
    }


}
