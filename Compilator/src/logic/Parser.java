//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "gramatica.y"
	package logic;
	
	import java.util.Hashtable;
	import logic.Lexicon;
	import utils.ElementoTS;
	import utils.ParserVal;
	import utils.MsgStack;
	import utils.Token;
	import java.util.Stack;
	import utils.syntacticTree.SyntacticTree;
	import utils.syntacticTree.ST_Common;
	import utils.syntacticTree.ST_Convertion;
	import utils.syntacticTree.ST_Control;
	import utils.syntacticTree.ST_Leaf;
	import utils.syntacticTree.ST_LeafCollection;
	import utils.syntacticTree.ST_Memory;
	import utils.syntacticTree.ST_Unary;
//#line 35 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short BEGIN=257;
public final static short END=258;
public final static short ID=259;
public final static short CTE=260;
public final static short INT=261;
public final static short LONG=262;
public final static short IF=263;
public final static short ELSE=264;
public final static short END_IF=265;
public final static short FOREACH=266;
public final static short IN=267;
public final static short PRINT=268;
public final static short ASSIGN=269;
public final static short LET=270;
public final static short GET=271;
public final static short EQ=272;
public final static short DIF=273;
public final static short CAD=274;
public final static short SIZE=275;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    1,    2,    2,    4,    4,    4,    4,
    5,    5,    6,    6,    7,    7,    7,    7,    8,    8,
    8,    8,    3,   11,   11,   10,   12,   12,   12,   12,
   12,   12,   13,   13,   20,   21,   18,   19,   23,   23,
   23,   23,   23,   23,   14,   25,   25,   24,   15,   26,
   26,   26,   27,   27,   27,   28,   28,   28,   22,   22,
   22,   22,    9,    9,   16,   29,   17,   17,   17,   17,
   17,   17,   17,   17,   17,   17,   17,   17,   17,   17,
   17,
};
final static short yylen[] = {                            2,
    1,    2,    1,    1,    1,    2,    3,    3,    1,    2,
    1,    1,    1,    3,    4,    6,    6,    8,    1,    1,
    3,    3,    4,    2,    1,    1,    2,    1,    2,    2,
    2,    2,    6,    8,    1,    1,    1,    3,    1,    1,
    1,    1,    1,    1,    4,    1,    1,    2,    3,    1,
    4,    4,    3,    3,    1,    3,    3,    1,    1,    1,
    4,    4,    1,    2,    4,    1,    2,    2,    5,    3,
    4,    4,    2,    3,    3,    4,    2,    2,    2,    5,
    2,
};
final static short yydefred[] = {                         0,
    0,   26,   11,   12,    0,    1,    0,    4,    5,    0,
    0,   10,    2,    6,    0,    0,    0,    0,    0,    0,
   37,    0,   66,   41,   42,   43,   44,    0,   39,   40,
    0,    0,    0,    0,    0,   28,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    7,    0,    8,    0,   32,
   78,    0,    0,   77,   48,    0,   73,   79,   67,    0,
   24,   27,   29,   30,   31,    0,    0,   63,   81,    0,
   60,   68,    0,    0,    0,    0,    0,   20,    0,   19,
   14,    0,   75,    0,    0,   74,   23,    0,    0,    0,
    0,   64,    0,    0,   70,   58,    0,    0,    0,    0,
    0,   15,    0,   52,    0,   76,   51,    0,    0,    0,
    0,    0,   71,    0,    0,   47,   45,    0,    0,    0,
    0,   65,    0,   22,   21,    0,    0,   80,   69,   35,
    0,   38,   62,   61,    0,    0,   56,   57,   17,    0,
   16,    0,   33,    0,   36,    0,   18,   34,
};
final static short yydgoto[] = {                          5,
    6,    7,    8,    9,   31,   16,   17,   79,   71,   11,
   33,   34,   35,   36,   37,   38,   39,   40,   89,  131,
  146,   96,   41,   42,  117,   43,   97,   98,   44,
};
final static short yysindex[] = {                      -179,
  -34,    0,    0,    0,    0,    0, -179,    0,    0, -230,
   26,    0,    0,    0,  -56,   -2,    2,   -5,  -78, -225,
    0, -193,    0,    0,    0,    0,    0,  -77,    0,    0,
 -191, -187, -171,   26,   32,    0,   35,   43,   48,   34,
  -33, -231, -165,   70,  -45,    0, -148,    0, -147,    0,
    0,   21, -175,    0,    0,   23,    0,    0,    0,   56,
    0,    0,    0,    0,    0,  -31,   27,    0,    0, -140,
    0,    0, -146, -199,  -38, -152,   65,    0,  -17,    0,
    0,   33,    0,   36,  -82,    0,    0, -132,   86,   30,
 -163,    0, -132,   26,    0,    0,   22,  -23,   87, -130,
  -36,    0,  -40,    0,   38,    0,    0, -133,   26,  -38,
   40,   41,    0,    0,    8,    0,    0,  -38,  -38,  -38,
  -38,    0,   42,    0,    0,   78,  -14,    0,    0,    0,
 -184,    0,    0,    0,  -23,  -23,    0,    0,    0, -123,
    0,   26,    0,   46,    0, -127,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,  140,    0,    0,    1,
    0,    0,    0,    0,    5,    0,    0,    0, -128,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -202,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -39,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   83,  -12,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -19,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -11,   -6,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,    0,   15,  136,   45,    0,    0,   44,  -28,    4,
  -16,   50,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -15,   55,    0,    0,    0,    0,  -18,    0,
};
final static int YYTABLESIZE=303;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         70,
    9,   59,   59,   59,   70,   59,   70,   59,   70,   88,
  107,   70,   53,   70,   32,   57,   80,   61,  120,   59,
   59,   13,   59,  121,   12,   72,  101,   73,   15,  101,
   55,   54,   55,   54,   45,   74,   53,   32,   53,   72,
   46,   47,   46,   54,   10,   49,   55,   54,   13,   78,
   90,   10,   53,   50,   78,   25,   46,    2,  124,   94,
   48,   25,   25,   13,  119,   55,  118,   29,   58,   30,
   59,   46,  125,   66,   80,  102,    1,    2,  141,  142,
  143,    3,    4,   84,   85,   29,   60,   30,   95,   29,
   62,   30,  130,   63,  132,  111,  112,  115,   28,  135,
  136,   64,  108,   75,  137,  138,   65,  113,  114,   76,
   81,   82,   32,   83,   87,   86,   28,   91,   32,   92,
   93,   99,  100,  103,    2,  145,  109,  122,  104,  123,
  128,  129,  133,  134,  139,  140,  144,  148,  147,    3,
   50,   49,   14,  116,  110,   32,  127,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  105,    0,    0,
   51,   52,   56,    0,    0,    0,  106,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   68,    0,    0,    0,    0,   68,
   67,   68,    0,   68,    0,   67,   68,   67,   68,   77,
   59,   59,   59,   59,  126,   69,   46,   46,   46,   46,
   46,   46,   46,   46,   46,   46,   46,    0,   46,    0,
   46,   46,   46,   46,    0,    0,    9,    9,    0,    0,
    0,    9,    9,   18,    2,   59,   19,   20,    3,    4,
   21,    0,    0,   22,    0,   23,    0,   24,   25,   26,
   27,   18,    2,    0,   19,   20,    3,    4,   21,    0,
    0,   22,    0,   23,    0,   24,   25,   26,   27,   24,
   25,   26,   27,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         45,
    0,   41,   42,   43,   45,   45,   45,   47,   45,   41,
   93,   45,   91,   45,   11,   93,   45,   34,   42,   59,
   60,    7,   62,   47,   59,   41,   44,  259,  259,   44,
   43,   43,   45,   45,   91,  267,   43,   34,   45,   59,
   60,   44,   62,  269,    0,   44,   59,   59,   44,   95,
   66,    7,   59,   59,   95,  258,   59,  257,   95,  259,
   59,  264,  265,   59,   43,  259,   45,   60,  260,   62,
  258,   91,  101,   40,  103,   93,  256,  257,   93,  264,
  265,  261,  262,  259,  260,   60,  258,   62,   74,   60,
   59,   62,  109,   59,  110,  259,  260,   94,   91,  118,
  119,   59,   88,  269,  120,  121,   59,   93,   94,   40,
  259,  259,  109,   93,   59,   93,   91,   91,  115,  260,
  267,  274,   58,   91,  257,  142,   41,   41,   93,  260,
   93,  265,   93,   93,   93,   58,  260,  265,   93,    0,
  269,   59,    7,   94,   90,  142,  103,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  260,   -1,   -1,
  259,  260,  260,   -1,   -1,   -1,  269,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  260,   -1,   -1,   -1,   -1,  260,
  259,  260,   -1,  260,   -1,  259,  260,  259,  260,  275,
  270,  271,  272,  273,  275,  269,  256,  257,  258,  259,
  260,  261,  262,  263,  264,  265,  266,   -1,  268,   -1,
  270,  271,  272,  273,   -1,   -1,  256,  257,   -1,   -1,
   -1,  261,  262,  256,  257,  258,  259,  260,  261,  262,
  263,   -1,   -1,  266,   -1,  268,   -1,  270,  271,  272,
  273,  256,  257,   -1,  259,  260,  261,  262,  263,   -1,
   -1,  266,   -1,  268,   -1,  270,  271,  272,  273,  270,
  271,  272,  273,
};
}
final static short YYFINAL=5;
final static short YYMAXTOKEN=275;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,"':'","';'",
"'<'",null,"'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,"'_'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"BEGIN","END","ID","CTE","INT","LONG","IF",
"ELSE","END_IF","FOREACH","IN","PRINT","ASSIGN","LET","GET","EQ","DIF","CAD",
"SIZE",
};
final static String yyrule[] = {
"$accept : programa_completo",
"programa_completo : programa",
"programa : bloque_decl bloque_ejec",
"programa : bloque_decl",
"programa : bloque_ejec",
"bloque_decl : sent_decl",
"bloque_decl : bloque_decl sent_decl",
"sent_decl : comienzo_decl lista_vars ';'",
"sent_decl : comienzo_decl lista_colecciones ';'",
"sent_decl : comienzo_decl",
"sent_decl : error ';'",
"comienzo_decl : INT",
"comienzo_decl : LONG",
"lista_vars : ID",
"lista_vars : lista_vars ',' ID",
"lista_colecciones : ID '[' lista_valores_inic ']'",
"lista_colecciones : lista_colecciones ',' ID '[' lista_valores_inic ']'",
"lista_colecciones : ID '[' SIZE ':' CTE ']'",
"lista_colecciones : lista_colecciones ',' ID '[' SIZE ':' CTE ']'",
"lista_valores_inic : constante_controlada",
"lista_valores_inic : '_'",
"lista_valores_inic : lista_valores_inic ',' constante_controlada",
"lista_valores_inic : lista_valores_inic ',' '_'",
"bloque_ejec : comienzo_bloque conj_sent END ';'",
"conj_sent : sent_ejec conj_sent",
"conj_sent : sent_ejec",
"comienzo_bloque : BEGIN",
"sent_ejec : sent_cond ';'",
"sent_ejec : sent_ctrl",
"sent_ejec : sent_asig ';'",
"sent_ejec : sent_print ';'",
"sent_ejec : error_p ';'",
"sent_ejec : error ';'",
"sent_cond : comienzo_if '(' cond_if ')' bloque_then END_IF",
"sent_cond : comienzo_if '(' cond_if ')' bloque_then ELSE bloque_else END_IF",
"bloque_then : conj_sent",
"bloque_else : conj_sent",
"comienzo_if : IF",
"cond_if : factor comparador factor",
"comparador : '<'",
"comparador : '>'",
"comparador : LET",
"comparador : GET",
"comparador : EQ",
"comparador : DIF",
"sent_ctrl : comienzo_foreach IN ID bloque_foreach",
"bloque_foreach : bloque_ejec",
"bloque_foreach : sent_ejec",
"comienzo_foreach : FOREACH ID",
"sent_asig : inic_sent_asig ASSIGN expression",
"inic_sent_asig : ID",
"inic_sent_asig : ID '[' CTE ']'",
"inic_sent_asig : ID '[' ID ']'",
"expression : expression '+' term",
"expression : expression '-' term",
"expression : term",
"term : term '*' factor",
"term : term '/' factor",
"term : factor",
"factor : ID",
"factor : constante_controlada",
"factor : ID '[' CTE ']'",
"factor : ID '[' ID ']'",
"constante_controlada : CTE",
"constante_controlada : '-' CTE",
"sent_print : comienzo_print '(' CAD ')'",
"comienzo_print : PRINT",
"error_p : comienzo_bloque END",
"error_p : comparador factor",
"error_p : comienzo_if '(' ')' bloque_ejec END_IF",
"error_p : comienzo_foreach IN bloque_ejec",
"error_p : comienzo_foreach ID IN bloque_ejec",
"error_p : comienzo_foreach IN ID bloque_ejec",
"error_p : '[' ']'",
"error_p : '[' CTE ']'",
"error_p : ID CTE ']'",
"error_p : ID '[' CTE ASSIGN",
"error_p : CTE ASSIGN",
"error_p : ID ID",
"error_p : comienzo_decl CTE",
"error_p : ID '[' CTE CTE ']'",
"error_p : comparador ASSIGN",
};

