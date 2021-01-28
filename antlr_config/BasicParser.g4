parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

unaryOper: NOT | MINUS | LEN | ORD | CHR;

binaryOper: PLUS | MINUS ;

expr: unaryOper expr
| expr binaryOper expr
| INTEGER
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
;

stat: RETURN expr
| PRINT expr
| PRINTLN expr
| stat SEMICOLON stat
| BEGIN stat END
;

// EOF indicates that the program must consume to the end of the input.
prog: BEGIN stat END EOF ;
