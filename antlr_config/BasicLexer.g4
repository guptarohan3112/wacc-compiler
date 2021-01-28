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
PRINT:   'print' ;
PRINTLN: 'println' ;


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