//#line 595 "gramatica.y"

private Lexicon lex;
private Hashtable<String,ElementoTS> symbolTable;
private MsgStack msgStack; 							// Stack de mensajes de error y warning
private MsgStack semanticStructStack;
Stack<Integer> stackOfLines = new Stack<Integer>(); // Stack para guardar los números de línea (para anidamiento de for/if)
private int numLineaDecl; 							// Guarda el número de líneal del comienzo de declaración
private int countSize; 								// Cuenta cantidad de elementos inicializados en el arreglo
private String elems; 								// Guarda los elementos iniciales de la colección
private String tipovar; 							// Almacena el tipo de la variable/colección para su seteo posterior en TS
private String expectedVarType; 					// Para control de tipos en la declaración
private ST_Control nodoRaiz; 						// Raiz del árbol sintáctico
private boolean hasError;

public Parser(Lexicon lex, Hashtable<String, ElementoTS> symbolTable, MsgStack msgStack, MsgStack semanticStructStack){
	this.lex = lex;
	this.symbolTable = symbolTable;
	this.msgStack = msgStack;
	this.semanticStructStack = semanticStructStack;
	countSize = 0;
	hasError = false;
	expectedVarType = "";
}

private int yylex(){
	Token token = lex.getNewToken();
	String lexeme = token.getLexeme();
	if (lexeme != null)
		yylval = new ParserVal(lexeme);
	return token.getTokenValue();
}

