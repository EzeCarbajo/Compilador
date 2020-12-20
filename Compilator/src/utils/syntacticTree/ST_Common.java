package utils.syntacticTree;

import java.util.Hashtable;
import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Common extends SyntacticTree {

	public ST_Common(String lexeme, SyntacticTree nodoIzq, SyntacticTree nodoDer) {
		super(lexeme);
		super.setHijoIzq(nodoIzq);
		super.setHijoDer(nodoDer);
	}

	public void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm,
			Hashtable<String, ElementoTS> symbolTable, String blankPrefix) {
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		System.out.println("entro a common " + getElem());
		getHijoIzq().recorreArbol(registers, assemblerCode, comInterm, symbolTable, blankPrefix + getBlankSpace());
		if (getHijoDer() != null)
			getHijoDer().recorreArbol(registers, assemblerCode, comInterm, symbolTable, blankPrefix + getBlankSpace());
		System.out.println("vuelve a common " + getElem());
		String op = getElem();
		if(op == "+") suma(registers,assemblerCode,symbolTable);
		else if(op == "-") resta(registers,assemblerCode,symbolTable);
		else if(op == "*") multiplicacion(registers,assemblerCode,symbolTable);
		else if(op == "/") division(registers,assemblerCode,symbolTable);
		else if(op == ":=") asignacion(registers, assemblerCode, symbolTable);
		else if((op == "<") || (op == ">") || (op == "LET") || (op == "GET") || (op == "EQ") || (op == "DIF"))
			comparacion(registers,assemblerCode,symbolTable);
	}

	private void asignacion(RegisterTable registers, MsgStack assemblerCode, Hashtable<String, ElementoTS> symbolTable) {
		String dataFromLeft = getHijoIzq().getAlmacenamiento();
		String dataFromRight = getHijoDer().getAlmacenamiento();
		if ((((ST_Leaf)getHijoIzq()).isCollectionPointer() && getHijoDer().isVariable()) 
				|| (getHijoIzq().isVariable() && getHijoDer().isVariable())) {  //// Se mueve el lado derecho a registro ////
			String tempReg;
			if (getHijoDer().getType() == ElementoTS.INT)
				tempReg = registers.getRegFreeInt(getHijoDer(), symbolTable, assemblerCode);
			else
				tempReg = registers.getRegFreeLong(getHijoDer(), symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + tempReg + ", " + dataFromRight);
			dataFromRight = tempReg;
		}
		if (((ST_Leaf)getHijoIzq()).isCollectionPointer()) //// Se realiza la asignación ////
			if (getHijoIzq().getType().equals(ElementoTS.INT))
				assemblerCode.addMsg("mov word ptr [" + dataFromLeft + "], " + dataFromRight);
			else
				assemblerCode.addMsg("mov dword ptr [" + dataFromLeft + "], " + dataFromRight);
		else
			assemblerCode.addMsg("mov " + dataFromLeft + ", " + dataFromRight);
		if(!(getHijoDer().isVariableOrConst()))
			registers.freeReg(registers.getRegPos(dataFromRight));
		if(!(getHijoIzq().isVariableOrConst()))
			registers.freeReg(registers.getRegPos(dataFromLeft));
	}

	private void comparacion(RegisterTable registers, MsgStack assemblerCode, Hashtable<String, ElementoTS> symbolTable) {
		String dataFromLeft = getHijoIzq().getAlmacenamiento();
		String dataFromRight;
		if (getHijoIzq().isVariableOrConst()) { //// Izq constante o variable ////
			String regAux;
			if(getHijoIzq().getType().equals(ElementoTS.LONG) || (symbolTable.containsKey(dataFromLeft) && symbolTable.get(dataFromLeft).isPointer()))
				regAux = registers.getRegFreeLong(getHijoIzq(), symbolTable, assemblerCode);
			else
				regAux = registers.getRegFreeInt(getHijoIzq(), symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + regAux + ", " + dataFromLeft);
			dataFromLeft = regAux;
		}
		dataFromRight = getHijoDer().getAlmacenamiento();
		if (getHijoIzq().getType() == ElementoTS.INT && (symbolTable.containsKey(getHijoIzq().getElem()) && 
				symbolTable.get(getHijoIzq().getElem()).isPointer())) { // Si el lado izquierdo es puntero y entero, el lado derecho se debe convertir en 32 bits
			String reg = registers.getReg(RegisterTable.NAME_AX, getHijoDer(), symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + reg + ", " + dataFromRight);
			assemblerCode.addMsg("cwde");
			reg = registers.extendTo32b(registers.getRegPos(reg));
			dataFromRight = reg;
			dataFromLeft = getHijoIzq().getAlmacenamiento();
		}
		assemblerCode.addMsg("cmp " + dataFromLeft +", " + dataFromRight);
		registers.freeReg(registers.getRegPos(dataFromLeft));
		if(!(getHijoDer().isVariableOrConst()))
			registers.freeReg(registers.getRegPos(dataFromRight));
		setAlmacenamiento(getElem());
	}

	private void suma(RegisterTable registers, MsgStack assemblerCode, Hashtable<String, ElementoTS> symbolTable) {
		String reg;
		String dataFromLeft = getHijoIzq().getAlmacenamiento();
		String dataFromRight =  getHijoDer().getAlmacenamiento();
		if(getHijoIzq().isVariableOrConst() && getHijoDer().isVariableOrConst()) {
			if(getHijoIzq().getType() == ElementoTS.INT)///ES ENTERO
				reg = registers.getRegFreeInt(getHijoIzq(), symbolTable, assemblerCode);
			else
				reg = registers.getRegFreeLong(getHijoIzq(), symbolTable, assemblerCode);
			dataFromRight = getHijoDer().getAlmacenamiento();
			assemblerCode.addMsg("mov " + reg + ", " + dataFromLeft);
			assemblerCode.addMsg("add " + reg + ", " + dataFromRight);
		} else {
			if(getHijoIzq().isVariableOrConst()) { // HIJO DER ES REG
				assemblerCode.addMsg("add " + dataFromRight + ", " + dataFromLeft);
				reg = dataFromRight;
				if (!getHijoIzq().isVariableOrConst())
					registers.freeReg(registers.getRegPos(dataFromLeft));
			} else { // HIJO IZQ ES REG (Puede que el derecho también sea registro)
				assemblerCode.addMsg("add " + dataFromLeft + ", " + dataFromRight);
				reg = dataFromLeft;
				if (!getHijoDer().isVariableOrConst())
					registers.freeReg(registers.getRegPos(dataFromRight));
			}
		}
		setAlmacenamiento(reg);
	}

	private void resta(RegisterTable registers, MsgStack assemblerCode, Hashtable<String, ElementoTS> symbolTable) {
		String regResult;
		String dataFromLeft = getHijoIzq().getAlmacenamiento();
		if (getHijoIzq().isVariableOrConst()) {
			if(getHijoIzq().getType() == ElementoTS.INT) ///ES ENTERO
				regResult = registers.getRegFreeInt(this, symbolTable, assemblerCode);
			else
				regResult = registers.getRegFreeLong(this,symbolTable,assemblerCode);
			assemblerCode.addMsg("mov " + regResult + ", " + dataFromLeft);
		} else { regResult = dataFromLeft; }
		String dataFromRight = getHijoDer().getAlmacenamiento();
		assemblerCode.addMsg("sub " + regResult + ", " + dataFromRight);
		setAlmacenamiento(regResult);
		if (!getHijoDer().isVariableOrConst())
			registers.freeReg(registers.getRegPos(dataFromRight));
	}

	private void multiplicacion(RegisterTable registers, MsgStack assemblerCode, Hashtable<String, ElementoTS> symbolTable) {
		String dataFromLeft = getHijoIzq().getAlmacenamiento();
		String dataFromRight = getHijoDer().getAlmacenamiento();
		if(getHijoIzq().getType() == ElementoTS.INT) {//ENTERO
			String regAX = registers.getReg(RegisterTable.NAME_AX, getHijoIzq(), symbolTable, assemblerCode);
			@SuppressWarnings("unused")
			String regDX = registers.getReg(RegisterTable.NAME_DX, getHijoIzq(), symbolTable, assemblerCode); // Se reserva para que, si tiene algo, no sea pisado
			dataFromLeft = getHijoIzq().getAlmacenamiento();
			dataFromRight = getHijoDer().getAlmacenamiento();
			assemblerCode.addMsg("mov " + regAX + ", " + dataFromLeft);
			if (getHijoDer().isConstant()) {
				String regAux = registers.getRegFreeInt(getHijoDer(), symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regAux + ", " + dataFromRight);
				dataFromRight = regAux;
				getHijoDer().setAlmacenamiento(regAux);
			}
			assemblerCode.addMsg("imul " + dataFromRight);
			regDX = registers.extendTo32b(registers.getRegPos(RegisterTable.NAME_DX));
			assemblerCode.addMsg("shl "  + regDX + ", 16");
			assemblerCode.addMsg("mov " + RegisterTable.NAME_DX + ", " + regAX);
			registers.freeReg(RegisterTable.AX);
			if (!getHijoDer().isVariableOrConst())
				registers.freeReg(registers.getRegPos(dataFromRight));	
			setAlmacenamiento(regDX);
		} else {//LONG
			String regEAX = registers.getReg(RegisterTable.NAME_EAX, this, symbolTable, assemblerCode);
			@SuppressWarnings("unused")
			String regEDX = registers.getReg(RegisterTable.NAME_EDX, getHijoIzq(), symbolTable, assemblerCode); // Nuevamente se reserva para, si tiene algo, no pisarlo
			dataFromLeft = getHijoIzq().getAlmacenamiento();
			dataFromRight = getHijoDer().getAlmacenamiento();
			assemblerCode.addMsg("mov " + regEAX + ", " + dataFromLeft);
			if (getHijoDer().isConstant()) {
				String regAux = registers.getRegFreeLong(getHijoDer(), symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regAux + ", " + dataFromRight);
				dataFromRight = regAux;
				getHijoDer().setAlmacenamiento(regAux);
			}
			assemblerCode.addMsg("imul " + dataFromRight);
			registers.freeReg(RegisterTable.EDX);
			if (!getHijoDer().isVariableOrConst())
				registers.freeReg(registers.getRegPos(dataFromRight));
			setAlmacenamiento(regEAX);
		}
		if (!getHijoIzq().isVariableOrConst()) {
			registers.freeReg(registers.getRegPos(dataFromLeft));
		}
		if (!getHijoDer().isVariableOrConst()) {
			registers.freeReg(registers.getRegPos(dataFromRight));
		}
	}

	private void division(RegisterTable registers, MsgStack assemblerCode,Hashtable<String, ElementoTS> symbolTable) {
		String dataFromLeft = getHijoIzq().getAlmacenamiento(); //Dividendo
		String dataFromRight = getHijoDer().getAlmacenamiento(); //Divisor
		String regAuxControlZero = "";
		if (getHijoDer().isConstant()) { // Con constantes a izquierda se rompe la comparación
			if (getHijoDer().getType() == ElementoTS.INT)
				regAuxControlZero = registers.getRegFreeInt(getHijoDer(), symbolTable, assemblerCode);
			else
				regAuxControlZero = registers.getRegFreeLong(getHijoDer(), symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + regAuxControlZero + ", " + dataFromRight);
			assemblerCode.addMsg("cmp " + regAuxControlZero + ", " + 0);
		} else
			assemblerCode.addMsg("cmp " + dataFromRight + ", " + 0);
		assemblerCode.addMsg("jz _msgDivisionPorCero"); //Salto a la subrutina de programa si el divisor es 0
		if (regAuxControlZero != "")
			registers.freeReg(registers.getRegPos(regAuxControlZero));
		if(getType() == ElementoTS.INT) { //// ES ENTERO
			System.out.println("Se realiza división entera de: " + getHijoIzq().getElem() + " y " + getHijoDer().getElem());
			//pide registros para contener al dividendo
			String regAX = registers.getReg(RegisterTable.NAME_AX, this, symbolTable, assemblerCode);
			@SuppressWarnings("unused")
			String regDX = registers.getReg(RegisterTable.NAME_DX, getHijoIzq(), symbolTable, assemblerCode);
			dataFromLeft = getHijoIzq().getAlmacenamiento();
			assemblerCode.addMsg("mov " + regAX + ", " + dataFromLeft); //divendo en AX
			assemblerCode.addMsg("cwd"); //extensión de signo para 16 bits
			String divider;
			if (getHijoDer().isVariableOrConst()) {
				divider = registers.getRegFreeInt(getHijoDer(), symbolTable, assemblerCode);
				dataFromRight = getHijoDer().getAlmacenamiento();
				assemblerCode.addMsg("mov " + divider + ", " + dataFromRight);//guarda divisor
			} else
				divider = getHijoDer().getAlmacenamiento();
			assemblerCode.addMsg("idiv " + divider);//DIVISION DX:AX / CX
			registers.freeReg(registers.getRegPos(divider));//LIBERA
			registers.freeReg(RegisterTable.DX);//LIBERA COCIENTE
			setAlmacenamiento(regAX);//DEVUELVE RESTO
		} else { //// ES LONG
			System.out.println("Se realiza división long de: " + getHijoIzq().getElem() + " y " + getHijoDer().getElem());
			String regEAX = registers.getReg(RegisterTable.NAME_EAX, this, symbolTable, assemblerCode);
			@SuppressWarnings("unused")
			String regEDX = registers.getReg(RegisterTable.NAME_EDX, getHijoIzq(), symbolTable, assemblerCode);
			dataFromLeft = getHijoIzq().getAlmacenamiento();
			assemblerCode.addMsg("mov " + regEAX + ", " + dataFromLeft); //divendo en EAX
			assemblerCode.addMsg("cdq"); //extension de signo para 32 bits
			String divider;
			if (getHijoDer().isVariableOrConst()) {
				divider = registers.getRegFreeLong(getHijoDer(), symbolTable, assemblerCode);
				dataFromRight = getHijoDer().getAlmacenamiento();
				assemblerCode.addMsg("mov " + divider + ", " + dataFromRight);//guarda divisor
			} else
				divider = getHijoDer().getAlmacenamiento();
			assemblerCode.addMsg("idiv " + divider);//DIVISION EDX:EAX / ECX
			registers.freeReg(registers.getRegPos(divider));
			registers.freeReg(RegisterTable.EDX);//libera resto
			setAlmacenamiento(regEAX);// devuelve cociente
		}
		if (!getHijoIzq().isVariableOrConst()) {
			registers.freeReg(registers.getRegPos(dataFromLeft));
		}
		if (!getHijoDer().isVariableOrConst()) {
			registers.freeReg(registers.getRegPos(dataFromRight));
		}
	}
}