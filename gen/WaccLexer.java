// Generated from /home/rahilshah/Documents/Year2/Laboratory2/WACC/wacc_05/antlr_config/WaccLexer.g4 by ANTLR 4.9.1
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
		FST=9, SND=10, PAIR_LIT=11, SKIP_STAT=12, RETURN=13, FREE=14, READ=15, 
		EXIT=16, PRINT=17, PRINTLN=18, IF=19, THEN=20, ELSE=21, FI=22, WHILE=23, 
		DO=24, DONE=25, OPEN_PARENTHESES=26, CLOSE_PARENTHESES=27, OPEN_SQUARE_BRACKET=28, 
		CLOSE_SQUARE_BRACKET=29, SEMICOLON=30, COMMA=31, INT=32, BOOL=33, CHAR=34, 
		STRING=35, NOT=36, LEN=37, ORD=38, CHR=39, EQUALS=40, PLUS=41, MINUS=42, 
		MULT=43, DIV=44, MOD=45, GT=46, GTE=47, LT=48, LTE=49, EQ=50, NOTEQ=51, 
		AND=52, OR=53, INT_SIGN=54, INT_LIT=55, BOOL_LIT=56, CHAR_LIT=57, STR_LIT=58, 
		IDENT=59;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "WHITESPACE", "BEGIN", "END", "IS", "CALL", "NEW_PAIR", "PAIR", 
			"FST", "SND", "PAIR_LIT", "SKIP_STAT", "RETURN", "FREE", "READ", "EXIT", 
			"PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", 
			"OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKET", "CLOSE_SQUARE_BRACKET", 
			"SEMICOLON", "COMMA", "INT", "BOOL", "CHAR", "STRING", "NOT", "LEN", 
			"ORD", "CHR", "EQUALS", "PLUS", "MINUS", "MULT", "DIV", "MOD", "GT", 
			"GTE", "LT", "LTE", "EQ", "NOTEQ", "AND", "OR", "INT_SIGN", "DIGIT", 
			"CHARACTER", "ESCAPED_CHAR", "STRING_FRAG", "INT_LIT", "BOOL_LIT", "CHAR_LIT", 
			"STR_LIT", "IDENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'begin'", "'end'", "'is'", "'call'", "'newpair'", 
			"'pair'", "'fst'", "'snd'", "'null'", "'skip'", "'return'", "'free'", 
			"'read'", "'exit'", "'print'", "'println'", "'if'", "'then'", "'else'", 
			"'fi'", "'while'", "'do'", "'done'", "'('", "')'", "'['", "']'", "';'", 
			"','", "'int'", "'bool'", "'char'", "'string'", "'!'", "'len'", "'ord'", 
			"'chr'", "'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'>'", "'>='", "'<'", 
			"'<='", "'=='", "'!='", "'&&'", "'||'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "WHITESPACE", "BEGIN", "END", "IS", "CALL", "NEW_PAIR", 
			"PAIR", "FST", "SND", "PAIR_LIT", "SKIP_STAT", "RETURN", "FREE", "READ", 
			"EXIT", "PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", 
			"DONE", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKET", 
			"CLOSE_SQUARE_BRACKET", "SEMICOLON", "COMMA", "INT", "BOOL", "CHAR", 
			"STRING", "NOT", "LEN", "ORD", "CHR", "EQUALS", "PLUS", "MINUS", "MULT", 
			"DIV", "MOD", "GT", "GTE", "LT", "LTE", "EQ", "NOTEQ", "AND", "OR", "INT_SIGN", 
			"INT_LIT", "BOOL_LIT", "CHAR_LIT", "STR_LIT", "IDENT"
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
		"\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3"+
		"\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3"+
		"\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3"+
		"\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3!\3\"\3\""+
		"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3%\3%\3&\3&\3&\3&\3\'"+
		"\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60"+
		"\3\60\3\60\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65"+
		"\3\65\3\65\3\66\3\66\3\66\3\67\3\67\38\38\39\39\39\59\u015c\n9\3:\3:\3"+
		";\7;\u0161\n;\f;\16;\u0164\13;\3<\6<\u0167\n<\r<\16<\u0168\3=\3=\3=\3"+
		"=\3=\3=\3=\3=\3=\5=\u0174\n=\3>\3>\3>\3>\3?\3?\3?\3?\3@\3@\3@\7@\u0181"+
		"\n@\f@\16@\u0184\13@\2\2A\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62c\63e\64g\65i\66k\67m8o\2q\2s\2u\2w9y:{;}<\177=\3\2\b\4\2\f\f\17\17"+
		"\5\2\13\f\17\17\"\"\4\2--//\5\2$$))^^\13\2$$))\62\62^^ddhhppttvv\5\2C"+
		"\\aac|\2\u0187\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2"+
		"\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2"+
		"_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3"+
		"\2\2\2\2m\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3"+
		"\2\2\2\3\u0081\3\2\2\2\5\u008c\3\2\2\2\7\u0090\3\2\2\2\t\u0096\3\2\2\2"+
		"\13\u009a\3\2\2\2\r\u009d\3\2\2\2\17\u00a2\3\2\2\2\21\u00aa\3\2\2\2\23"+
		"\u00af\3\2\2\2\25\u00b3\3\2\2\2\27\u00b7\3\2\2\2\31\u00bc\3\2\2\2\33\u00c1"+
		"\3\2\2\2\35\u00c8\3\2\2\2\37\u00cd\3\2\2\2!\u00d2\3\2\2\2#\u00d7\3\2\2"+
		"\2%\u00dd\3\2\2\2\'\u00e5\3\2\2\2)\u00e8\3\2\2\2+\u00ed\3\2\2\2-\u00f2"+
		"\3\2\2\2/\u00f5\3\2\2\2\61\u00fb\3\2\2\2\63\u00fe\3\2\2\2\65\u0103\3\2"+
		"\2\2\67\u0105\3\2\2\29\u0107\3\2\2\2;\u0109\3\2\2\2=\u010b\3\2\2\2?\u010d"+
		"\3\2\2\2A\u010f\3\2\2\2C\u0113\3\2\2\2E\u0118\3\2\2\2G\u011d\3\2\2\2I"+
		"\u0124\3\2\2\2K\u0126\3\2\2\2M\u012a\3\2\2\2O\u012e\3\2\2\2Q\u0132\3\2"+
		"\2\2S\u0134\3\2\2\2U\u0136\3\2\2\2W\u0138\3\2\2\2Y\u013a\3\2\2\2[\u013c"+
		"\3\2\2\2]\u013e\3\2\2\2_\u0140\3\2\2\2a\u0143\3\2\2\2c\u0145\3\2\2\2e"+
		"\u0148\3\2\2\2g\u014b\3\2\2\2i\u014e\3\2\2\2k\u0151\3\2\2\2m\u0154\3\2"+
		"\2\2o\u0156\3\2\2\2q\u015b\3\2\2\2s\u015d\3\2\2\2u\u0162\3\2\2\2w\u0166"+
		"\3\2\2\2y\u0173\3\2\2\2{\u0175\3\2\2\2}\u0179\3\2\2\2\177\u017d\3\2\2"+
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
		"\7f\2\2\u00b6\26\3\2\2\2\u00b7\u00b8\7p\2\2\u00b8\u00b9\7w\2\2\u00b9\u00ba"+
		"\7n\2\2\u00ba\u00bb\7n\2\2\u00bb\30\3\2\2\2\u00bc\u00bd\7u\2\2\u00bd\u00be"+
		"\7m\2\2\u00be\u00bf\7k\2\2\u00bf\u00c0\7r\2\2\u00c0\32\3\2\2\2\u00c1\u00c2"+
		"\7t\2\2\u00c2\u00c3\7g\2\2\u00c3\u00c4\7v\2\2\u00c4\u00c5\7w\2\2\u00c5"+
		"\u00c6\7t\2\2\u00c6\u00c7\7p\2\2\u00c7\34\3\2\2\2\u00c8\u00c9\7h\2\2\u00c9"+
		"\u00ca\7t\2\2\u00ca\u00cb\7g\2\2\u00cb\u00cc\7g\2\2\u00cc\36\3\2\2\2\u00cd"+
		"\u00ce\7t\2\2\u00ce\u00cf\7g\2\2\u00cf\u00d0\7c\2\2\u00d0\u00d1\7f\2\2"+
		"\u00d1 \3\2\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4\7z\2\2\u00d4\u00d5\7k\2"+
		"\2\u00d5\u00d6\7v\2\2\u00d6\"\3\2\2\2\u00d7\u00d8\7r\2\2\u00d8\u00d9\7"+
		"t\2\2\u00d9\u00da\7k\2\2\u00da\u00db\7p\2\2\u00db\u00dc\7v\2\2\u00dc$"+
		"\3\2\2\2\u00dd\u00de\7r\2\2\u00de\u00df\7t\2\2\u00df\u00e0\7k\2\2\u00e0"+
		"\u00e1\7p\2\2\u00e1\u00e2\7v\2\2\u00e2\u00e3\7n\2\2\u00e3\u00e4\7p\2\2"+
		"\u00e4&\3\2\2\2\u00e5\u00e6\7k\2\2\u00e6\u00e7\7h\2\2\u00e7(\3\2\2\2\u00e8"+
		"\u00e9\7v\2\2\u00e9\u00ea\7j\2\2\u00ea\u00eb\7g\2\2\u00eb\u00ec\7p\2\2"+
		"\u00ec*\3\2\2\2\u00ed\u00ee\7g\2\2\u00ee\u00ef\7n\2\2\u00ef\u00f0\7u\2"+
		"\2\u00f0\u00f1\7g\2\2\u00f1,\3\2\2\2\u00f2\u00f3\7h\2\2\u00f3\u00f4\7"+
		"k\2\2\u00f4.\3\2\2\2\u00f5\u00f6\7y\2\2\u00f6\u00f7\7j\2\2\u00f7\u00f8"+
		"\7k\2\2\u00f8\u00f9\7n\2\2\u00f9\u00fa\7g\2\2\u00fa\60\3\2\2\2\u00fb\u00fc"+
		"\7f\2\2\u00fc\u00fd\7q\2\2\u00fd\62\3\2\2\2\u00fe\u00ff\7f\2\2\u00ff\u0100"+
		"\7q\2\2\u0100\u0101\7p\2\2\u0101\u0102\7g\2\2\u0102\64\3\2\2\2\u0103\u0104"+
		"\7*\2\2\u0104\66\3\2\2\2\u0105\u0106\7+\2\2\u01068\3\2\2\2\u0107\u0108"+
		"\7]\2\2\u0108:\3\2\2\2\u0109\u010a\7_\2\2\u010a<\3\2\2\2\u010b\u010c\7"+
		"=\2\2\u010c>\3\2\2\2\u010d\u010e\7.\2\2\u010e@\3\2\2\2\u010f\u0110\7k"+
		"\2\2\u0110\u0111\7p\2\2\u0111\u0112\7v\2\2\u0112B\3\2\2\2\u0113\u0114"+
		"\7d\2\2\u0114\u0115\7q\2\2\u0115\u0116\7q\2\2\u0116\u0117\7n\2\2\u0117"+
		"D\3\2\2\2\u0118\u0119\7e\2\2\u0119\u011a\7j\2\2\u011a\u011b\7c\2\2\u011b"+
		"\u011c\7t\2\2\u011cF\3\2\2\2\u011d\u011e\7u\2\2\u011e\u011f\7v\2\2\u011f"+
		"\u0120\7t\2\2\u0120\u0121\7k\2\2\u0121\u0122\7p\2\2\u0122\u0123\7i\2\2"+
		"\u0123H\3\2\2\2\u0124\u0125\7#\2\2\u0125J\3\2\2\2\u0126\u0127\7n\2\2\u0127"+
		"\u0128\7g\2\2\u0128\u0129\7p\2\2\u0129L\3\2\2\2\u012a\u012b\7q\2\2\u012b"+
		"\u012c\7t\2\2\u012c\u012d\7f\2\2\u012dN\3\2\2\2\u012e\u012f\7e\2\2\u012f"+
		"\u0130\7j\2\2\u0130\u0131\7t\2\2\u0131P\3\2\2\2\u0132\u0133\7?\2\2\u0133"+
		"R\3\2\2\2\u0134\u0135\7-\2\2\u0135T\3\2\2\2\u0136\u0137\7/\2\2\u0137V"+
		"\3\2\2\2\u0138\u0139\7,\2\2\u0139X\3\2\2\2\u013a\u013b\7\61\2\2\u013b"+
		"Z\3\2\2\2\u013c\u013d\7\'\2\2\u013d\\\3\2\2\2\u013e\u013f\7@\2\2\u013f"+
		"^\3\2\2\2\u0140\u0141\7@\2\2\u0141\u0142\7?\2\2\u0142`\3\2\2\2\u0143\u0144"+
		"\7>\2\2\u0144b\3\2\2\2\u0145\u0146\7>\2\2\u0146\u0147\7?\2\2\u0147d\3"+
		"\2\2\2\u0148\u0149\7?\2\2\u0149\u014a\7?\2\2\u014af\3\2\2\2\u014b\u014c"+
		"\7#\2\2\u014c\u014d\7?\2\2\u014dh\3\2\2\2\u014e\u014f\7(\2\2\u014f\u0150"+
		"\7(\2\2\u0150j\3\2\2\2\u0151\u0152\7~\2\2\u0152\u0153\7~\2\2\u0153l\3"+
		"\2\2\2\u0154\u0155\t\4\2\2\u0155n\3\2\2\2\u0156\u0157\4\62;\2\u0157p\3"+
		"\2\2\2\u0158\u015c\n\5\2\2\u0159\u015a\7^\2\2\u015a\u015c\5s:\2\u015b"+
		"\u0158\3\2\2\2\u015b\u0159\3\2\2\2\u015cr\3\2\2\2\u015d\u015e\t\6\2\2"+
		"\u015et\3\2\2\2\u015f\u0161\5q9\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2"+
		"\2\2\u0162\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163v\3\2\2\2\u0164\u0162"+
		"\3\2\2\2\u0165\u0167\5o8\2\u0166\u0165\3\2\2\2\u0167\u0168\3\2\2\2\u0168"+
		"\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169x\3\2\2\2\u016a\u016b\7v\2\2\u016b"+
		"\u016c\7t\2\2\u016c\u016d\7w\2\2\u016d\u0174\7g\2\2\u016e\u016f\7h\2\2"+
		"\u016f\u0170\7c\2\2\u0170\u0171\7n\2\2\u0171\u0172\7u\2\2\u0172\u0174"+
		"\7g\2\2\u0173\u016a\3\2\2\2\u0173\u016e\3\2\2\2\u0174z\3\2\2\2\u0175\u0176"+
		"\7)\2\2\u0176\u0177\5q9\2\u0177\u0178\7)\2\2\u0178|\3\2\2\2\u0179\u017a"+
		"\7$\2\2\u017a\u017b\5u;\2\u017b\u017c\7$\2\2\u017c~\3\2\2\2\u017d\u0182"+
		"\t\7\2\2\u017e\u0181\t\7\2\2\u017f\u0181\5o8\2\u0180\u017e\3\2\2\2\u0180"+
		"\u017f\3\2\2\2\u0181\u0184\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0183\3\2"+
		"\2\2\u0183\u0080\3\2\2\2\u0184\u0182\3\2\2\2\n\2\u0085\u015b\u0162\u0168"+
		"\u0173\u0180\u0182\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}