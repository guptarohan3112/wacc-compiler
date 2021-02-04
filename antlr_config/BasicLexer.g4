lexer grammar BasicLexer;

// Comments and whitespace
COMMENT: '#' ~[\r\n]* [\r\n] -> skip ;
WHITESPACE: (' '|'\n'|'\t'|'\r') -> channel(HIDDEN) ;

// Keywords
BEGIN: 'begin' ;
END: 'end' ;

// Statements
SKIP_STAT: 'skip' ;
RETURN:    'return' ;
FREE:      'free' ;
READ:      'read' ;
EXIT:      'exit' ;
PRINT:     'print' ;
PRINTLN:   'println' ;
IF :       'if' ;
THEN :     'then' ;
ELSE :     'else' ;
FI :       'fi' ;
WHILE :    'while' ;
DO :       'do' ;
DONE :     'done' ;
IS :       'is' ;

// Brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

// Semicolon and Comma
SEMICOLON: ';' ;
COMMA:     ',' ;

// base types
INT:    'int' ;
BOOL:   'bool' ;
CHAR:   'char' ;
STRING: 'string' ;

// Unary operators
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;

// Assignment
EQUALS: '=' ;

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

// Integer sign
INT_SIGN: '+' | '-' ;

// Identifiers
IDENT: ('_' | 'a'..'z' | 'A'..'Z') ('_' | 'a'..'z' | 'A'..'Z' | DIGIT)* ;

fragment DIGIT: '0'..'9' ;
fragment CHARACTER: ~('\\' | '\'' | '"') | '\\' ESCAPED_CHAR ;
fragment ESCAPED_CHAR: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\' ;
fragment STRING_FRAG: CHARACTER* ;

// Literals for base types
INT_LIT: DIGIT+ ;
BOOL_LIT: 'true' | 'false' ;
CHAR_LIT: '\'' CHAR '\'' ;
STR_LIT: '"' STRING '"' ;



