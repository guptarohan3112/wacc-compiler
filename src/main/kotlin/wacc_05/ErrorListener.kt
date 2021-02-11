package wacc_05

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

class ErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        println("Syntax Error (Error 100)\n - Error at Line $line : $charPositionInLine")
        if (msg != null && msg.isNotEmpty()) {
            println(msg)
        }
    }


}