private void yyerror(String msg){
	msgStack.addMsg("Línea "+ lex.getNewLineCounter() + ": " + msg);
}

public ST_Control getRaiz(){
	return nodoRaiz;
}

public boolean hasErrors() { return hasError; }
//#line 432 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 28 "gramatica.y"
{ nodoRaiz = new ST_Control("PROGRAMA",(SyntacticTree)val_peek(0)); }
break;
case 2:
//#line 31 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 3:
//#line 32 "gramatica.y"
{ yyval = new ST_Common("SENTENCIA", null, null); msgStack.addMsg("Warning: Programa sin codigo ejecutable"); }
break;
case 4:
//#line 33 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 7:
//#line 40 "gramatica.y"
{ semanticStructStack.addMsg("Linea "+ numLineaDecl +": Sentencia declarativa"); }
break;
case 8:
//#line 41 "gramatica.y"
{ 
				semanticStructStack.addMsg("Linea "+ numLineaDecl +": Sentencia declarativa");
				if (expectedVarType != "" && tipovar == ElementoTS.INT && expectedVarType == ElementoTS.LONG){
					 msgStack.addMsg("Error: incompatibilidad de tipos en la declaración de la línea: " + lex.getNewLineCounter());
					 hasError = true;
				}
				expectedVarType = "";
			  }
