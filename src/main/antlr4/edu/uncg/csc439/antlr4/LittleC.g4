grammar LittleC;

//=========== Lexeme patterns and tokens start here ==============

// Put your lexical analyzer rules here - the following rule is just
// so that there is some lexer rule in the initial grammar (otherwise
// ANTLR won't make a Lexer class)

WS: [ \t\r\n]+ -> skip ;
COMMENT: '//' ~[\r\n]* '\r'? '\n' -> skip ;

INT: 'int' ;
CHAR: 'char' ;
VOID: 'void' ;

IF: 'if' ;
ELSE: 'else' ;
WHILE: 'while' ;
FOR: 'for' ;
RETURN: 'return' ;
BREAK: 'break' ;

STATIC: 'static' ;
EXTERN: 'extern' ;

ASGN: '=' ;
ADD: '+' ;
SUB: '-' ;
MUL: '*' ;
DIV: '/' ;
MOD: '%' ;
INC: '++' ;
DEC: '--' ;
LEN: '#' ;

LT: '<' ;
LTE: '<=' ;
GT: '>' ;
GTE: '>=' ;
EQ: '==' ;
NEQ: '!=' ;

NOT: '!' ;
AND: '&&' ;
OR: '||' ;

LP: '(' ;
RP: ')' ;
LBC: '{' ;
RBC: '}' ;
LBK: '[' ;
RBK: ']' ;
SMC: ';' ;
DBQ: '"' ;
CMA: ',' ;

fragment RESTID: ('_' | LETTER | INTLIT)* ;
ID: '_' RESTID | LETTER RESTID ;

fragment LETTER: [a-zA-Z] ;
fragment NUMBER: [0-9] ;

