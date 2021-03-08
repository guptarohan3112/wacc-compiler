package wacc_05.front_end.error_tests

import org.junit.Test

import wacc_05.WaccCompiler
import wacc_05.front_end.ErrorCode
import wacc_05.symbol_table.FunctionST
import java.io.File
import kotlin.test.assertTrue

class ErrorTests {

    @Test
    fun runValidTests() {
        val testPassed: Boolean = runTestsInDir("src/test/test_cases/valid", ErrorCode.SUCCESS)
        assertTrue(testPassed, "Failed Valid Program Checker Tests")
    }

    @Test
    fun runInvalidSemanticsTests() {
        val testPassed: Boolean = runTestsInDir(
            "src/test/test_cases/invalid/semanticErr",
            ErrorCode.SEMANTIC_ERROR
        )
        assertTrue(testPassed, "Failed Invalid Semantic Checker Tests")
    }

    /* NOTE: we do not check syntax error tests since these call exit process directly and would
     * terminate the entire testsuite.
     */

    private fun runTestsInDir(directoryPath: String, expected: Int): Boolean {
        val passedTests: ArrayList<String> = ArrayList()
        val failedTests: ArrayList<String> = ArrayList()
        File(directoryPath).walk().forEach {
            if (it.extension == "wacc") {
                try {
                    if (WaccCompiler.runCompiler(it.absolutePath, debug=false, validOnly=true) == expected)
                        passedTests.add(it.nameWithoutExtension)
                    else
                        failedTests.add(it.nameWithoutExtension)
                } catch (e: Exception) {
                    failedTests.add(it.nameWithoutExtension)
                }
            }
        }

        println("---------------------------------------------")
        println(passedTests.size.toString() + " tests passed.")

        println(failedTests.size.toString() + " tests failed.")
        println("---------------------------------------------")

        // Show the tests that return the wrong exit code (to what was expected)
        println(failedTests.toString())

        return failedTests.size == 0
    }
}