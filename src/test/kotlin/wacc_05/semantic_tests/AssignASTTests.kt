//package wacc_05.semantic_tests
//
//import io.mockk.*
//import org.junit.Test
//import wacc_05.ast_structure.ExprAST
//import wacc_05.ast_structure.StatementAST
//import wacc_05.ast_structure.assignment_ast.*
//import wacc_05.symbol_table.identifier_objects.FunctionIdentifier
//import wacc_05.symbol_table.identifier_objects.TypeIdentifier
//import wacc_05.symbol_table.identifier_objects.VariableIdentifier
//
//class AssignASTTests : StatSemanticTests() {
//
//    @Test
//    fun assignASTLHSValidIdentCheck() {
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(intType))
//
//        StatementAST.AssignAST(AssignLHSAST("x"), ExprAST.IntLiterAST("+", "3")).check(st, seh)
//    }
//
//    @Test
//    fun assignASTLHSValidPairElemCheck() {
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(TypeIdentifier.PairIdentifier(intType, intType)))
//
//        StatementAST.AssignAST(
//            AssignLHSAST(PairElemAST(ExprAST.IdentAST("x"), false)),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//    }
//
//    @Test
//    fun assignASTLHSValidArrayElemCheck() {
//        // x[3] = 3
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))
//
//        StatementAST.AssignAST(
//            AssignLHSAST(ExprAST.ArrayElemAST("x", arrayListOf(ExprAST.IntLiterAST("+", "3")))),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//    }
//
//    @Test
//    fun assignASTLHSIdentNotPresentCheck() {
//        st.add("int", intType)
//
//        every { seh.invalidIdentifier("x") } just Runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.invalidIdentifier("x") }
//    }
//
//    @Test
//    fun assignASTLHSIncorrectTypeCheck() {
//        st.add("int", intType)
//        st.add("char", charType)
//        st.add("x", VariableIdentifier(charType))
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.typeMismatch(charType, intType) }
//    }
//
//    @Test
//    fun assignASTLHSPairNotPresentCheck() {
//        st.add("int", intType)
//
//        every { seh.invalidIdentifier(any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST(PairElemAST(ExprAST.IdentAST("x"), true)),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.invalidIdentifier("x") }
//    }
//
//    @Test
//    fun assignASTLHSArrayNotPresentCheck() {
//        st.add("int", intType)
//
//        every { seh.invalidIdentifier(any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST(ExprAST.ArrayElemAST("x", arrayListOf(ExprAST.IntLiterAST("+", "3")))),
//            ExprAST.IntLiterAST("+", "3")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.invalidIdentifier("x") }
//    }
//
//    @Test
//    fun assignASTRHSValidArrayLiterCheck() {
//        // we assign an array of same type but different length to x
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))
//
//        // x = [3]
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ArrayLiterAST(arrayListOf(ExprAST.IntLiterAST("+", "3")))
//        ).check(st, seh)
//    }
//
//    @Test
//    fun assignASTRHSValidNewPairCheck() {
//        val identifier = TypeIdentifier.PairIdentifier(intType, charType)
//
//        st.add("int", intType)
//        st.add("char", charType)
//        st.add("x", VariableIdentifier(identifier))
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            NewPairAST(ExprAST.IntLiterAST("+", "3"), ExprAST.CharLiterAST("c"))
//        ).check(st, seh)
//    }
//
//    @Test
//    fun assignASTRHSValidCallCheck() {
//        // create a generic function identifier of int return type
//        // test should succeed since function has same return type as variable x
//        val identifier = FunctionIdentifier(intType, ArrayList(), st)
//
//        st.add("int", intType)
//        st.add("foo", identifier)
//        st.add("x", VariableIdentifier(intType))
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            FuncCallAST("foo", ArrayList())
//        ).check(st, seh)
//    }
//
//    @Test
//    fun assignASTRHSIncorrectTypeCheck() {
//        st.add("int", intType)
//        st.add("char", charType)
//        st.add("x", VariableIdentifier(intType))
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ExprAST.CharLiterAST("c")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
//    }
//
//    @Test
//    fun assignASTRHSUndefinedIdentifierCheck() {
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(intType))
//
//        every { seh.invalidIdentifier(any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ExprAST.IdentAST("y")
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.invalidIdentifier("y") }
//    }
//
//    @Test
//    fun assignASTRHSArrayLiterIncorrectTypeCheck() {
//        // we try and assign an array of different type
//        st.add("int", intType)
//        st.add("char", charType)
//        st.add("x", VariableIdentifier(TypeIdentifier.ArrayIdentifier(intType, 4)))
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            ArrayLiterAST(arrayListOf(ExprAST.CharLiterAST("c"), ExprAST.CharLiterAST("f")))
//        ).check(st, seh)
//
//        verify(exactly = 1) {
//            seh.typeMismatch(
//                TypeIdentifier.ArrayIdentifier(intType, 0),
//                TypeIdentifier.ArrayIdentifier(charType, 0)
//            )
//        }
//    }
//
//    @Test
//    fun assignASTRHSFuncCallIncorrectTypeCheck() {
//        val identifier = FunctionIdentifier(charType, ArrayList(), st)
//        st.add("int", intType)
//        st.add("char", charType)
//        st.add("x", VariableIdentifier(intType))
//        st.add("foo", identifier)
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            FuncCallAST("foo", ArrayList())
//        ).check(st, seh)
//
//        verify(exactly = 1) { seh.typeMismatch(intType, charType) }
//    }
//
//    @Test
//    fun assignASTRHSNewPairTypeMismatchCheck() {
//        val intCharIdentifier = TypeIdentifier.PairIdentifier(intType, charType)
//        val charIntIdentifier = TypeIdentifier.PairIdentifier(charType, intType)
//        st.add("char", charType)
//        st.add("int", intType)
//        st.add("x", VariableIdentifier(intCharIdentifier))
//
//        every { seh.typeMismatch(any(), any()) } just runs
//
//        StatementAST.AssignAST(
//            AssignLHSAST("x"),
//            NewPairAST(ExprAST.CharLiterAST("c"), ExprAST.IntLiterAST("+", "3"))
//        ).check(st, seh)
//
//        verify { seh.typeMismatch(intCharIdentifier, charIntIdentifier) }
//    }
//}