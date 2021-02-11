package wacc_05.error_tests

import org.junit.Test

import wacc_05.WaccCompiler
import java.io.File
import kotlin.test.assertTrue

class ErrorTests {

    @Test
    fun runValidTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\TestCases\\valid\\", "Valid", wacc_05.Error.SUCCESS)
        assertTrue(testPassed, "Failed Valid Program Checker Tests")
    }

    @Test
    fun runInvalidSyntaxTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\TestCases\\invalid\\syntaxErr\\", "Invalid Syntax", wacc_05.Error.SYNTAX_ERROR)
        assertTrue(testPassed, "Failed Invalid Syntax Checker Tests")
    }

    @Test
    fun runInvalidSemanticsTests() {
        val testPassed: Boolean = runTestsInDir("src\\test\\test_cases\\invalid\\semanticErr\\", "Invalid Semantic", wacc_05.Error.SEMANTIC_ERROR)
        assertTrue(testPassed, "Failed Invalid Semantic Checker Tests")
    }

    fun runTestsInDir(directoryPath: String, type: String, expected: Int): Boolean {
        val passedTests: ArrayList<String> = ArrayList<String>()
        val failedTests: ArrayList<String> = ArrayList<String>()

        File(directoryPath).walk().forEach {
            if (it.extension == "wacc") {
                if (WaccCompiler.runCompiler(it.absolutePath) == expected)
                    passedTests.add(it.nameWithoutExtension)
                else
                    failedTests.add(it.nameWithoutExtension)
            }
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
        if (expected == 100) {
            println(failedTests.toString())
        }
        return failedTests.size == 0

    }

}