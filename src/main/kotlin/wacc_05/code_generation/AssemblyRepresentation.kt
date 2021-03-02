package wacc_05.code_generation

import wacc_05.code_generation.instructions.BranchInstruction
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
    private val pInstrs: HashSet<PInstruction> = HashSet()

    private var hasRuntimeError: Boolean = false

    fun addDataInstr(instr: Instruction) {
        dataInstrs.add(instr)
    }

    fun addMainInstr(instr: Instruction) {
        mainInstrs.add(instr)
    }

    fun addPInstr(p_instr: PInstruction) {
        pInstrs.add(p_instr)
        if (p_instr is PInstruction.p_throw_overflow_error) {
            AssemblyRepresentation.addMainInstr(
                BranchInstruction(p_instr::class.java.simpleName, Condition.LVS)
            )
        }

        AssemblyRepresentation.addMainInstr(
            BranchInstruction(p_instr::class.java.simpleName, Condition.L)
        )
    }

    fun runtimeErr() {
        this.hasRuntimeError = true
    }


    // This function builds the '.s' file with the information stored in fields (after translation)
    fun buildAssembly(file_name: String) {

        File("$file_name.s").printWriter().use { out ->
            val sb: StringBuilder = StringBuilder()
            sb.append("\t.data\n")

            pInstrs.forEach { instr->
                instr.checkRuntimeErr()
            }

            if (hasRuntimeError) {
                pInstrs.add(PInstruction.p_throw_runtime_error())
                pInstrs.add(PInstruction.p_print_string())
            }

            // Add msg_labels to dataInstrs
            pInstrs.forEach {
                it.addMessageLabel()
            }

            dataInstrs.forEach { instr ->
                sb.append(printInstr(instr))
            }

            sb.append("\n\t.text\n")
            sb.append("\n\t.global main\n")

            mainInstrs.forEach { instr ->
                sb.append(printInstr(instr))
            }

            pInstrs.forEach {
                val instructions = it.applyIO()
                instructions.forEach { instr ->
                    sb.append(printInstr(instr))
                }
            }

            out.println(sb.toString())
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
            "\t$instr\n"
        } else {
            "\t\t$instr\n"
        }
    }
}