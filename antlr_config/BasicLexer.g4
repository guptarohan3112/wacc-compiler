lexer grammar BasicLexer;

// Keywords
BEGIN: 'begin' ;
END: 'end' ;

// Brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

// Identifiers
IDENT: ('_' | 'a'..'z' | 'A'..'Z') ('_' | 'a'..'z' | 'A'..'Z' | DIGIT)* ;

// Integer sign
INT_SIGN: '+' | '-' ;

// Characters
CHAR: ~('\\' | '\'' | '"') | '\\' ESCAPED_CHAR ;
ESCAPED_CHAR: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\' ;

// Literals for base types
fragment DIGIT: '0'..'9' ; 
INT_LIT: INT_SIGN? DIGIT+ ;

BOOL_LIT: 'true' | 'false' ;

CHAR_LIT: '\'' CHAR '\'' ;

fragment STRING: CHAR* ;
STR_LIT: '"' STRING '"' ;

// Statements
RETURN:  'return' ;
FREE:    'free' ;
READ:    'read' ;
PRINT:   'print' ;
PRINTLN: 'println' ;
EXIT:    'exit' ;
IF :     'if' ;
THEN :   'then' ;
ELSE :   'else' ;
FI :     'fi' ;
WHILE :  'while' ;
DO :     'do' ;
DONE :   'done' ;

// Unary operators
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;

// Binary operators
PLUS: '+' ;
MINUS: '-' ;
MULT: '*' ;
DIV: '/' ;
MOD: '%' ;
GT: '>' ;
GTE: '>=' ;
LT: '<';
LTE: '<=' ;
EQ: '==' ;
NOTEQ: '!=' ;
AND: '&&';
OR: '||' ;

// Semicolon
SEMICOLON: ';' ;

// Comments and whitespace
COMMENT: '#' ~[\r\n]* [\r\n] -> skip ;
WHITESPACE: (' '|'\n'|'\t'|'\r') -> skip ;


