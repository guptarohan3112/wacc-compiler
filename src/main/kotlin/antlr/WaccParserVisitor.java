// Generated from ./WaccParser.g4 by ANTLR 4.9.1
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WaccParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WaccParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WaccParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(WaccParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(WaccParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(WaccParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(WaccParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statBeginEnd}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatBeginEnd(WaccParser.StatBeginEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statFree}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatFree(WaccParser.StatFreeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statPrint}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatPrint(WaccParser.StatPrintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statPrintln}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatPrintln(WaccParser.StatPrintlnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statSequential}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatSequential(WaccParser.StatSequentialContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statExit}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatExit(WaccParser.StatExitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statWhile}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatWhile(WaccParser.StatWhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statDeclaration}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatDeclaration(WaccParser.StatDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statAssignRHS}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatAssignRHS(WaccParser.StatAssignRHSContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statSkip}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatSkip(WaccParser.StatSkipContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statReturn}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatReturn(WaccParser.StatReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statIf}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatIf(WaccParser.StatIfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statAssignLHS}
	 * labeled alternative in {@link WaccParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatAssignLHS(WaccParser.StatAssignLHSContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#assignLHS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignLHS(WaccParser.AssignLHSContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#assignRHS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignRHS(WaccParser.AssignRHSContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(WaccParser.ArgListContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pairElem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairElem(WaccParser.PairElemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(WaccParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#baseType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseType(WaccParser.BaseTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pairType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairType(WaccParser.PairTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pairElemType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairElemType(WaccParser.PairElemTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(WaccParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#unaryOper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOper(WaccParser.UnaryOperContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#binaryOper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOper(WaccParser.BinaryOperContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#arrayElem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayElem(WaccParser.ArrayElemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#arrayLit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLit(WaccParser.ArrayLitContext ctx);
	/**
	 * Visit a parse tree produced by {@link WaccParser#pairLit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairLit(WaccParser.PairLitContext ctx);
}