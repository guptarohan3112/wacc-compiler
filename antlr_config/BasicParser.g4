parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

prog: BEGIN (func)* stat END EOF ;

func: type IDENT OPEN_PARENTHESES paramList? CLOSE_PARENTHESES IS stat END ;

paramList: param (COMMA param)* ;

param: type IDENT ;

stat: SKIP_STAT
| type IDENT EQUALS assignRHS
| assignLHS EQUALS assignRHS
| READ assignLHS
| FREE expr
| RETURN expr
| EXIT expr
| PRINT expr
| PRINTLN expr
| IF expr THEN stat ELSE stat FI
| WHILE expr DO stat DONE
| BEGIN stat END
| stat SEMICOLON stat
;

assignLHS: IDENT
;

assignRHS: expr
;

type: baseType
;

baseType: INT
| BOOL
| CHAR
| STRING
;

expr: (PLUS|MINUS)? INT_LIT
| BOOL_LIT
| STR_LIT
| CHAR_LIT
| IDENT
| unaryOper expr
| expr binaryOper expr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
;

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