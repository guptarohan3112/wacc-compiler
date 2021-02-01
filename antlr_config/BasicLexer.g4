lexer grammar BasicLexer;

//operators
PLUS: '+' ;
MINUS: '-' ;

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
