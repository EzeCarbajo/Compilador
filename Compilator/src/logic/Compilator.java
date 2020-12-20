package logic;

import java.util.Hashtable;
import java.util.List;

import utils.MsgStack;
import utils.RegisterTable;
import utils.syntacticTree.ST_Control;
import utils.AssemblerGenerator;
import utils.ElementoTS;

/*
 *TODO: Antes de entregar sacar las ventanas de token y estructura sintáctica
 *TODO: Revisar Nodo common
 *TODO: Revisar si gramática es correcta
 */

public class Compilator {
	private static Compilator single_instance = null;
	private Hashtable<String, ElementoTS> symbolTable;
	private MsgStack msgStack, semanticStructStack, tokenStack;
	private Lexicon lexicon;
	private Parser parser;
	private ST_Control nodoRaiz;
	private RegisterTable registros;
	private MsgStack assemblerCode;
	private MsgStack syntacticTree;
	private boolean hasErrors;


	public Compilator(List<Integer> programBuffer) {
		msgStack = new MsgStack();
		semanticStructStack = new MsgStack();
		tokenStack = new MsgStack();
		assemblerCode = new MsgStack();
		syntacticTree = new MsgStack();
		symbolTable = new Hashtable<String, ElementoTS>();
		ElementoTS.resetIdGenerator();
		lexicon = new Lexicon(programBuffer, msgStack, tokenStack, symbolTable);
		parser = new Parser(lexicon, symbolTable, msgStack, semanticStructStack);
		hasErrors = true;
	}

	public static Compilator getInstance(List<Integer> programBuffer) {
		single_instance = new Compilator(programBuffer); //Lo usamos así para que cree siempre uno nuevo, así renueva el program buffer
		return single_instance;
	}

	public void compilate() {
		int i = parser.yyparse();
		if (i == 0) {
			System.out.println("Parser correcto");
			if (!parser.hasErrors()) {
				hasErrors = false;
				nodoRaiz = ((ST_Control)parser.getRaiz());
				registros = new RegisterTable();
				nodoRaiz.recorreArbol(registros, assemblerCode, syntacticTree, symbolTable, "");
				assemblerCode = AssemblerGenerator.getAssembler(symbolTable, assemblerCode);
			} else
				hasErrors = true;
		}
		else
			System.out.println("Error en parser");
	}
	
	public boolean hasErrors() { return hasErrors; }

	public MsgStack getMsgStack() { return msgStack; }

	public MsgStack getTokenStack() { return tokenStack; }

	public MsgStack getSemanticStructStack() { return semanticStructStack; }
	
	public MsgStack getAssemblerCode() { return assemblerCode; }
	
	public MsgStack getSyntacticTree() { return syntacticTree; }

	public Hashtable<String, ElementoTS> getSymbTable() { return symbolTable; }
}


