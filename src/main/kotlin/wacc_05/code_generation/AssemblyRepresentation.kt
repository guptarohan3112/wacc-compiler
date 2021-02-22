package wacc_05.code_generation

import wacc_05.code_generation.instructions.Instruction


// This class stores all of the information that we need in order to write to an assembly file
class AssemblyRepresentation() {

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

    // This function builds the '.s' file with the information stored in fields (after translation)
    fun buildAssembly(file_name: String) {

        // Create '.s' file with file_name

        // Writing to the created file
        // 1. .data directive header and info in dataIntrs
        // 2. .text directive header
        // 3. .global main directive header and info in mainInstrs
        // 4. call applyIO to each instruction in ioInstrs

        // Close the file after writing to it
    }




}