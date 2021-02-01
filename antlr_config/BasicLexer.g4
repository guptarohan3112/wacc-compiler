lexer grammar BasicLexer;

BEGIN: 'begin' ;
END: 'end' ;

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

//base-types
fragment DIGIT: '0'..'9' ; 

INT_SIGN: '+' | '-' ;
INTEGER: INT_SIGN? DIGIT+ ;

BOOLEAN: 'true' | 'false' ;

fragment STRING: CHAR* ;
STR_LITER: '"' STRING '"' ;

//characters
CHAR_LITER: '\'' CHAR '\'' ;
CHAR: ~('\\' | '\'' | '"') | '\\' ESCAPED_CHAR ;
ESCAPED_CHAR: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\' ;

//identifiers
IDENT: ('_' | 'a'..'z' | 'A'..'Z') ('_' | 'a'..'z' | 'A'..'Z' | DIGIT)* ;

//statements
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

//unary operators
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;

//Binary operators
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

SEMICOLON: ';' ;

//comments and whitespace
COMMENT: '#' ~[\r\n]* [\r\n] -> skip ;
WHITESPACE: (' '|'\n'|'\t'|'\r') -> skip ;


