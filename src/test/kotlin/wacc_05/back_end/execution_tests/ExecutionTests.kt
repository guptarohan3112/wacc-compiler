package wacc_05.back_end.execution_tests

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import wacc_05.WaccCompiler
import wacc_05.ast_structure.ExprAST
import wacc_05.code_generation.utilities.Registers
import wacc_05.code_generation.instructions.LabelInstruction
import wacc_05.code_generation.instructions.MessageLabelInstruction
import java.io.File
import java.io.InputStream
import kotlin.test.assertTrue

@RunWith(value = Parameterized::class)
class ExecutionTests(
    private val file: File
) {

    companion object {
        private val DIRECTORY_PATH = "src/test/test_cases/valid/array"

        // we have to ignore these tests as our test program cannot run command line inputs
        private val READ_TESTS = hashSetOf(
            "rmStyleAddIO", "fibonacciFullIt", "echoBigInt", "echoBigNegInt", "echoChar", "echoInt",
            "echoNegInt", "echoPuncChar", "read", "IOLoop", "IOSequence", "printInputTriangle",
            "fibonacciFullRec", "readPair"
        )

        // tests whose output we cannot read from their file due to it being absent and/or of incorrect format
        private val IGNORE_TESTS = hashSetOf("fixedPointRealArithmetic", "print-carridge-return", "print-backspace")

        // tests that we now fail due to optimisations within the compiler
        private val OUTDATED_TESTS = hashSetOf("divZero")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<File> {
            val files: ArrayList<File> = ArrayList()
            val testPath: File = File(DIRECTORY_PATH)
            for (file in testPath.walk()) {
                if (file.extension == "wacc") {
                    files.add(file)
                }
            }
            return files
        }
    }

    @Test
    fun runValidTests() {
        val it: File = file
        val testPassed: Boolean = runTestsInDir(it)
        Registers.freeAll()
        LabelInstruction.reset()
        MessageLabelInstruction.reset()

        Thread.sleep(300)
        assertTrue(testPassed, "Failed Valid Program Checker Tests")
    }

    @Synchronized
    private fun runTestsInDir(it: File): Boolean {
        var passed = false

        if (it.extension == "wacc") {
            if (READ_TESTS.contains(it.nameWithoutExtension)
                || IGNORE_TESTS.contains(it.nameWithoutExtension)
                || OUTDATED_TESTS.contains(it.nameWithoutExtension)
            ) {
                // skip
                return true
            }

            try {
                println(it.absolutePath)
                // Run the compiler- this should generate the assembly file (to be executed)
                WaccCompiler.runCompiler(it.absolutePath, 0, debug = false, validOnly = false)


                val assemblyName = it.nameWithoutExtension + ".s"
                val assembly = File(assemblyName)
                if (!assembly.exists()) {
                    // The file cannot be found, some sort of error, add to failedTests
                    println("Assembly file $assemblyName was not created")
                    passed = false
                } else {
                    // The output and exit code must be compared to the particular comments of the wacc file
                    val prog: String = it.nameWithoutExtension
                    // Compile executable file, run it on the emulator and capture program output
                    Runtime.getRuntime()
                        .exec("arm-linux-gnueabi-gcc -o $prog -mcpu=arm1176jzf-s -mtune=arm1176jz-s $assemblyName")
                        .waitFor()
                    val emulate: String = "qemu-arm -L /usr/arm-linux-gnueabi/ $prog"
                    val run = Runtime.getRuntime().exec(emulate)
                    val buf: InputStream = run.inputStream
                    var progOutput = buf.bufferedReader().use { it.readText() }
                    run.waitFor()
                    val progExit = run.exitValue()

                    val (assemblyOutput, assemblyExit) = getExpectedOutput(File(it.absolutePath))
                    if (progOutput.isNotEmpty()) {
                        progOutput = progOutput.substring(0, progOutput.length - 1)
                    }
                    Runtime.getRuntime().exec("rm $prog").waitFor()
                    Runtime.getRuntime().exec("rm $assemblyName").waitFor()
                    if (assemblyOutput.contains("#addrs#") || assemblyOutput.contains("#runtime_error#")) {
                        return true
                    }
                    passed = if (progOutput == assemblyOutput && progExit == assemblyExit) {
                        true
                    } else {
                        if (progOutput != assemblyOutput) {
                            println("ERROR IN EXECUTING $assemblyName: output is not as expected")
                            println("program $prog output: *$progOutput*")
                            println("Expected output: *$assemblyOutput*")

                        } else {
                            println("ERROR IN EXECUTING $assemblyName: exit code is $progExit when it should be $assemblyExit")
                        }
                        false
                    }
                }

            } catch (e: Exception) {
                println("Test Exception")
                println(e.message)
                return false
            }
        }

        return passed
    }


    private fun getExpectedOutput(assembly: File): Pair<String, Int> {
        val inputStream: InputStream = assembly.inputStream()
        val lines = mutableListOf<String>()
        val outputLines = mutableListOf<String>()
        var outputFlag = false
        var exitFlag = false
        var exitCode = 0

        inputStream.bufferedReader().forEachLine { lines.add(it) }
        lines.forEach {
            if (it.isNotEmpty() && it[0] == '#') {
                val line: String = it.substring(2)
                if (line == "Output:") {
                    outputFlag = true
                } else if (outputFlag) {
                    if (line != "#empty#") {
                        outputLines.add(line)
                    }
                }
                if (line == "Exit:") {
                    exitFlag = true
                } else if (exitFlag) {
                    exitCode = line.removeSuffix(" ").toInt()
                }
            } else {
                outputFlag = false
                exitFlag = false
            }

        }
        return Pair(outputLines.joinToString(separator = "\n"), exitCode)
    }
}
