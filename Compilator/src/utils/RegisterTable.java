package utils;


import java.util.ArrayList;
import java.util.Hashtable;

import utils.syntacticTree.SyntacticTree;

public class RegisterTable {

	private class Registro {
		public String name;
		public Boolean is_busy; // False: libre, True: ocupado
		public SyntacticTree nodoAsociado;

		public Registro() { is_busy = false; }
		public String toString() { return "Nombre: " + name + " - Estado: " + is_busy + " - Nodo: " + nodoAsociado; }
	}

	private static final int CANT_REGISTROS = 4; //Cantidad de registros en procesador
	public static final int AX = 0;
	public static final String NAME_AX = "ax";
	public static final int BX = 1;
	public static final String NAME_BX = "bx";
	public static final int CX = 2;
	public static final String NAME_CX = "cx";
	public static final int DX = 3;
	public static final String NAME_DX = "dx";
	public static final int EAX = 0;
	public static final String NAME_EAX = "eax";
	public static final int EBX = 1;
	public static final String NAME_EBX = "ebx";
	public static final int ECX = 2;
	public static final String NAME_ECX = "ecx";
	public static final int EDX = 3;
	public static final String NAME_EDX = "edx";

	private int contadorVarAUX;
	private ArrayList<Registro> regState;

	public RegisterTable() {
		contadorVarAUX = 0;
		regState = new ArrayList<Registro>();
		for(int i = 0; i < CANT_REGISTROS; i++) {
			Registro reg = new Registro();
			regState.add(reg);
		}
	}

	public String getRegFreeInt(SyntacticTree nodoAsociado, Hashtable<String, ElementoTS> symbolTable, MsgStack assemblerCode) {
		Registro reg = null;
		boolean encontrado = false;
		int i = 0;
		while (i < regState.size() && !encontrado) {
			reg = regState.get(i);
			if(!reg.is_busy) {
				if(i == AX) reg.name = NAME_AX;
				else if(i == BX) reg.name = NAME_BX;
				else if(i == CX) reg.name = NAME_CX;
				else reg.name = NAME_DX;
				reg.nodoAsociado = nodoAsociado;
				reg.is_busy = true;
				encontrado = true;
			}
			i++;
		}
		if(!encontrado) {
			String nombreVarAux = "_@varAux" + contadorVarAUX++;
			int regPos = (int)((Math.random()*2 + 1)); //devueve como valor 1 o 2
			reg = regState.get(regPos); // Siempre se le asigna a BX o CX
			ElementoTS nuevoElemento = new ElementoTS(ElementoTS.ID, "", reg.nodoAsociado.getType()); //agrego la variable auxiliar a la tabla de simbolos
			if ((reg.name == NAME_EAX || reg.name == NAME_EBX || reg.name == NAME_ECX || reg.name == NAME_EDX) && reg.nodoAsociado.getType() == ElementoTS.INT)
				nuevoElemento.setHasPointer();
			nuevoElemento.setIdentifierClass(ElementoTS.VAR);
			symbolTable.put(nombreVarAux.substring(1), nuevoElemento);
			reg.nodoAsociado.setAlmacenamiento(nombreVarAux); // setea el nodo del registro almacenado en var
			assemblerCode.addMsg("mov " + nombreVarAux + ", " + reg.name);// mueve lo que contiene CX a var
			reg.nodoAsociado = nodoAsociado;
			if (regPos == BX)
				reg.name = NAME_BX;
			if (regPos == CX)
				reg.name = NAME_CX;
		}
		return reg.name;

	}

	public String getRegFreeLong(SyntacticTree nodoAsociado, Hashtable<String, ElementoTS> symbolTable, MsgStack assemblerCode) {
		Registro reg = null;
		boolean encontrado = false;
		int i = 0;
		while (i < regState.size() && !encontrado) {
			reg = regState.get(i);
			if(!reg.is_busy) {
				if(i == EAX) reg.name = NAME_EAX;
				else if(i == EBX) reg.name = NAME_EBX;
				else if(i == ECX) reg.name = NAME_ECX;
				else reg.name = NAME_EDX;
				reg.nodoAsociado = nodoAsociado;
				reg.is_busy = true;
				encontrado = true;
			}
			i++;
		}
		if(!encontrado) {
			String nombreVarAux = "_@varAux" + contadorVarAUX++;
			int regPos = (int)((Math.random()*2 + 1)); //devuelve como valor 1 o 2
			reg = regState.get(regPos); // Asigna siempre EBX o ECX
			ElementoTS nuevoElemento = new ElementoTS(ElementoTS.ID, "", reg.nodoAsociado.getType()); //agrego la variable auxiliar a la tabla de simbolos
			if ((reg.name == NAME_EAX || reg.name == NAME_EBX || reg.name == NAME_ECX || reg.name == NAME_EDX) && reg.nodoAsociado.getType() == ElementoTS.INT)
				nuevoElemento.setHasPointer();
			nuevoElemento.setIdentifierClass(ElementoTS.VAR);
			symbolTable.put(nombreVarAux.substring(1), nuevoElemento);
			reg.nodoAsociado.setAlmacenamiento(nombreVarAux);//setea el nodo del registro almacenado en var
			assemblerCode.addMsg("mov " + nombreVarAux + ", " + reg.name);//mueve lo que contiene ECX a var
			reg.nodoAsociado = nodoAsociado;
			if (regPos == EBX)
				reg.name = NAME_EBX;
			if (regPos == ECX)
				reg.name = NAME_ECX;

		}
		return reg.name;

	}

	public void freeReg(int reg) { regState.get(reg).is_busy = false; }
	
	public String extendTo32b(int reg) { (regState.get(reg)).name = getNameRegister32b(reg); return regState.get(reg).name; }
	
	public String reduceTo16b(int reg) { (regState.get(reg)).name = getNameRegister16b(reg); return regState.get(reg).name; }
	
	private String getNameRegister32b(int reg) {
		return (reg == 0)? NAME_EAX :
				(reg == 1)? NAME_EBX :
				(reg == 2)? NAME_ECX : NAME_EDX;
	}
	
	private String getNameRegister16b(int reg) {
		return (reg == 0)? NAME_AX :
				(reg == 1)? NAME_BX :
				(reg == 2)? NAME_CX : NAME_DX;
	}

	public int getRegPos(String regName) {
		return  (regName == NAME_AX || regName == NAME_EAX)? AX :
			(regName == NAME_BX || regName == NAME_EBX)? BX :
				(regName == NAME_CX || regName == NAME_ECX)? CX : DX;
	}

	// Para pedir un registro en específico
	public String getReg(String nameReg, SyntacticTree nodo, Hashtable<String, ElementoTS> symbolTable, MsgStack assemblerCode) {
		//chequeo si esta libre, sino llama al cambiaRegistro() del nodo al que apunta el registro y agrega comando de cambio de registro
		Registro reg = regState.get(getRegPos(nameReg));
		if(reg.is_busy) {
			String regNuevo;
			if(reg.name == NAME_EAX || reg.name == NAME_EBX || reg.name == NAME_ECX || reg.name == NAME_EDX)
				regNuevo = getRegFreeLong(reg.nodoAsociado, symbolTable, assemblerCode);
			else
				regNuevo = getRegFreeInt(reg.nodoAsociado, symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + regNuevo + ", " + reg.name);
			reg.nodoAsociado.setAlmacenamiento(regNuevo);
			reg.nodoAsociado = nodo;
			reg.name = nameReg;
		} else {
			reg.is_busy = true;
			reg.nodoAsociado = nodo;
			reg.name = nameReg;
		}
		return nameReg;
	}
}

