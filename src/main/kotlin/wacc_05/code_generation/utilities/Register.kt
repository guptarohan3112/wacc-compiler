package wacc_05.code_generation.utilities

class Register(private val number: Int) : Operand(), Comparable<Register> {

    private var hasBeenPushed: Boolean = false
    private var inUse: Boolean = false

    override fun toString(): String {
        return when (number) {
            13 -> "sp"
            14 -> "lr"
            15 -> "pc"
            else -> "r$number"
        }
    }

    fun pushedNow() {
        this.hasBeenPushed = true
    }

    fun poppedNow() {
        this.hasBeenPushed = false
    }

    fun hasBeenPushed(): Boolean {
        return hasBeenPushed
    }

    fun inUse(): Boolean {
        return inUse()
    }

    fun occupiedNow() {
        this.inUse = true
    }

    fun freedNow() {
        this.inUse = false
    }

    override fun compareTo(other: Register): Int {
        return number - other.number
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Register

        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        return number
    }

}