break;
case 10:
//#line 50 "gramatica.y"
{ msgStack.addMsg("Error genérico en el bloque declarativo no contemplado cerca de línea " + lex.getNewLineCounter()); }
break;
case 11:
//#line 53 "gramatica.y"
{tipovar = ElementoTS.INT; numLineaDecl = lex.getNewLineCounter(); }
break;
case 12:
//#line 54 "gramatica.y"
{tipovar = ElementoTS.LONG; numLineaDecl = lex.getNewLineCounter(); }
break;
case 13:
//#line 57 "gramatica.y"
{
					if(!symbolTable.get(val_peek(0).sval).isDeclared()){
						symbolTable.get(val_peek(0).sval).setDeclared();
						symbolTable.get(val_peek(0).sval).setVariableType(tipovar);
						symbolTable.get(val_peek(0).sval).setIdentifierClass(ElementoTS.VAR);
					} else {
						msgStack.addMsg("Error: variable "+ val_peek(0).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
						hasError = true;
					}
				}
break;
case 14:
//#line 67 "gramatica.y"
{
					if(!symbolTable.get(val_peek(0).sval).isDeclared()){
						symbolTable.get(val_peek(0).sval).setDeclared();
						symbolTable.get(val_peek(0).sval).setVariableType(tipovar);
						symbolTable.get(val_peek(0).sval).setIdentifierClass(ElementoTS.VAR);
					} else {
						msgStack.addMsg("Error: variable "+ val_peek(0).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
						hasError = true;
					}
				}
break;
case 15:
//#line 79 "gramatica.y"
{
				if(!symbolTable.get(val_peek(3).sval).isDeclared()) {
					symbolTable.get(val_peek(3).sval).setDeclared();
					symbolTable.get(val_peek(3).sval).setCSize(countSize);
					symbolTable.get(val_peek(3).sval).setElemsCollection(elems);
					symbolTable.get(val_peek(3).sval).setVariableType(tipovar);
					symbolTable.get(val_peek(3).sval).setIdentifierClass(ElementoTS.COL);
					elems = "";
					countSize = 0;
				} else {
					msgStack.addMsg("Error: colección "+ val_peek(3).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
					hasError = true;
				}
			}
break;
case 16:
//#line 93 "gramatica.y"
{
				if(!symbolTable.get(val_peek(3).sval).isDeclared()) {
					symbolTable.get(val_peek(3).sval).setDeclared();
					symbolTable.get(val_peek(3).sval).setCSize(countSize);
					symbolTable.get(val_peek(3).sval).setElemsCollection(elems);
					symbolTable.get(val_peek(3).sval).setVariableType(tipovar);
					symbolTable.get(val_peek(3).sval).setIdentifierClass(ElementoTS.COL);
					elems = "";
					countSize = 0;
				} else {
					msgStack.addMsg("Error: colección "+ val_peek(3).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
					hasError = true;
				}
			}
break;
case 17:
//#line 107 "gramatica.y"
{
				if(!symbolTable.get(val_peek(5).sval).isDeclared()) {
					symbolTable.get(val_peek(5).sval).setDeclared();
					int size = Integer.parseInt(val_peek(1).sval);
					symbolTable.get(val_peek(5).sval).setCSize(size);
					for (int i = 0; i < size - 1; i++) {
						elems += "_,";
					}
					elems += "_";
					symbolTable.get(val_peek(5).sval).setElemsCollection(elems);
					elems = "";
					symbolTable.get(val_peek(5).sval).setVariableType(tipovar);
				} else {
					msgStack.addMsg("Error: colección"+ val_peek(5).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
					hasError = true;
				}
			}
break;
case 18:
//#line 124 "gramatica.y"
{
				if(!symbolTable.get(val_peek(5).sval).isDeclared()){
					symbolTable.get(val_peek(5).sval).setDeclared();
					int size = Integer.parseInt(val_peek(1).sval);
					symbolTable.get(val_peek(5).sval).setCSize(size);
					for (int i = 0; i < size - 1; i++) {
						elems += "_,";
					}
					elems += "_";
					symbolTable.get(val_peek(5).sval).setElemsCollection(elems);
					elems = "";
					symbolTable.get(val_peek(5).sval).setVariableType(tipovar);
				} else {
					msgStack.addMsg("Error: colección "+ val_peek(5).sval+" re-declarada en el número de línea " + lex.getNewLineCounter());
					hasError = true;
				}
			}
break;
case 19:
//#line 143 "gramatica.y"
{ elems = ((SyntacticTree)val_peek(0)).getElem(); countSize++; }
break;
case 20:
//#line 144 "gramatica.y"
{ elems= "_"; countSize++;}
break;
case 21:
//#line 145 "gramatica.y"
{ elems = elems + "," + ((SyntacticTree)val_peek(0)).getElem(); countSize++; }
break;
case 22:
//#line 146 "gramatica.y"
{ elems = elems + ",_"; countSize++; }
break;
case 23:
//#line 149 "gramatica.y"
{ yyval = val_peek(2); semanticStructStack.addMsg("Línea "+ stackOfLines.peek().intValue() +": Bloque de ejecución"); }
break;
case 24:
//#line 152 "gramatica.y"
{ yyval = new ST_Common("SENTENCIA", (SyntacticTree)val_peek(1), (SyntacticTree)val_peek(0));	}
break;
case 25:
//#line 153 "gramatica.y"
{ yyval = new ST_Common("SENTENCIA", (SyntacticTree)val_peek(0), null); }
break;
case 26:
//#line 156 "gramatica.y"
{ stackOfLines.push(lex.getNewLineCounter()); }
break;
case 27:
//#line 159 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 28:
//#line 160 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 29:
//#line 161 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 30:
//#line 162 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 31:
//#line 163 "gramatica.y"
{
				yyval = new ST_Common("ERROR", null, null);
				/* Hay un warning en error_p, por eso hasError se setea true abajo y no acá */
			}
break;
case 32:
//#line 167 "gramatica.y"
{ 
				msgStack.addMsg("Error genérico no contemplado cerca de línea " + lex.getNewLineCounter());
				hasError = true;
				yyval = new ST_Common("ERROR", null, null);
			}
break;
case 33:
//#line 174 "gramatica.y"
{
                    semanticStructStack.addMsg("L?nea " + stackOfLines.peek().intValue() + ": Sentencia IF ");
                    stackOfLines.pop();
					ST_Control nodothen = new ST_Control("THEN", (SyntacticTree)val_peek(1));
					yyval = new ST_Common("IF",(SyntacticTree)val_peek(3),nodothen);
                }
break;
case 34:
//#line 180 "gramatica.y"
{
                    semanticStructStack.addMsg("L?nea " + stackOfLines.peek().intValue() + ": Sentencia IF ");
                    stackOfLines.pop();
					ST_Control nodothen = new ST_Control("THEN_ELSE", (SyntacticTree)val_peek(3));
					ST_Control nodoelse = new ST_Control("ELSE", (SyntacticTree)val_peek(1));
					SyntacticTree nodoCuerpo = new ST_Common("CUERPO",nodothen,nodoelse);
					yyval = new ST_Common("IF",(SyntacticTree)val_peek(5),nodoCuerpo);
                }
break;
case 35:
//#line 192 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 36:
//#line 195 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 37:
//#line 198 "gramatica.y"
{ stackOfLines.push(lex.getNewLineCounter()); }
break;
case 38:
//#line 201 "gramatica.y"
{
			SyntacticTree nodoCond;
			if (((SyntacticTree)val_peek(2)).getType() != ((SyntacticTree)val_peek(0)).getType()){
				hasError = true;
				msgStack.addMsg("Error: Incompatibilidad de tipos entre las variables utilizadas en línea: " + lex.getNewLineCounter());
				nodoCond = new ST_Common("ERROR", null, null);
			} else
				nodoCond = new ST_Common(val_peek(1).sval, (SyntacticTree)val_peek(2), (SyntacticTree)val_peek(0));
			SyntacticTree nodoCondCtrol = new ST_Control("COND_IF",nodoCond);
			yyval = nodoCondCtrol; }
break;
case 39:
//#line 213 "gramatica.y"
{ val_peek(0).sval = "<"; yyval = val_peek(0); }
break;
case 40:
//#line 214 "gramatica.y"
{ val_peek(0).sval = ">"; yyval = val_peek(0); }
break;
case 41:
//#line 215 "gramatica.y"
{ val_peek(0).sval = "LET"; yyval = val_peek(0); }
break;
case 42:
//#line 216 "gramatica.y"
{ val_peek(0).sval = "GET"; yyval = val_peek(0); }
break;
case 43:
//#line 217 "gramatica.y"
{ val_peek(0).sval = "EQ"; yyval = val_peek(0); }
break;
case 44:
//#line 218 "gramatica.y"
{ val_peek(0).sval = "DIF"; yyval = val_peek(0); }
break;
case 45:
//#line 221 "gramatica.y"
{ 
		ST_Common nodo = null;
        semanticStructStack.addMsg("Linea "+ stackOfLines.peek().intValue() + ": Sentencia foreach");
        stackOfLines.pop();
		if(!(symbolTable.containsKey(val_peek(2).sval) && symbolTable.get(val_peek(1).sval).isDeclared())) {
			msgStack.addMsg("Error: Colección  " + val_peek(1).sval + " no declarada.");
			hasError = true;
		}
		if (hasError) /*Por si detecto error en comienzo_foreach*/
			nodo = new ST_Common("ERROR", null, null);
		else {
			if (!(symbolTable.get(val_peek(3).sval).getVariableType().equals(symbolTable.get(val_peek(1).sval).getVariableType()))) {
				msgStack.addMsg("Error: Tipos distintos en línea: " + lex.getNewLineCounter());
				hasError = true;
				nodo = new ST_Common("ERROR", null, null);
			} else {
				SyntacticTree nodoAux1 = new ST_Leaf("_" + val_peek(3).sval);
				nodoAux1.setAlmacenamiento(nodoAux1.getElem());
				SyntacticTree nodoMax1 = new ST_Leaf("_" + val_peek(1).sval);
				nodoMax1.setAlmacenamiento(nodoMax1.getElem());
				SyntacticTree nodoInic = new ST_Memory("::=",nodoAux1,nodoMax1);
				SyntacticTree nodoAux2 = new ST_Leaf("_" + val_peek(3).sval);
				nodoAux2.setAlmacenamiento(nodoAux2.getElem());
				SyntacticTree nodoMax2 = new ST_Leaf("_" + val_peek(1).sval);
				nodoMax2.setAlmacenamiento(nodoMax2.getElem());
				SyntacticTree nodoComp = new ST_Memory("<<",nodoAux2,nodoMax2);
				SyntacticTree nodoCond = new ST_Control("COND_FOREACH",nodoComp);
				SyntacticTree nodoBucle = new ST_Common("BUCLE", nodoInic,nodoCond);
				SyntacticTree nodoAux3 = new ST_Leaf("_" + val_peek(3).sval);
				nodoAux3.setAlmacenamiento(nodoAux3.getElem());
				SyntacticTree nodoMax3 = new ST_Leaf("_" + val_peek(1).sval);
				nodoMax3.setAlmacenamiento(nodoMax3.getElem());
				nodoMax3.setType(symbolTable.get(val_peek(1).sval).getVariableType());
				SyntacticTree nodoSuma = new ST_Memory("+=", nodoAux3,nodoMax3);
				SyntacticTree nodoCuerpo = new ST_Common("CUERPO", (SyntacticTree)val_peek(0), nodoSuma);
				SyntacticTree nodoCuerpoAvance = new ST_Control("CUERPOAVANCE", nodoCuerpo);
				nodo = new ST_Common("FOREACH", nodoBucle,nodoCuerpoAvance);				
			}
		}
		symbolTable.get(val_peek(3).sval).foreachControlOff();
		yyval = nodo;
	}
break;
case 46:
//#line 265 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 47:
//#line 266 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 48:
//#line 269 "gramatica.y"
{ 
					stackOfLines.push(lex.getNewLineCounter());
					if (symbolTable.containsKey(val_peek(0).sval) && symbolTable.get(val_peek(0).sval).isDeclared()) {
						symbolTable.get(val_peek(0).sval).foreachControlOn();
						symbolTable.get(val_peek(0).sval).setHasPointer();
					} else {
						msgStack.addMsg("Error: Variable " + val_peek(0).sval + " no declarada.");
						hasError = true;
					}
					yyval = val_peek(0);
				}
break;
case 49:
//#line 282 "gramatica.y"
{
				SyntacticTree nodo = null;
				semanticStructStack.addMsg("Linea "+ stackOfLines.peek().intValue() + ": Asignación"); 
				stackOfLines.pop();
				if(((SyntacticTree) val_peek(0)).getType() == ElementoTS.LONG && ((SyntacticTree) val_peek(2)).getType().equals(ElementoTS.INT)){
					msgStack.addMsg("Error: incompatibilidad de tipos en línea: " + lex.getNewLineCounter());
					hasError = true;
					nodo = new ST_Common("ERROR", null, null);
				} else {
					boolean flag = false;
					String subIndexName = ((SyntacticTree) val_peek(2)).getElem();
					if (subIndexName.contains("[")) {
						int subIndexNameStart = 0;
						while (subIndexName.charAt(subIndexNameStart) != '[')
							subIndexNameStart++;
						subIndexNameStart++; /* Salta el corchete inicial*/
						subIndexName = subIndexName.substring(subIndexNameStart + 1, subIndexName.length() - 1);
						if (symbolTable.containsKey(subIndexName)) /* Si no está en la TS es porque era una constante*/
							flag = (symbolTable.get(subIndexName).isPointer() && ((SyntacticTree) val_peek(0)).getType().equals(ElementoTS.INT));
					}
					if (((SyntacticTree) val_peek(2)).getType() == ElementoTS.LONG && ((SyntacticTree) val_peek(0)).getType().equals(ElementoTS.INT)
							|| flag){
						SyntacticTree nodoConv = new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(0));
						nodo = new ST_Common(":=",(SyntacticTree)val_peek(2),nodoConv);
					}else
						nodo = new ST_Common(":=",(SyntacticTree)val_peek(2),(SyntacticTree)val_peek(0));
					nodo.setType( ((SyntacticTree) val_peek(2)).getType());
				}
				yyval = nodo;
			}
break;
case 50:
//#line 314 "gramatica.y"
{ 
						SyntacticTree nodo = null;
						stackOfLines.push(lex.getNewLineCounter()); 
						if(!symbolTable.get(val_peek(0).sval).isDeclared()){
							msgStack.addMsg("Error: Variable no declarada en línea: " + lex.getNewLineCounter());
							hasError = true;
							nodo = new ST_Leaf("ERROR");
						}else {
							if (symbolTable.get(val_peek(0).sval).foreachControlState() && symbolTable.get(val_peek(0).sval).isPointer()){
								msgStack.addMsg("Error: No se puede asignar a " + val_peek(0).sval + " dentro de foreach.");
								hasError = true;
								nodo = new ST_Leaf("ERROR");
							} else {
								nodo = new ST_Leaf("_" + val_peek(0).sval);
								nodo.setType(symbolTable.get(val_peek(0).sval).getVariableType());
								nodo.setAlmacenamiento(nodo.getElem());
								((ST_Leaf)nodo).setLeftSideAssignment();
							}
						}
						yyval = nodo;
					}
break;
case 51:
//#line 335 "gramatica.y"
{ 
				   		SyntacticTree nodo = null;
						stackOfLines.push(lex.getNewLineCounter());
						if(!symbolTable.get(val_peek(3).sval).isDeclared()) {
							msgStack.addMsg("Error: Variable no declarada en línea: " + lex.getNewLineCounter());
							hasError = true;
							nodo  = new ST_Leaf("ERROR");
						}else{
							long pos = Long.valueOf(val_peek(1).sval);
							if((long)symbolTable.get(val_peek(3).sval).getCSize() <= pos){
								msgStack.addMsg("Error: Posición de colección superada en línea: " + lex.getNewLineCounter());
								hasError = true;
								nodo  = new ST_Leaf("ERROR");
							}else{
								nodo = new ST_Leaf("_" + val_peek(3).sval + "[" + val_peek(1).sval + "]");
								nodo.setType(symbolTable.get(val_peek(3).sval).getVariableType());
								nodo.setAlmacenamiento(nodo.getElem());
								((ST_Leaf)nodo).setLeftSideAssignment();
							}
						}
						yyval = nodo;
					}
break;
case 52:
//#line 357 "gramatica.y"
{
						SyntacticTree nodo = null;
						stackOfLines.push(lex.getNewLineCounter());
						if(!symbolTable.get(val_peek(3).sval).isDeclared() || !symbolTable.get(val_peek(1).sval).isDeclared()) {
							msgStack.addMsg("Error: Una o ambas variables no fueron declaradas en línea: " + lex.getNewLineCounter());
							hasError = true;
							nodo  = new ST_Leaf("ERROR");
						}else {
							if (symbolTable.get(val_peek(1).sval).getIdentifierClass().equals(ElementoTS.VAR)) {
								if (symbolTable.get(val_peek(1).sval).foreachControlState()) {
									ST_Leaf nodoHoja = new ST_Leaf("_" + val_peek(1).sval);
									nodoHoja.setAlmacenamiento(nodoHoja.getElem());
									ST_Convertion nodoAux = new ST_Convertion(ST_Convertion.PTOV, nodoHoja);
									nodo = new ST_LeafCollection("_" + val_peek(3).sval + "[" + "_" + val_peek(1).sval + "]", nodoAux);
								} else {
									nodo  = new ST_Leaf("_" + val_peek(3).sval + "[" + "_" + val_peek(1).sval + "]");
								}
								nodo.setType(symbolTable.get(val_peek(3).sval).getVariableType());
								nodo.setAlmacenamiento(nodo.getElem());
								((ST_Leaf)nodo).setLeftSideAssignment();
							} else{
								msgStack.addMsg("Error: El subíndice es distinto a una variable o constante en línea: " + lex.getNewLineCounter());
								hasError = true;
								nodo  = new ST_Leaf("ERROR");
							}
						}
						yyval = nodo;
					}
break;
case 53:
//#line 387 "gramatica.y"
{
					SyntacticTree nodo = null;
					if(((SyntacticTree) val_peek(2)).getType() != ((SyntacticTree) val_peek(0)).getType()) {
						if(((SyntacticTree) val_peek(2)).getType().equals(ElementoTS.INT))
							nodo = new ST_Common("+",new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(2)), (SyntacticTree)val_peek(0));
						else
							nodo = new ST_Common("+",(SyntacticTree)val_peek(2),new ST_Convertion(ST_Convertion.ITOL, (SyntacticTree)val_peek(0)));
						nodo.setType(ElementoTS.LONG);
					} else {
						nodo = new ST_Common("+",(SyntacticTree)val_peek(2),(SyntacticTree)val_peek(0));
						nodo.setType(((SyntacticTree) val_peek(2)).getType());
					}
					yyval = nodo;
				}
break;
case 54:
//#line 401 "gramatica.y"
{
					SyntacticTree nodo = null;
					if(((SyntacticTree) val_peek(2)).getType() != ((SyntacticTree) val_peek(0)).getType()) {
						if(((SyntacticTree) val_peek(2)).getType().equals(ElementoTS.INT))
							nodo = new ST_Common("-",new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(2)),(SyntacticTree)val_peek(0));
						else
							nodo = new ST_Common("-",(SyntacticTree)val_peek(2),new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(0)));
						nodo.setType(ElementoTS.LONG);
					} else {
						nodo = new ST_Common("-",(SyntacticTree)val_peek(2),(SyntacticTree)val_peek(0));
						nodo.setType(((SyntacticTree) val_peek(2)).getType());
					}
					yyval = nodo;
				}
break;
case 55:
//#line 415 "gramatica.y"
{ yyval =val_peek(0); }
break;
case 56:
//#line 418 "gramatica.y"
{
			SyntacticTree nodo = null;
			if(((SyntacticTree) val_peek(2)).getType() != ((SyntacticTree) val_peek(0)).getType()) {
				if(((SyntacticTree) val_peek(2)).getType().equals(ElementoTS.INT))
					nodo = new ST_Common("*",new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(2)),(SyntacticTree)val_peek(0));
				else
					nodo = new ST_Common("*",(SyntacticTree)val_peek(2),new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(0)));
			} else {
				nodo = new ST_Common("*",(SyntacticTree)val_peek(2),(SyntacticTree)val_peek(0));
			}
			nodo.setType(ElementoTS.LONG);
			yyval = nodo;
		}
