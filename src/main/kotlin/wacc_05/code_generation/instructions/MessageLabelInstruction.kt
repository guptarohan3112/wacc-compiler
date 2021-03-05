package wacc_05.code_generation.instructions

import java.awt.Label

class MessageLabelInstruction(private val name: String, private val string: String) : LabelInstruction(name) {
    companion object {
        private var currentLabel: Int = 0

        public fun getUniqueLabel(string: String): MessageLabelInstruction {
            return MessageLabelInstruction("msg_${currentLabel++}", string)
        }

        fun reset() {
            currentLabel = 0
        }
    }

    private var length: Int = 0

    init {
        length = string.length
        val count = string.count{"\\".contains(it)}
        length -= count
    }

    override fun toString(): String {
        return "$name:\n\t\t.word $length\n\t\t.ascii \"$string\""
    }

}