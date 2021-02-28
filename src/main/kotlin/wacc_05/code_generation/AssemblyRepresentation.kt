package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction
import wacc_05.code_generation.instructions.LabelInstruction
import java.io.File


// This class stores all of the information that we need in order to write to an assembly file
object AssemblyRepresentation {

    // Global variables
    private val dataInstrs: ArrayList<Instruction> = ArrayList()

    // Instructions in the program, including those in user defined functions and the main function
    private val mainInstrs: ArrayList<Instruction> = ArrayList()

    // IO functions that are called in the user defined program
    private val ioInstrs: HashSet<IOInstruction> = HashSet()

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
            out.println("\t.data\n")
            dataInstrs.forEach { instr->
                out.println(printInstr(instr))
            }

            out.println("\n\t.text\n")

            out.println("\t.global main")
//            out.println("\tmain:")

            mainInstrs.forEach { instr->
                out.println(printInstr(instr))
            }

            out.println()

            ioInstrs.forEach {
                val instructions = it.applyIO()
                instructions.forEach { instr ->
                    out.println(printInstr(instr))
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

    private fun printInstr(instr: Instruction): String {
        return if (instr is LabelInstruction) {
            "\t$instr"
        } else {
            "\t\t$instr"
        }
    }
}