break;
case 57:
//#line 431 "gramatica.y"
{
			SyntacticTree nodo = null;
			if(((SyntacticTree) val_peek(2)).getType() != ((SyntacticTree) val_peek(0)).getType()) {
				if(((SyntacticTree) val_peek(2)).getType().equals(ElementoTS.INT))
					nodo = new ST_Common("/", new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(2)),(SyntacticTree)val_peek(0));
				else
					nodo = new ST_Common("/", (SyntacticTree)val_peek(2),new ST_Convertion(ST_Convertion.ITOL,(SyntacticTree)val_peek(0)));
				nodo.setType(ElementoTS.LONG);
			}else{
				nodo = new ST_Common("/",(SyntacticTree)val_peek(2),(SyntacticTree)val_peek(0));
				nodo.setType(((SyntacticTree)val_peek(2)).getType());
			}
			yyval = nodo;
		}
break;
case 58:
//#line 445 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 59:
//#line 448 "gramatica.y"
{
				SyntacticTree nodo = null;
				if(!symbolTable.get(val_peek(0).sval).isDeclared()){
					msgStack.addMsg("Error: Variable no declarada en línea: " + lex.getNewLineCounter());
					hasError = true;
					nodo = new ST_Leaf("_");
				} else {
					if (symbolTable.get(val_peek(0).sval).foreachControlState()){
						ST_Leaf nodoHoja = new ST_Leaf("_" + val_peek(0).sval);
						nodoHoja.setAlmacenamiento(nodoHoja.getElem());
						ST_Convertion nodoAux = new ST_Convertion(ST_Convertion.PTOV, nodoHoja);
						nodo = new ST_LeafCollection("_" + val_peek(0).sval, nodoAux);
					} else
						nodo = new ST_Leaf("_" + val_peek(0).sval);
					nodo.setType(symbolTable.get(val_peek(0).sval).getVariableType());
					nodo.setAlmacenamiento(nodo.getElem());
				}
				yyval = nodo;
			}
