// Generated from ./WaccLexer.g4 by ANTLR 4.9.1
package antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WaccLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, WHITESPACE=2, BEGIN=3, END=4, IS=5, CALL=6, NEW_PAIR=7, PAIR=8, 
		FST=9, SND=10, SKIP_STAT=11, RETURN=12, FREE=13, READ=14, EXIT=15, PRINT=16, 
		PRINTLN=17, IF=18, THEN=19, ELSE=20, FI=21, WHILE=22, DO=23, DONE=24, 
		OPEN_PARENTHESES=25, CLOSE_PARENTHESES=26, OPEN_SQUARE_BRACKET=27, CLOSE_SQUARE_BRACKET=28, 
		SEMICOLON=29, COMMA=30, INT=31, BOOL=32, CHAR=33, STRING=34, NOT=35, LEN=36, 
		ORD=37, CHR=38, EQUALS=39, PLUS=40, MINUS=41, MULT=42, DIV=43, MOD=44, 
		GT=45, GTE=46, LT=47, LTE=48, EQ=49, NOTEQ=50, AND=51, OR=52, INT_SIGN=53, 
		IDENT=54, NULL=55, INT_LIT=56, BOOL_LIT=57, CHAR_LIT=58, STR_LIT=59;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "WHITESPACE", "BEGIN", "END", "IS", "CALL", "NEW_PAIR", "PAIR", 
			"FST", "SND", "SKIP_STAT", "RETURN", "FREE", "READ", "EXIT", "PRINT", 
			"PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", "OPEN_PARENTHESES", 
			"CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKET", "CLOSE_SQUARE_BRACKET", "SEMICOLON", 
			"COMMA", "INT", "BOOL", "CHAR", "STRING", "NOT", "LEN", "ORD", "CHR", 
			"EQUALS", "PLUS", "MINUS", "MULT", "DIV", "MOD", "GT", "GTE", "LT", "LTE", 
			"EQ", "NOTEQ", "AND", "OR", "INT_SIGN", "IDENT", "NULL", "DIGIT", "CHARACTER", 
			"ESCAPED_CHAR", "STRING_FRAG", "INT_LIT", "BOOL_LIT", "CHAR_LIT", "STR_LIT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'begin'", "'end'", "'is'", "'call'", "'newpair'", 
			"'pair'", "'fst'", "'snd'", "'skip'", "'return'", "'free'", "'read'", 
			"'exit'", "'print'", "'println'", "'if'", "'then'", "'else'", "'fi'", 
			"'while'", "'do'", "'done'", "'('", "')'", "'['", "']'", "';'", "','", 
			"'int'", "'bool'", "'char'", "'string'", "'!'", "'len'", "'ord'", "'chr'", 
			"'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'>'", "'>='", "'<'", "'<='", 
			"'=='", "'!='", "'&&'", "'||'", null, null, "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "WHITESPACE", "BEGIN", "END", "IS", "CALL", "NEW_PAIR", 
			"PAIR", "FST", "SND", "SKIP_STAT", "RETURN", "FREE", "READ", "EXIT", 
			"PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", 
			"OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKET", "CLOSE_SQUARE_BRACKET", 
			"SEMICOLON", "COMMA", "INT", "BOOL", "CHAR", "STRING", "NOT", "LEN", 
			"ORD", "CHR", "EQUALS", "PLUS", "MINUS", "MULT", "DIV", "MOD", "GT", 
			"GTE", "LT", "LTE", "EQ", "NOTEQ", "AND", "OR", "INT_SIGN", "IDENT", 
			"NULL", "INT_LIT", "BOOL_LIT", "CHAR_LIT", "STR_LIT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public WaccLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WaccLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2=\u0185\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\7\2\u0084\n\2\f\2\16\2\u0087\13\2\3\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3"+
		"\"\3#\3#\3#\3#\3#\3#\3#\3$\3$\3%\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3\'"+
		"\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66"+
		"\3\66\3\67\3\67\3\67\7\67\u0155\n\67\f\67\16\67\u0158\13\67\38\38\38\3"+
		"8\38\39\39\3:\3:\3:\5:\u0164\n:\3;\3;\3<\7<\u0169\n<\f<\16<\u016c\13<"+
		"\3=\6=\u016f\n=\r=\16=\u0170\3>\3>\3>\3>\3>\3>\3>\3>\3>\5>\u017c\n>\3"+
		"?\3?\3?\3?\3@\3@\3@\3@\2\2A\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62c\63e\64g\65i\66k\67m8o9q\2s\2u\2w\2y:{;}<\177=\3\2\b\4\2\f\f\17\17"+
		"\5\2\13\f\17\17\"\"\4\2--//\5\2C\\aac|\5\2$$))^^\13\2$$))\62\62^^ddhh"+
		"ppttvv\2\u0187\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2"+
		"\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2"+
		"_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3"+
		"\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3"+
		"\2\2\2\3\u0081\3\2\2\2\5\u008c\3\2\2\2\7\u0090\3\2\2\2\t\u0096\3\2\2\2"+
		"\13\u009a\3\2\2\2\r\u009d\3\2\2\2\17\u00a2\3\2\2\2\21\u00aa\3\2\2\2\23"+
		"\u00af\3\2\2\2\25\u00b3\3\2\2\2\27\u00b7\3\2\2\2\31\u00bc\3\2\2\2\33\u00c3"+
		"\3\2\2\2\35\u00c8\3\2\2\2\37\u00cd\3\2\2\2!\u00d2\3\2\2\2#\u00d8\3\2\2"+
		"\2%\u00e0\3\2\2\2\'\u00e3\3\2\2\2)\u00e8\3\2\2\2+\u00ed\3\2\2\2-\u00f0"+
		"\3\2\2\2/\u00f6\3\2\2\2\61\u00f9\3\2\2\2\63\u00fe\3\2\2\2\65\u0100\3\2"+
		"\2\2\67\u0102\3\2\2\29\u0104\3\2\2\2;\u0106\3\2\2\2=\u0108\3\2\2\2?\u010a"+
		"\3\2\2\2A\u010e\3\2\2\2C\u0113\3\2\2\2E\u0118\3\2\2\2G\u011f\3\2\2\2I"+
		"\u0121\3\2\2\2K\u0125\3\2\2\2M\u0129\3\2\2\2O\u012d\3\2\2\2Q\u012f\3\2"+
		"\2\2S\u0131\3\2\2\2U\u0133\3\2\2\2W\u0135\3\2\2\2Y\u0137\3\2\2\2[\u0139"+
		"\3\2\2\2]\u013b\3\2\2\2_\u013e\3\2\2\2a\u0140\3\2\2\2c\u0143\3\2\2\2e"+
		"\u0146\3\2\2\2g\u0149\3\2\2\2i\u014c\3\2\2\2k\u014f\3\2\2\2m\u0151\3\2"+
		"\2\2o\u0159\3\2\2\2q\u015e\3\2\2\2s\u0163\3\2\2\2u\u0165\3\2\2\2w\u016a"+
		"\3\2\2\2y\u016e\3\2\2\2{\u017b\3\2\2\2}\u017d\3\2\2\2\177\u0181\3\2\2"+
		"\2\u0081\u0085\7%\2\2\u0082\u0084\n\2\2\2\u0083\u0082\3\2\2\2\u0084\u0087"+
		"\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0088\3\2\2\2\u0087"+
		"\u0085\3\2\2\2\u0088\u0089\t\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\b\2"+
		"\2\2\u008b\4\3\2\2\2\u008c\u008d\t\3\2\2\u008d\u008e\3\2\2\2\u008e\u008f"+
		"\b\3\3\2\u008f\6\3\2\2\2\u0090\u0091\7d\2\2\u0091\u0092\7g\2\2\u0092\u0093"+
		"\7i\2\2\u0093\u0094\7k\2\2\u0094\u0095\7p\2\2\u0095\b\3\2\2\2\u0096\u0097"+
		"\7g\2\2\u0097\u0098\7p\2\2\u0098\u0099\7f\2\2\u0099\n\3\2\2\2\u009a\u009b"+
		"\7k\2\2\u009b\u009c\7u\2\2\u009c\f\3\2\2\2\u009d\u009e\7e\2\2\u009e\u009f"+
		"\7c\2\2\u009f\u00a0\7n\2\2\u00a0\u00a1\7n\2\2\u00a1\16\3\2\2\2\u00a2\u00a3"+
		"\7p\2\2\u00a3\u00a4\7g\2\2\u00a4\u00a5\7y\2\2\u00a5\u00a6\7r\2\2\u00a6"+
		"\u00a7\7c\2\2\u00a7\u00a8\7k\2\2\u00a8\u00a9\7t\2\2\u00a9\20\3\2\2\2\u00aa"+
		"\u00ab\7r\2\2\u00ab\u00ac\7c\2\2\u00ac\u00ad\7k\2\2\u00ad\u00ae\7t\2\2"+
		"\u00ae\22\3\2\2\2\u00af\u00b0\7h\2\2\u00b0\u00b1\7u\2\2\u00b1\u00b2\7"+
		"v\2\2\u00b2\24\3\2\2\2\u00b3\u00b4\7u\2\2\u00b4\u00b5\7p\2\2\u00b5\u00b6"+
		"\7f\2\2\u00b6\26\3\2\2\2\u00b7\u00b8\7u\2\2\u00b8\u00b9\7m\2\2\u00b9\u00ba"+
		"\7k\2\2\u00ba\u00bb\7r\2\2\u00bb\30\3\2\2\2\u00bc\u00bd\7t\2\2\u00bd\u00be"+
		"\7g\2\2\u00be\u00bf\7v\2\2\u00bf\u00c0\7w\2\2\u00c0\u00c1\7t\2\2\u00c1"+
		"\u00c2\7p\2\2\u00c2\32\3\2\2\2\u00c3\u00c4\7h\2\2\u00c4\u00c5\7t\2\2\u00c5"+
		"\u00c6\7g\2\2\u00c6\u00c7\7g\2\2\u00c7\34\3\2\2\2\u00c8\u00c9\7t\2\2\u00c9"+
		"\u00ca\7g\2\2\u00ca\u00cb\7c\2\2\u00cb\u00cc\7f\2\2\u00cc\36\3\2\2\2\u00cd"+
		"\u00ce\7g\2\2\u00ce\u00cf\7z\2\2\u00cf\u00d0\7k\2\2\u00d0\u00d1\7v\2\2"+
		"\u00d1 \3\2\2\2\u00d2\u00d3\7r\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5\7k\2"+
		"\2\u00d5\u00d6\7p\2\2\u00d6\u00d7\7v\2\2\u00d7\"\3\2\2\2\u00d8\u00d9\7"+
		"r\2\2\u00d9\u00da\7t\2\2\u00da\u00db\7k\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd"+
		"\7v\2\2\u00dd\u00de\7n\2\2\u00de\u00df\7p\2\2\u00df$\3\2\2\2\u00e0\u00e1"+
		"\7k\2\2\u00e1\u00e2\7h\2\2\u00e2&\3\2\2\2\u00e3\u00e4\7v\2\2\u00e4\u00e5"+
		"\7j\2\2\u00e5\u00e6\7g\2\2\u00e6\u00e7\7p\2\2\u00e7(\3\2\2\2\u00e8\u00e9"+
		"\7g\2\2\u00e9\u00ea\7n\2\2\u00ea\u00eb\7u\2\2\u00eb\u00ec\7g\2\2\u00ec"+
		"*\3\2\2\2\u00ed\u00ee\7h\2\2\u00ee\u00ef\7k\2\2\u00ef,\3\2\2\2\u00f0\u00f1"+
		"\7y\2\2\u00f1\u00f2\7j\2\2\u00f2\u00f3\7k\2\2\u00f3\u00f4\7n\2\2\u00f4"+
		"\u00f5\7g\2\2\u00f5.\3\2\2\2\u00f6\u00f7\7f\2\2\u00f7\u00f8\7q\2\2\u00f8"+
		"\60\3\2\2\2\u00f9\u00fa\7f\2\2\u00fa\u00fb\7q\2\2\u00fb\u00fc\7p\2\2\u00fc"+
		"\u00fd\7g\2\2\u00fd\62\3\2\2\2\u00fe\u00ff\7*\2\2\u00ff\64\3\2\2\2\u0100"+
		"\u0101\7+\2\2\u0101\66\3\2\2\2\u0102\u0103\7]\2\2\u01038\3\2\2\2\u0104"+
		"\u0105\7_\2\2\u0105:\3\2\2\2\u0106\u0107\7=\2\2\u0107<\3\2\2\2\u0108\u0109"+
		"\7.\2\2\u0109>\3\2\2\2\u010a\u010b\7k\2\2\u010b\u010c\7p\2\2\u010c\u010d"+
		"\7v\2\2\u010d@\3\2\2\2\u010e\u010f\7d\2\2\u010f\u0110\7q\2\2\u0110\u0111"+
		"\7q\2\2\u0111\u0112\7n\2\2\u0112B\3\2\2\2\u0113\u0114\7e\2\2\u0114\u0115"+
		"\7j\2\2\u0115\u0116\7c\2\2\u0116\u0117\7t\2\2\u0117D\3\2\2\2\u0118\u0119"+
		"\7u\2\2\u0119\u011a\7v\2\2\u011a\u011b\7t\2\2\u011b\u011c\7k\2\2\u011c"+
		"\u011d\7p\2\2\u011d\u011e\7i\2\2\u011eF\3\2\2\2\u011f\u0120\7#\2\2\u0120"+
		"H\3\2\2\2\u0121\u0122\7n\2\2\u0122\u0123\7g\2\2\u0123\u0124\7p\2\2\u0124"+
		"J\3\2\2\2\u0125\u0126\7q\2\2\u0126\u0127\7t\2\2\u0127\u0128\7f\2\2\u0128"+
		"L\3\2\2\2\u0129\u012a\7e\2\2\u012a\u012b\7j\2\2\u012b\u012c\7t\2\2\u012c"+
		"N\3\2\2\2\u012d\u012e\7?\2\2\u012eP\3\2\2\2\u012f\u0130\7-\2\2\u0130R"+
		"\3\2\2\2\u0131\u0132\7/\2\2\u0132T\3\2\2\2\u0133\u0134\7,\2\2\u0134V\3"+
		"\2\2\2\u0135\u0136\7\61\2\2\u0136X\3\2\2\2\u0137\u0138\7\'\2\2\u0138Z"+
		"\3\2\2\2\u0139\u013a\7@\2\2\u013a\\\3\2\2\2\u013b\u013c\7@\2\2\u013c\u013d"+
		"\7?\2\2\u013d^\3\2\2\2\u013e\u013f\7>\2\2\u013f`\3\2\2\2\u0140\u0141\7"+
		">\2\2\u0141\u0142\7?\2\2\u0142b\3\2\2\2\u0143\u0144\7?\2\2\u0144\u0145"+
		"\7?\2\2\u0145d\3\2\2\2\u0146\u0147\7#\2\2\u0147\u0148\7?\2\2\u0148f\3"+
		"\2\2\2\u0149\u014a\7(\2\2\u014a\u014b\7(\2\2\u014bh\3\2\2\2\u014c\u014d"+
		"\7~\2\2\u014d\u014e\7~\2\2\u014ej\3\2\2\2\u014f\u0150\t\4\2\2\u0150l\3"+
		"\2\2\2\u0151\u0156\t\5\2\2\u0152\u0155\t\5\2\2\u0153\u0155\5q9\2\u0154"+
		"\u0152\3\2\2\2\u0154\u0153\3\2\2\2\u0155\u0158\3\2\2\2\u0156\u0154\3\2"+
		"\2\2\u0156\u0157\3\2\2\2\u0157n\3\2\2\2\u0158\u0156\3\2\2\2\u0159\u015a"+
		"\7p\2\2\u015a\u015b\7w\2\2\u015b\u015c\7n\2\2\u015c\u015d\7n\2\2\u015d"+
		"p\3\2\2\2\u015e\u015f\4\62;\2\u015fr\3\2\2\2\u0160\u0164\n\6\2\2\u0161"+
		"\u0162\7^\2\2\u0162\u0164\5u;\2\u0163\u0160\3\2\2\2\u0163\u0161\3\2\2"+
		"\2\u0164t\3\2\2\2\u0165\u0166\t\7\2\2\u0166v\3\2\2\2\u0167\u0169\5s:\2"+
		"\u0168\u0167\3\2\2\2\u0169\u016c\3\2\2\2\u016a\u0168\3\2\2\2\u016a\u016b"+
		"\3\2\2\2\u016bx\3\2\2\2\u016c\u016a\3\2\2\2\u016d\u016f\5q9\2\u016e\u016d"+
		"\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u016e\3\2\2\2\u0170\u0171\3\2\2\2\u0171"+
		"z\3\2\2\2\u0172\u0173\7v\2\2\u0173\u0174\7t\2\2\u0174\u0175\7w\2\2\u0175"+
		"\u017c\7g\2\2\u0176\u0177\7h\2\2\u0177\u0178\7c\2\2\u0178\u0179\7n\2\2"+
		"\u0179\u017a\7u\2\2\u017a\u017c\7g\2\2\u017b\u0172\3\2\2\2\u017b\u0176"+
		"\3\2\2\2\u017c|\3\2\2\2\u017d\u017e\7)\2\2\u017e\u017f\5s:\2\u017f\u0180"+
		"\7)\2\2\u0180~\3\2\2\2\u0181\u0182\7$\2\2\u0182\u0183\5w<\2\u0183\u0184"+
		"\7$\2\2\u0184\u0080\3\2\2\2\n\2\u0085\u0154\u0156\u0163\u016a\u0170\u017b"+
		"\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}