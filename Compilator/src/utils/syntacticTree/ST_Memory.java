package utils.syntacticTree;

import java.util.Hashtable;

import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Memory extends SyntacticTree{
	public ST_Memory(String lexeme, SyntacticTree nodoIzq, SyntacticTree nodoDer) {
		super(lexeme);
		super.setHijoIzq(nodoIzq);
		super.setHijoDer(nodoDer);
	}

	@Override
	public void recorreArbol(RegisterTable registers, MsgStack comAssembler, MsgStack comInterm, 
			Hashtable<String, ElementoTS> symbolTable, String blankPrefix) {
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		String dataFromLeft = "";
		String dataFromRight = "";
		System.out.println("en memory: " + getElem());
		getHijoIzq().recorreArbol(registers, comAssembler, comInterm, symbolTable, blankPrefix + getBlankSpace());
		getHijoDer().recorreArbol(registers, comAssembler, comInterm, symbolTable, blankPrefix + getBlankSpace());
		dataFromLeft = getHijoIzq().getAlmacenamiento();
		dataFromRight = getHijoDer().getAlmacenamiento(); 

		switch(getElem()) {
		case "::=": {
			String collectionPtr = registers.getRegFreeLong(this, symbolTable, comAssembler);
			comAssembler.addMsg("lea " + collectionPtr + ", " + dataFromRight); //offset del arreglo en i
			comAssembler.addMsg("mov " + dataFromLeft + ", " + collectionPtr);
			registers.freeReg(registers.getRegPos(collectionPtr));
			break;
		}
		case "<<": {
			String collectionPtr = registers.getRegFreeLong(this, symbolTable, comAssembler);
			comAssembler.addMsg("mov " + collectionPtr + ", " + dataFromLeft); // lado izq tiene la direccion del arreglo
			String collectionSize = registers.getRegFreeLong(getHijoIzq(), symbolTable, comAssembler);
			System.out.println("nombre de colección: " + dataFromRight.substring(1));
			comAssembler.addMsg("mov " + collectionSize + ", " + symbolTable.get(dataFromRight.substring(1)).getCSizeBytes()); // obtiene el largo del arreglo en bytes
			String collectionOffset = registers.getRegFreeLong(getHijoDer(), symbolTable, comAssembler);
			comAssembler.addMsg("lea " + collectionOffset + ", " + dataFromRight); // obtiene el offset del arreglo
			comAssembler.addMsg("add " + collectionSize + ", " + collectionOffset); //direccion final del arreglo
			comAssembler.addMsg("cmp " + collectionPtr + ", " + collectionSize); //se compara la direccion a la que apunta i con direccion fin del arreglo
			registers.freeReg(registers.getRegPos(collectionSize));
			registers.freeReg(registers.getRegPos(collectionOffset));
			registers.freeReg(registers.getRegPos(collectionPtr));
			break;
		}
		case "+=": {
			if (getHijoDer().getType().equals(ElementoTS.INT)) {
				comAssembler.addMsg("add " + dataFromLeft + ", 2");
			} else {
				comAssembler.addMsg("add " + dataFromLeft + ", 4");
			}
			break;
		}
		}
		setAlmacenamiento(getElem());
	}
}