break;
case 60:
//#line 467 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 61:
//#line 468 "gramatica.y"
{
					SyntacticTree nodo = null;
					if(!symbolTable.get(val_peek(3).sval).isDeclared()){
						msgStack.addMsg("Error: Variable no declarada en línea: " + lex.getNewLineCounter());
						hasError = true;
						nodo = new ST_Leaf("ERROR");
					}else{
						long pos = Integer.valueOf(val_peek(1).sval);
						if(symbolTable.get(val_peek(3).sval).getCSize() <= pos) {
							msgStack.addMsg("Error: Posicion de coleccion superada en línea: " + lex.getNewLineCounter());
							hasError = true;
							nodo = new ST_Leaf("ERROR");
						} else
							nodo = new ST_Leaf("_" + val_peek(3).sval + "[" + val_peek(1).sval + "]");
						nodo.setType(symbolTable.get(val_peek(3).sval).getVariableType());
						nodo.setAlmacenamiento(nodo.getElem());
					}
					yyval = nodo;
				}
break;
case 62:
//#line 487 "gramatica.y"
{
					SyntacticTree nodo = null;
					if(!symbolTable.get(val_peek(3).sval).isDeclared() || !symbolTable.get(val_peek(1).sval).isDeclared()){
						msgStack.addMsg("Error: Una o ambas variables no fueron declaradas en línea: " + lex.getNewLineCounter());
						hasError = true;
						nodo = new ST_Leaf("ERROR");
					}else{
						if (symbolTable.get(val_peek(1).sval).getIdentifierClass().equals(ElementoTS.VAR)) {
							if (symbolTable.get(val_peek(1).sval).foreachControlState()){
								ST_Leaf nodoHoja = new ST_Leaf("_" + val_peek(1).sval);
								nodoHoja.setAlmacenamiento(nodoHoja.getElem());
								ST_Convertion nodoAux = new ST_Convertion(ST_Convertion.PTOV, nodoHoja);
								nodo = new ST_LeafCollection("_" + val_peek(3).sval + "[" + "_" + val_peek(1).sval + "]", nodoAux);
							} else
								nodo = new ST_Leaf("_" + val_peek(3).sval + "[" + "_" + val_peek(1).sval + "]");
							nodo.setType(symbolTable.get(val_peek(3).sval).getVariableType());
							nodo.setAlmacenamiento(nodo.getElem());
						} else{
							msgStack.addMsg("Error: El subíndice es distinto a una variable o constante en línea: " + lex.getNewLineCounter());
							hasError = true;
							nodo = new ST_Leaf("ERROR");
						}
					}
					yyval = nodo;
				}
