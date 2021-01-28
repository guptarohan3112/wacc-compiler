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

// EOF indicates that the program must consume to the end of the input.
prog: BEGIN (expr SEMICOLON)*  END EOF ;
