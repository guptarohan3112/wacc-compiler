package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction
import java.io.File


// This class stores all of the information that we need in order to write to an assembly file
object AssemblyRepresentation {

    // Global variables
    private val dataInstrs: ArrayList<Instruction>
    // Instructions in the program, including those in user defined functions and the main function
    private val mainInstrs: ArrayList<Instruction>
    // IO functions that are called in the user defined program
    private val ioInstrs: HashSet<IOInstruction>

    init {
        dataInstrs = ArrayList()
        mainInstrs = ArrayList()
        ioInstrs = HashSet()
    }

    fun addDataInstr(instr: Instruction) {
        dataInstrs.add(instr)
    }

    fun addMainInstr(instr: Instruction) {
        mainInstrs.add(instr)
    }

    fun addIOInstr(io_instr: IOInstruction) {
        ioInstrs.add(io_instr)
    }



    // This function builds the '.s' file with the information stored in fields (after translation)
    fun buildAssembly(file_name: String) {

        File("$file_name.s").printWriter().use { out ->
            out.println("\t.data")
            dataInstrs.forEach {
                out.println("$it")
            }

            out.println()
            out.println("\t.text")
            out.println()

            out.println("\t.global main")
            out.println("\tmain:")

            mainInstrs.forEach {
                out.println("\t\t$it")
            }

            out.println()

            ioInstrs.forEach {
                val instructions = it.applyIO()
                val label = it.getLabel()
                out.println("\t$label")
                instructions.forEach { instr ->
                    out.println("\t\t$instr")
                }
            }
        }

        // Create '.s' file with file_name

        // Writing to the created file
        // 1. .data directive header and info in dataIntrs
        // 2. .text directive header
        // 3. .global main directive header and info in mainInstrs
        // 4. call applyIO to each instruction in ioInstrs

        // Close the file after writing to it
    }

}