package logic;

import java.util.Hashtable;
import java.util.List;

import utils.MsgStack;
import utils.StateAndSemAction;
import utils.Token;
import utils.semanticActions.SA_AddChar;
import utils.semanticActions.SA_CadenaEnd;
import utils.semanticActions.SA_ConstantEnd;
import utils.semanticActions.SA_CountLine;
import utils.semanticActions.SA_CountLineAndAdd;
import utils.semanticActions.SA_IdentifierEnd;
import utils.semanticActions.SA_InvalidAndReturnChar;
import utils.semanticActions.SA_InvalidChar;
import utils.semanticActions.SA_ReturnAndAddToTS;
import utils.semanticActions.SA_ReturnChar;
import utils.ElementoTS;

public class Lexicon {
	private final int END_OF_FILE = 0; //Requerido por BYACC/J como fin de archivo
	private final int END_OF_TEXT = 3; //ASCII

	//Constantes declaradas por BYACC/J
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

	private List<Integer> programBuffer;
	private int nlCounter;
	private MsgStack msgStack;
	private MsgStack tokenStack;
	private StateAndSemAction[][] stateMachine;
	private Hashtable<String, ElementoTS> symbolTable;
	private int currentChar;
	private String currentLexeme;
	private int ready;

	public Lexicon(List<Integer> programBuffer, MsgStack msgStack, MsgStack tokenStack, Hashtable<String, ElementoTS> simbTable) {
		nlCounter = 1;
		this.msgStack = msgStack;
		this.tokenStack = tokenStack;
		this.symbolTable = simbTable;
		this.programBuffer = programBuffer;
		stateMachine = new StateAndSemAction[9][15];
		initStateMachine();
		initSimbTable();
	}

	private int range(int i) {
		return  (i >= 65 && i <= 90 || i>= 97 && i <= 122)? 0 : 					// Letras
			( i >= 48 && i <= 57)? 1 : 												// Digitos
				(i == 95)? 2 : 														// '_'
					(i == 3)? 3 :													// 'ETX' Reemplaza al fin de archivo, no viene en un texto normal, se agrega artificialmente al final del buffer de programa
						((i>=40 && i<=45) || i==47 || i==91 || i==93 || i==59)? 4 :	// Símbolos literales aceptados
							(i==123)? 5 :											// '{'
								(i==125)? 6 :										// '}'
									(i==60)? 7 :									// '<'
										(i==62)? 8 :								// '>'
											(i==61)? 9 :							// '='
												(i==10)? 10 :						// Salto de línea
													(i==35)? 11 :					// '#'
														(i==9 || i==32)? 12 :		// Espacio y TAB
															(i==58)? 14 :			// ':'
																13;					// Otros
	}

	public Token getNewToken() {
		currentLexeme = ""; //Limpia la variable
		int state = 0;
		int previous_state = -1;
		while(state != -1) { //Mientras que no llegue a F, itera sobre la tabla de estados
			currentChar = programBuffer.get(0); //Lee el primer caracter
			programBuffer.remove(0); //Lo elimina del buffer
			int symbol = range(currentChar); //Ve a que columna tiene que ir con el caracter que obtuvo anteriormente
			if (stateMachine[state][symbol].getSemanticAction() != null)
				stateMachine[state][symbol].getSemanticAction().action(this); //Si tiene, realiza una A.S.
			previous_state = state; //Guarda el estado anterior para el control posterior
			state = stateMachine[state][symbol].getState(); //Cambia de estado
		}
		switch (previous_state) {
		case 0:
			if (currentChar == END_OF_TEXT) { //Es end of file y tiene que mandar 0, sino un token normal
				tokenStack.addMsg("Fin de archivo");
				return new Token(END_OF_FILE);
			}
			else {

				tokenStack.addMsg(String.valueOf((char)currentChar));
				return new Token(currentChar); //Literal directo
			}
		case 1:
			//Ver si el lexema es un PR, sino es un ID
			if (symbolTable.get(currentLexeme).getTokenClass().equals(ElementoTS.PR)) {
				tokenStack.addMsg("Palabra reservada " + currentLexeme);
				return new Token(Integer.valueOf(symbolTable.get(currentLexeme).getValue()));
			}
			else {
				tokenStack.addMsg("Identificador " + currentLexeme);
				return new Token(ID, currentLexeme);
			}
		case 2:
			tokenStack.addMsg("Constante " + currentLexeme);
			return new Token(CTE, currentLexeme);
		case 3:
			tokenStack.addMsg("Cadena " + currentLexeme);
			return new Token(CAD, currentLexeme);
		case 4:
			if (currentLexeme.length() == 1) { // Solo >
				tokenStack.addMsg(currentLexeme);
				return new Token((int)(currentLexeme.charAt(0)));
			}
			else {
				tokenStack.addMsg(">=");
				return new Token(GET);
			}
		case 5: 
			if (currentLexeme.length() == 1) { // Solo <
				tokenStack.addMsg(currentLexeme);
				return new Token((int)(currentLexeme.charAt(0)));
			}
			else if (currentLexeme.equals("<=")) {
				tokenStack.addMsg("<=");
				return new Token(LET);
			}
			else {
				tokenStack.addMsg("<>");
				return new Token(DIF);
			}
		case 6:
			tokenStack.addMsg("=="); //Si es = solo, lo completa como comparación
			return new Token(EQ);
		case 8:
			if (currentLexeme.length() == 1) { // Solo : (ya no se asume que : sea para poner :=)
				tokenStack.addMsg(currentLexeme);
				return new Token((int)(currentLexeme.charAt(0)));
			}
			else if (currentLexeme.equals(":=")) {
				tokenStack.addMsg("Asignación");
				return new Token(ASSIGN);
			}
		}
		return null; //No llega acá, es para que no de error por falta de retorno
	}

