package wacc_05.back_end_tests.execution_tests

import org.junit.Test

import wacc_05.WaccCompiler
import java.io.File
import kotlin.test.assertTrue

class ExecutionTests {

    // Need to think about reads and other possible cases
    @Test
    fun runValidTests() {
        val testPassed: Boolean = runTestsInDir("src/test/test_cases/valid")
        assertTrue(testPassed, "Failed Valid Program Checker Tests")
    }

    private fun runTestsInDir(directoryPath: String): Boolean {
        val passedTests: ArrayList<String> = ArrayList()
        val failedTests: ArrayList<String> = ArrayList()

        File(directoryPath).walk().forEach {
            if (it.extension == "wacc") {
                try {
                    // Run the compiler- this should generate the assembly file (to be executed)
                    WaccCompiler.runCompiler(it.absolutePath, false)
                    val assembly = File(it.nameWithoutExtension+".s")
                    if (!assembly.exists()) {
                        // the file cannot be found, some sort of error, add to failedTests
                        println("Assembly file ${it.nameWithoutExtension}.s was not created")
                        failedTests.add(it.nameWithoutExtension)
                    } else {
                        // The file has been found- the output and exit code must be compared to what is indicated in the comments of the wacc file
                        Runtime.getRuntime().exec("./src/test/test_cases/refCompile -p "+it.absolutePath)

                        // Calls a function to parse the wacc file to get Exit(default 0) and Output
                        // Compare exit of compiling (similar as before) and output (pipe output to temporary file output.txt)

                    }

                } catch (e: Exception) {
                    failedTests.add(it.nameWithoutExtension)
                }
            }
        }

        println("---------------------------------------------")
        println(passedTests.size.toString() + " tests passed.")
        println(failedTests.size.toString() + " tests failed.")
        println("---------------------------------------------")

        // Show the tests that return the wrong output from execution
        println(failedTests.toString())

        // Delete the output.txt file

        return failedTests.size == 0
    }

}