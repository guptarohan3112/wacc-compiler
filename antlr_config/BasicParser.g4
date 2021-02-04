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
| arrayElem
| pairElem
;

assignRHS: expr
| arrayLit
| newPair
| pairElem
| call
;

newPair: NEW_PAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES ;

call: CALL IDENT OPEN_PARENTHESES argList? CLOSE_PARENTHESES ;

argList: expr (COMMA expr)* ;

pairElem: FST expr
| SND expr
;

type: baseType
| type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET
| pairType
;

baseType: INT
| BOOL
| CHAR
| STRING
;

pairType: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES ;

pairElemType: baseType
| type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET
| PAIR
;

expr: (PLUS|MINUS)? INT_LIT
| BOOL_LIT
| CHAR_LIT
| STR_LIT
| pairLit
| IDENT
| arrayElem
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

arrayElem: IDENT (OPEN_SQUARE_BRACKET expr CLOSE_SQUARE_BRACKET)+ ;

arrayLit: OPEN_SQUARE_BRACKET (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKET ;

pairLit: NULL ;