break;
case 63:
//#line 515 "gramatica.y"
{
							String lexeme = val_peek(0).sval;
							ElementoTS tts = symbolTable.get(lexeme);
							if(lexeme == "32768"){
								tts.setVariableType(ElementoTS.LONG);
							}
							if (lexeme == "2147483648"){
								String lexnuevo = "2147483647";
								val_peek(0).sval = lexnuevo;
								symbolTable.get(lexeme).decreaseVariableRepetitions();
								if(symbolTable.containsKey(lexnuevo)){
									symbolTable.get(lexnuevo).increaseVariableRepetitions();
								}else {
									ElementoTS ttsnuevo = new ElementoTS( ElementoTS.CONST, tts.getValue(), tts.getVariableType());
									symbolTable.put(lexnuevo, ttsnuevo);
								}
							lexeme = lexnuevo;
								msgStack.addMsg("Warning: Constante positiva fuera de rango.");
							}
							String currentType = symbolTable.get(lexeme).getVariableType();
							expectedVarType = (expectedVarType == "")? currentType : 
								(currentType == ElementoTS.LONG || expectedVarType == ElementoTS.LONG)? ElementoTS.LONG : ElementoTS.INT;
							SyntacticTree nodo = new ST_Leaf(lexeme);
							nodo.setType(currentType);
							nodo.setAlmacenamiento(nodo.getElem());
							yyval = nodo;
	        			}
