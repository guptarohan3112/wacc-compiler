parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

unaryOper: NOT
| MINUS
| LEN
| ORD
| CHR;

binaryOper: PLUS
| MINUS
| MULT
| DIV
| MOD
| GT
| GTE
| LT
| LTE
| EQ
| NOTEQ
| AND
| OR ;

expr: unaryOper expr
| expr binaryOper expr
| INT_LIT
| BOOL_LIT
| STR_LIT
| CHAR_LIT
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
;

stat: FREE expr
| RETURN expr
| EXIT expr
| PRINT expr
| PRINTLN expr
| IF expr THEN stat ELSE stat FI
| WHILE expr DO stat DONE
| BEGIN stat END
| stat SEMICOLON stat
;

// EOF indicates that the program must consume to the end of the input.
prog: BEGIN stat END EOF ;