fragment STRINGLIT_PART: ( ~["\\\r\n] | '\\' . )* '"' ;
STRINGLIT: '"' STRINGLIT_PART ;
fragment CHARLIT_PART: (LETTER | NUMBER | ~[\\] ) '\'' ;
CHARLIT: '\'' CHARLIT_PART ;
INTLIT: NUMBER+ ;

//=========== Grammar starts here ==============

/* Some Basic Items */

literal: INTLIT | CHARLIT | STRINGLIT ;

/* ========= Statements ========= */

// Parser rule 'program' defines a LittleC program
program: dclr_stmt+ ;

dclr_stmt: vrbl_dclr
         | array_dclr
         | func_dclr
         ;

stmts: stmt+ ;

stmt: vrbl_dclr
    | array_dclr
    | asgn_stmt SMC
    | array_insert SMC
    | func_call SMC
    | expr SMC
    | if_stmt
    | for_loop
    | while_loop
    | rtrn_stmt
    | brk_stmt
    ;

block: LBC RBC          # NewScope
     | LBC stmts RBC    # NewScope
     ;

asgn_stmt: <assoc_right> ID ASGN expr           # Assignment1
         | <assoc_right> ID ASGN asgn_stmt      # Assignment2
         | array_asgn                           # Assignment3
         | LP asgn_stmt RP                      # Assignment4
         ;

array_asgn: <assoc_right> ID ASGN STRINGLIT     # ArrayAsgn_Strlit
          ;

array_insert: ID LBK expr RBK ASGN expr         # ArrayInsertion
            ;

if_stmt: IF LP expr RP if_else_block (ELSE if_else_block)? ;

if_else_block: stmt       # NewIfScope1
             | block      # NewIfScope2
             ;

rtrn_stmt: RETURN SMC               # Return
         | RETURN ID SMC            # ReturnID
         | RETURN literal SMC       # ReturnLit
         | RETURN expr SMC          # ReturnExpr
         ;

brk_stmt: BREAK SMC         # Break
        ;

while_loop: WHILE LP expr RP block ;

for_loop: FOR LP asgn_stmt SMC expr SMC (expr | asgn_stmt) RP for_loop_block ;

for_loop_block: stmt        # ForLoopBlock1
              | block       # ForLoopBlock2
              ;

/* Variables Declarations, Array Declarations, and Function Declarations, Definitions, and Calls */

vrbl_type: INT | CHAR ;

array_type: INT LBK RBK | CHAR LBK RBK ;

func_type: VOID | vrbl_type | array_type ;

vrbl_dclr: vrbl_type ID SMC                  # vrblDclr
         | vrbl_type asgn_stmt SMC           # vrblDclrAndAsgn
         ;

array_dclr: vrbl_type ID LBK expr RBK SMC                   # arrayDclr
          | vrbl_type ID LBK expr RBK ASGN expr SMC         # arrayDclrAndAsgn1
          | vrbl_type ID LBK expr RBK ASGN STRINGLIT SMC    # arrayDclrAndAsgn2
          ;

func_call: ID LP atl_parameters? RP     # funcCall
         ;

atl_parameters: atl_parameter (CMA atl_parameter)* ;

atl_parameter: expr             # altParIsExpr
             | STRINGLIT        # atlParIsStringlit
             ;

func_dclr: func_type ID LP frm_parameters? RP SMC             # funcDclr
         | func_type ID LP frm_parameters? RP func_block      # funcDclrAndDefnt
         ;

func_block: block       # funcBlock
          ;

frm_parameters: frm_parameter (CMA frm_parameter)* ;

frm_parameter: vrbl_type ID             # frmParIsVrblType
             | array_type ID            # frmParIsArrayType
             ;

/* ========= Expressions and Operations ========= */

expr: oprt
    | LP expr RP
    ;

/* Binary Boolean, Comparison, and Math Operations */

oprt: bool_oprt
    | LP bool_oprt RP
    ;

bool_oprt: sub_bool                 # isSubBool
         | bool_oprt OR sub_bool    # OrOprt
         ;

sub_bool: cmp_elmt                  # isCmpElmt
        | sub_bool AND cmp_elmt     # AndOprt
        ;

cmp_elmt: cmp_oprt
        | LP cmp_oprt RP
        ;

cmp_oprt: sub_cmp                   # isSubCmp
        | cmp_oprt EQ sub_cmp       # IsEqualTo
        | cmp_oprt NEQ sub_cmp      # IsNotEqualTo
        ;

sub_cmp: math_elmt                  # isMathElmt
       | sub_cmp LT math_elmt       # LessThan
       | sub_cmp LTE math_elmt      # LessThanEqual
       | sub_cmp GT math_elmt       # GreaterThan
       | sub_cmp GTE math_elmt      # GreaterThanEqual
       ;

math_elmt: math_oprt
        | LP math_oprt RP
        ;

math_oprt: term                     # isTerm
         | math_oprt ADD term       # Addition
         | math_oprt SUB term       # Subtraction
         ;

term: factor                # isFactor
    | term MUL factor       # Multiply
    | term DIV factor       # Divide
    | term MOD factor       # Mod
    ;

factor: uni_elmt ;

uni_elmt: uni_oprt
        | LP uni_oprt RP
        ;

/* Unary Operators */

uni_oprt: elmt                  # isElmt
        | ADD uni_oprt          # PosOprnd
        | SUB uni_oprt          # NegOprnd
        | NOT uni_oprt          # NotOprnd
        | INC uni_oprt          # PreIncOprnd
        | DEC uni_oprt          # PreDecOprnd
        | uni_oprt INC          # PostIncOprnd
        | uni_oprt DEC          # PostDecOprnd
        | LP bool_oprt RP       # NestedOprt
        ;

elmt: ID                        # isID
    | INTLIT                    # isINTLIT
    | CHARLIT                   # isCHARLIT
    | func_call                 # isFuncCall
    | ar_len_oprt               # isArrayLength
    | ar_indexing               # isArrayIndexing
    ;

ar_len_oprt: LEN ID             # ArrayLength
           ;

ar_indexing: ID LBK expr RBK    # ArrayIndexing
           ;