	private void initSimbTable()
	{
		//Carga de todas las palabras reservadas (PR) en la tabla de símbolos
		symbolTable.put("begin", new ElementoTS(ElementoTS.PR, String.valueOf(BEGIN), null));
		symbolTable.put("end", new ElementoTS(ElementoTS.PR, String.valueOf(END), null));;
		symbolTable.put("int", new ElementoTS(ElementoTS.PR, String.valueOf(INT), null));
		symbolTable.put("long", new ElementoTS(ElementoTS.PR, String.valueOf(LONG), null));
		symbolTable.put("if", new ElementoTS(ElementoTS.PR, String.valueOf(IF), null));
		symbolTable.put("else", new ElementoTS(ElementoTS.PR, String.valueOf(ELSE), null));
		symbolTable.put("end_if", new ElementoTS(ElementoTS.PR, String.valueOf(END_IF), null));
		symbolTable.put("foreach",new ElementoTS(ElementoTS.PR, String.valueOf(FOREACH), null));
		symbolTable.put("in", new ElementoTS(ElementoTS.PR, String.valueOf(IN), null));
		symbolTable.put("print", new ElementoTS(ElementoTS.PR, String.valueOf(PRINT), null));
		symbolTable.put("size", new ElementoTS(ElementoTS.PR, String.valueOf(SIZE), null));
	}

	public void putSymbolTable(String lexeme, ElementoTS tupla) {
		symbolTable.put(lexeme, tupla);
	}

	public boolean containsKey(String lexeme)
	{
		return this.symbolTable.containsKey(lexeme);
	}

	//
	public Hashtable<String, ElementoTS> getSymbolTable() {
		return symbolTable;
	}

	public void increaseCounterSymbolTable() {
		symbolTable.get(currentLexeme).increaseVariableRepetitions();
	}

