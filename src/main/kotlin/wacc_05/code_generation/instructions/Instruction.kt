package wacc_05.code_generation.instructions

/* instructions: add, subtract, multiply, AND, OR, compare, branch, (NOT(?)),
                    move, load, store, push, pop, define (foo:) */

interface Instruction {
    override fun toString(): String
}