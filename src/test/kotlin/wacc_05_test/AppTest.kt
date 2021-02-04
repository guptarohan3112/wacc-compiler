/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package wacc_05_test

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import wacc_05.App
import java.io.File

fun main(args:Array<String>) {
    AppTest.runValidTests()
    AppTest.runInvalidSyntaxTests()
    AppTest.runInvalidSemanticsTests()
}

object AppTest {

    @Test
    fun runValidTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\valid\\", "Valid")
        assertTrue(testPassed)
    }

    @Test
    fun runInvalidSyntaxTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\invalid\\syntaxErr\\", "Invalid Syntax")
        assertTrue(testPassed)
    }

    @Test
    fun runInvalidSemanticsTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\invalid\\semanticErr\\", "Invalid Semantic")
        assertTrue(testPassed)
    }

    fun runTestsInDir(directoryPath: String, type: String): Boolean {
        val passedTests: ArrayList<String> = ArrayList<String>()
        val failedTests: ArrayList<String> = ArrayList<String>()

        File(directoryPath).walk().forEach {
            if (it.extension == "wacc")
                if (App.runCompiler(it.absolutePath) == 0)
                    passedTests.add(it.nameWithoutExtension)
                else
                    failedTests.add(it.nameWithoutExtension)
        }

        println("---------------------------------------------")
        if (failedTests.size > 0) {
//            passedTests.forEach {
//                println(it + " failed.")
//            }
            println(passedTests.size.toString() + " tests passed.")
            println(failedTests.size.toString() + " tests failed.")
        }
        else
            println("$type tests PASSED!")
        println("---------------------------------------------")

        return failedTests.size == 0

    }

}