	private void initStateMachine () {
		//Carga de maquina de estados [Estado,Caracteres] [Fila,Columna] de la matriz de transición

		stateMachine[0][0]  = new StateAndSemAction(1, new SA_AddChar());
		stateMachine[0][1]  = new StateAndSemAction(2, new SA_AddChar());
		stateMachine[0][2]  = new StateAndSemAction(-1, null);
		stateMachine[0][3]  = new StateAndSemAction(-1, null);
		stateMachine[0][4]  = new StateAndSemAction(-1, null);
		stateMachine[0][5]  = new StateAndSemAction(3, null);
		stateMachine[0][6]  = new StateAndSemAction(0, new SA_InvalidChar());
		stateMachine[0][7]  = new StateAndSemAction(5, new SA_AddChar());
		stateMachine[0][8]  = new StateAndSemAction(4, new SA_AddChar());
		stateMachine[0][9]  = new StateAndSemAction(6, new SA_AddChar());
		stateMachine[0][10] = new StateAndSemAction(0, new SA_CountLine());
		stateMachine[0][11] = new StateAndSemAction(7, null);
		stateMachine[0][12] = new StateAndSemAction(0, null);
		stateMachine[0][13] = new StateAndSemAction(0, new SA_InvalidChar());
		stateMachine[0][14] = new StateAndSemAction(8, new SA_AddChar());

		//////////////Nuevo estado //////////////
		stateMachine[1][0]  = new StateAndSemAction(1, new SA_AddChar());
		stateMachine[1][1]  = new StateAndSemAction(1, new SA_AddChar());
		stateMachine[1][2]  = new StateAndSemAction(1, new SA_AddChar());
		stateMachine[1][3]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][4]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][5]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][6]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][7]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][8]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][9]  = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][10] = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][11] = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][12] = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][13] = new StateAndSemAction(-1, new SA_IdentifierEnd() );
		stateMachine[1][14] = new StateAndSemAction(-1, new SA_IdentifierEnd());

		//////////////Nuevo estado //////////////
		stateMachine[2][0]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][1]  = new StateAndSemAction(2, new SA_AddChar());
		stateMachine[2][2]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][3]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][4]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][5]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][6]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][7]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][8]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][9]  = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][10] = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][11] = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][12] = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][13] = new StateAndSemAction(-1, new SA_ConstantEnd());
		stateMachine[2][14] = new StateAndSemAction(-1, new SA_ConstantEnd());

		//////////////Nuevo estado //////////////
		stateMachine[3][0]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][1]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][2]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][3]  = new StateAndSemAction(-1, new SA_ReturnAndAddToTS());
		stateMachine[3][4]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][5]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][6]  = new StateAndSemAction(-1, new SA_CadenaEnd());
		stateMachine[3][7]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][8]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][9]  = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][10] = new StateAndSemAction(3, new SA_CountLineAndAdd());
		stateMachine[3][11] = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][12] = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][13] = new StateAndSemAction(3, new SA_AddChar());
		stateMachine[3][14] = new StateAndSemAction(3, new SA_AddChar());

		//////////////Nuevo estado //////////////
		stateMachine[4][0]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][1]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][2]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][3]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][4]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][5]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][6]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][7]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][8]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][9]  = new StateAndSemAction(-1, new SA_AddChar());
		stateMachine[4][10] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][11] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][12] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][13] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[4][14] = new StateAndSemAction(-1, new SA_ReturnChar());

		//Nuevo estado//
		stateMachine[5][0]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][1]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][2]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][3]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][4]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][5]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][6]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][7]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][8]  = new StateAndSemAction(-1, new SA_AddChar());
		stateMachine[5][9]  = new StateAndSemAction(-1, new SA_AddChar());
		stateMachine[5][10] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][11] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][12] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][13] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[5][14] = new StateAndSemAction(-1, new SA_ReturnChar());

		//////////////Nuevo estado //////////////
		stateMachine[6][0]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][1]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][2]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][3]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][4]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][5]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][6]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][7]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][8]  = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][9]  = new StateAndSemAction(-1, new SA_AddChar());
		stateMachine[6][10] = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][11] = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][12] = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][13] = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());
		stateMachine[6][14] = new StateAndSemAction(-1, new SA_InvalidAndReturnChar());

		//////////////Nuevo estado //////////////
		stateMachine[7][0]  = new StateAndSemAction(7, null);
		stateMachine[7][1]  = new StateAndSemAction(7, null);
		stateMachine[7][2]  = new StateAndSemAction(7, null);
		stateMachine[7][3]  = new StateAndSemAction(0, new SA_ReturnChar());
		stateMachine[7][4]  = new StateAndSemAction(7, null);
		stateMachine[7][5]  = new StateAndSemAction(7, null);
		stateMachine[7][6]  = new StateAndSemAction(7, null);
		stateMachine[7][7]  = new StateAndSemAction(7, null);
		stateMachine[7][8]  = new StateAndSemAction(7, null);
		stateMachine[7][9]  = new StateAndSemAction(7, null);
		stateMachine[7][10] = new StateAndSemAction(0, new SA_CountLine());
		stateMachine[7][11] = new StateAndSemAction(7, null);
		stateMachine[7][12] = new StateAndSemAction(7, null);
		stateMachine[7][13] = new StateAndSemAction(7, null);
		stateMachine[7][14] = new StateAndSemAction(7, null);

		////////////// Nuevo estado //////////////
		stateMachine[8][0]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][1]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][2]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][3]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][4]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][5]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][6]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][7]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][8]  = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][9]  = new StateAndSemAction(-1, new SA_AddChar());
		stateMachine[8][10] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][11] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][12] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][13] = new StateAndSemAction(-1, new SA_ReturnChar());
		stateMachine[8][14] = new StateAndSemAction(-1, new SA_ReturnChar());
	}

	public boolean isReady(){ return (ready == 0); }

	public void addMsg(String msg){ msgStack.addMsg(msg); }

	public int getNewLineCounter() { return nlCounter; }

	public int getCurrentCharacter() { return currentChar; }
	
	public void setLexeme(String newLexeme) { currentLexeme = newLexeme; }

	public String getCurrentLexeme(){ return currentLexeme; }

	public void updateLexeme(String lexeme) { currentLexeme = lexeme; }

	public void returnCharacter() { programBuffer.add(0, currentChar); }

	public void addCharacter() { currentLexeme = currentLexeme + (char)currentChar; }

	public void addNlCounter() { nlCounter = nlCounter+1; }
}