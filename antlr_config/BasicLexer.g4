lexer grammar BasicLexer;

BEGIN: 'begin' ;
END: 'end' ;

//operators
PLUS: '+' ;
MINUS: '-' ;

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

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

//numbers
fragment DIGIT: '0'..'9' ; 

INTEGER: DIGIT+ ;

SEMICOLON: ';' ;

//comments and whitespace
COMMENT: '#' ~[\r\n]* [\r\n] -> skip ;
WHITESPACE: (' '|'\n'|'\t'|'\r') -> skip ;