break;
case 64:
//#line 542 "gramatica.y"
{
							String lexeme = val_peek(0).sval;
							ElementoTS tts = symbolTable.get(lexeme);
							String lexnuevo = "-" + lexeme;
							val_peek(0).sval = lexnuevo;
							if(!symbolTable.containsKey(lexnuevo)){
								ElementoTS ttsneg = new ElementoTS(ElementoTS.CONST, tts.getValue(), tts.getVariableType());
								symbolTable.put(lexnuevo, ttsneg);
							}else
								symbolTable.get(lexnuevo).increaseVariableRepetitions();
							if (tts.getVariableRepetitions() == 0)
								symbolTable.remove(lexeme);
							symbolTable.get(lexeme).decreaseVariableRepetitions();
							String currentType = symbolTable.get(lexeme).getVariableType();
							expectedVarType = (expectedVarType == "")? currentType : 
								(currentType == ElementoTS.LONG || expectedVarType == ElementoTS.LONG)? ElementoTS.LONG : ElementoTS.INT;
							SyntacticTree nodo = new ST_Leaf(lexnuevo);
							nodo.setType(currentType);
							nodo.setAlmacenamiento(nodo.getElem());
							yyval = nodo;
						}
break;
case 65:
//#line 564 "gramatica.y"
{
                semanticStructStack.addMsg("Linea "+ stackOfLines.peek().intValue() +": Sentencia print");
                stackOfLines.pop();
				ST_Leaf nodoCad = new ST_Leaf("_" + val_peek(1).sval);
				nodoCad.setType(symbolTable.get(val_peek(1).sval).getVariableType());
				nodoCad.setAlmacenamiento(nodoCad.getElem());
				yyval = new ST_Unary("PRINT", nodoCad);
            }
break;
case 66:
//#line 574 "gramatica.y"
{ stackOfLines.push(lex.getNewLineCounter()); }
break;
case 67:
//#line 577 "gramatica.y"
{ msgStack.addMsg("Warning: Declaración de bloque sin contenido"); }
break;
case 68:
//#line 578 "gramatica.y"
{ msgStack.addMsg("Error: Comparación incompleta en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 69:
//#line 579 "gramatica.y"
{ msgStack.addMsg("Error: Declaración de IF sin condición en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 70:
//#line 580 "gramatica.y"
{ msgStack.addMsg("Error: Declaracion de foreach sin índice y sin colección en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 71:
//#line 581 "gramatica.y"
{ msgStack.addMsg("Error: Declaracion de foreach sin colección en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 72:
//#line 582 "gramatica.y"
{ msgStack.addMsg("Error: Declaracion de foreach sin índice en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 73:
//#line 583 "gramatica.y"
{ msgStack.addMsg("Error: Nombre y valor de posicion de colección faltante en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 74:
//#line 584 "gramatica.y"
{ msgStack.addMsg("Error: Nombre de colección faltante en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 75:
//#line 585 "gramatica.y"
{ msgStack.addMsg("Error: Corchete de apertura faltante en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 76:
//#line 586 "gramatica.y"
{ msgStack.addMsg("Error: Corchete de cierre faltante en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 77:
//#line 587 "gramatica.y"
{ msgStack.addMsg("Error: Constante a la izquierda de una asignación en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 78:
//#line 588 "gramatica.y"
{ msgStack.addMsg("Error: Sentencia inválida en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 79:
//#line 589 "gramatica.y"
{ msgStack.addMsg("Error: Constantes declaradas con tipo en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 80:
//#line 590 "gramatica.y"
{ msgStack.addMsg("Error: Falta de separador entre valores constantes en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
case 81:
//#line 591 "gramatica.y"
{ msgStack.addMsg("Error: Sentencia de asignación inválida en línea: " + lex.getNewLineCounter()); hasError = true; }
break;
//#line 1316 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
