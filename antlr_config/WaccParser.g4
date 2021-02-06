parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
}

prog: BEGIN (func)* stat END EOF ;

func: type IDENT OPEN_PARENTHESES paramList? CLOSE_PARENTHESES IS stat END ;

paramList: param (COMMA param)* ;

param: type IDENT ;

stat: SKIP_STAT                   # statSkip
| type IDENT EQUALS assignRHS     # statDeclaration
| assignLHS EQUALS assignRHS      # statAssignLHS
| READ assignLHS                  # statAssignRHS
| FREE expr                       # statFree
| RETURN expr                     # statReturn
| EXIT expr                       # statExit
| PRINT expr                      # statPrint
| PRINTLN expr                    # statPrintln
| IF expr THEN stat ELSE stat FI  # statIf
| WHILE expr DO stat DONE         # statWhile
| BEGIN stat END                  # statBeginEnd
| stat SEMICOLON stat             # statSequential
;

assignLHS: IDENT
| arrayElem
| pairElem
;

assignRHS: expr
| arrayLit
| newPair
| pairElem
| funcCalL
;

newPair: NEW_PAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES ;

funcCalL: CALL IDENT OPEN_PARENTHESES argList? CLOSE_PARENTHESES ;

argList: expr (COMMA expr)* ;

pairElem: FST expr
| SND expr
;

type: baseType
| arrayType
| pairType
;

arrayType: (baseType | pairType) OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET ;

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

expr: intLit
| boolLit
| charLit
| strLit
| PAIR_LIT
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

intLit: (PLUS|MINUS)? INT_LIT ;

boolLit : BOOL_LIT ;

charLit: CHAR_LIT ;

strLit: STR_LIT ;


