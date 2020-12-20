package utils.syntacticTree;

import java.util.Hashtable;

import utils.ElementoTS;
import utils.MsgStack;
import utils.ParserVal;
import utils.RegisterTable;

public abstract class SyntacticTree extends ParserVal {
	private String blankSpace = "  "; // Para poder cambiar el espaciado en el árbol de manera más rápida
	private SyntacticTree izq;
	private SyntacticTree der;
	private String lexeme;
	private String type;
	protected String almacenamiento;
	
	public SyntacticTree(String lexeme){
		this.lexeme = lexeme;
		der = null;
		izq = null;
		type = ""; //Sin tipo por defecto
	}

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public SyntacticTree getHijoIzq(){ return izq; }
	public SyntacticTree getHijoDer() { return der; }
	public String getElem() { return lexeme; } 
	public void setHijoIzq(SyntacticTree izq) { this.izq = izq; }
	public void setHijoDer(SyntacticTree der) { this.der = der; }
	public void setElem(String elem) { lexeme = elem; }

	public abstract void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm, Hashtable<String, ElementoTS> symbolTable, String blankPrefix);
	
	public String getAlmacenamiento() { return almacenamiento; }
	
	public void setAlmacenamiento(String almacenamiento) { this.almacenamiento = almacenamiento; }
	
	public boolean isVariableOrConst() { return (isVariable() || isConstant()); }

	public boolean isVariable() { return getAlmacenamiento().charAt(0) == '_'; }
	
	public boolean isConstant() { return ((int)(getAlmacenamiento().charAt(0)) >= 48 && (int)(getAlmacenamiento().charAt(0)) <= 57 || getAlmacenamiento().charAt(0) == '-'); }
	
	public String getBlankSpace() { return blankSpace; }
}