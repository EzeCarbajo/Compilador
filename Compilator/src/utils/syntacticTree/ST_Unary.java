package utils.syntacticTree;

import java.util.Hashtable;

import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Unary extends SyntacticTree{

	public ST_Unary(String lexeme, SyntacticTree nodo) {
		super(lexeme);
		super.setHijoIzq(nodo);
	}

	@Override
	public void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm, Hashtable<String, ElementoTS> symbolTable, String blankPrefix) {
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		comInterm.addMsg(blankPrefix + getBlankSpace() + "Nodo: " + getHijoIzq().getElem());
		String reg= registers.getReg(RegisterTable.NAME_EAX, getHijoIzq(), symbolTable, assemblerCode);
		assemblerCode.addMsg("lea " + reg + ", " + getHijoIzq().getElem());
		assemblerCode.addMsg("call _print");
		registers.freeReg(RegisterTable.EAX);
	}
}
