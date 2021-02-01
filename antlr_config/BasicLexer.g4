lexer grammar BasicLexer;

//operators
PLUS: '+' ;
MINUS: '-' ;

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

//numbers
fragment DIGIT: '0'..'9' ; 

INT_SIGN: '+' | '-' ;
INTEGER: INT_SIGN? DIGIT+ ;
BOOLEAN: 'true' | 'false' ;
fragment STRING: CHAR* ;

STR_LITER: '"' STRING '"' ;



