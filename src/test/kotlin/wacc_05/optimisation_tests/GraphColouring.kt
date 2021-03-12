package wacc_05.optimisation_tests

import antlr.WaccLexer
import antlr.WaccParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import wacc_05.Visitor
import wacc_05.ast_structure.AST
import wacc_05.code_generation.utilities.Register
import wacc_05.front_end.*
import wacc_05.graph_colouring.GraphFormationVisitor
import wacc_05.graph_colouring.InterferenceGraph
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphColouring {

    val graph: InterferenceGraph = InterferenceGraph()

    fun runTest(filePath: String) {
        val inputStream = File(filePath).inputStream()
        val input = CharStreams.fromStream(inputStream)
        val lexer = WaccLexer(input)
        val errorListener = SyntaxErrorListener()

        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)

        val parser = WaccParser(tokens)
        parser.removeErrorListeners()

        val tree = parser.prog()

        val visitor = Visitor()
        val ast: AST = visitor.visit(tree)

        // interference graph formation and colouring
        val gfVisitor = GraphFormationVisitor(graph)
        gfVisitor.visit(ast)
        graph.formGraph()
    }

    @Test
    fun graphTest1() {
        runTest("src/test/test_cases/optimisation/graph_colouring/graphtest1.wacc")
        assertEquals(1, graph.getNodes().size)
    }

    @Test
    fun graphTest2() {
        runTest("src/test/test_cases/optimisation/graph_colouring/graphtest2.wacc")
        assertEquals(2, graph.getNodes().size)

        val x = graph.findNode("x")!!
        val y = graph.findNode("y")!!

        assertTrue(x.getNeighbours().contains(y))
        assertTrue(y.getNeighbours().contains(x))
    }

    @Test
    fun graphTestOverflow() {
        runTest("src/test/test_cases/optimisation/graph_colouring/graphtestoverflow.wacc")
        assertEquals(8, graph.getNodes().size)

        graph.colourGraph()
        assertEquals(Register(-1), graph.findNode("h")!!.getRegister())
    }
}