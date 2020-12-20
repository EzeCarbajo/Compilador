package utils.syntacticTree;

import java.util.Hashtable;

import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Leaf extends SyntacticTree{
	
	protected boolean forLeftSideInAssignment;

	public ST_Leaf(String lex)
	{
		super(lex);
		forLeftSideInAssignment = false;
	}
	
	public void setLeftSideAssignment() { forLeftSideInAssignment = true; }
	
	public boolean isCollectionPointer() { return forLeftSideInAssignment; }
	
	@Override
	public void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm, Hashtable<String,
			ElementoTS> symbolTable, String blankPrefix) {
		System.out.println("en leaf simple: " + getElem());
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		String data = getElem();
		if(data.charAt(data.length() - 1) == ']' ) { // Si es una colección
			int subIndexNameStart = 0;
			while (data.charAt(subIndexNameStart) != '[')
				subIndexNameStart++;
			subIndexNameStart++; // Salta el corchete inicial
			String subIndexName = data.substring(subIndexNameStart, data.length() - 1);
			String regIndex = "";
			if (getType().equals(ElementoTS.INT) || (symbolTable.containsKey(subIndexName) && !symbolTable.get(subIndexName).isPointer())) { 
				regIndex = registers.getReg(RegisterTable.NAME_AX, this, symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regIndex + ", " + subIndexName); // Valor de índice en EAX
				assemblerCode.addMsg("cwde");
				regIndex = registers.extendTo32b(registers.getRegPos(RegisterTable.NAME_AX));
			} else {
				regIndex = registers.getReg(RegisterTable.NAME_EAX, this, symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regIndex + ", " + subIndexName); // Valor de índice en EAX
			}
			String collectionName = data.substring(1, subIndexNameStart - 1);
			String regSizeByType = registers.getRegFreeLong(null, symbolTable, assemblerCode);
			int size;
			if(symbolTable.get(collectionName).getVariableType().equals("int"))
				size = 2;
			else
				size = 4;
			System.out.println("en leaf con " + collectionName + "[" + subIndexName + "]");
			assemblerCode.addMsg("mov " + regSizeByType + ", " + size);
			assemblerCode.addMsg("mul " + regSizeByType); // Calcula el índice en Bytes (EDX:EAX = EAX * regSizeByType)
			registers.freeReg(registers.getRegPos(regSizeByType));
			String regCollectionOffset = registers.getRegFreeLong(this, symbolTable, assemblerCode);
			assemblerCode.addMsg("lea " + regCollectionOffset + ", " + "_" + collectionName); // Base de la colección
			assemblerCode.addMsg("add " + regIndex + ", " + regCollectionOffset); // Calcula posición del índice en memoria (Base + Desplazamiento)
			//chequeo por arreglo superado
			String regEndCollection = registers.getRegFreeLong(this,symbolTable,assemblerCode);
			assemblerCode.addMsg("mov " + regEndCollection + ", " + symbolTable.get(collectionName).getCSizeBytes()); //guarda el tamaño en bytes del arreglo
			assemblerCode.addMsg("add " + regEndCollection + ", " + regCollectionOffset); //guarda la direccion final del arreglo
			assemblerCode.addMsg("cmp " + regIndex + ", " + regEndCollection); //compara direccion final del arreglo con direccion a la que se desea acceder del arreglo
			assemblerCode.addMsg("jge _msgArregloFueraDeRango");
			//chequeo por direccion de memoria menor (por subíndices negativos)
			assemblerCode.addMsg("cmp " + regIndex + ", " + regCollectionOffset); //compara direccion final del arreglo con direccion a la que se desea acceder del arreglo
			assemblerCode.addMsg("jl _msgArregloFueraDeRango");
			if (forLeftSideInAssignment) { // Si es para el lado izquierdo se devuelve el puntero
				data = regIndex;
				registers.freeReg(registers.getRegPos(regCollectionOffset));
			} else { // Para el lado derecho se devuelve el valor apuntado
				if (getType().equals(ElementoTS.INT)) {
					registers.freeReg(registers.getRegPos(regCollectionOffset));
					data = registers.getRegFreeInt(this, symbolTable, assemblerCode);
					assemblerCode.addMsg("mov " + data + ", word ptr [" + regIndex + "]"); //guarda en regColec el valor almacenado en la direccion de memoria guardada en regI
				} else {
					assemblerCode.addMsg("mov " + regCollectionOffset + ", dword ptr [" + regIndex + "]");
					data = regCollectionOffset;
				}
				registers.freeReg(registers.getRegPos(regIndex));
			}
			registers.freeReg(registers.getRegPos(regEndCollection));
		} else
		// Tratamiento de punteros como enteros, devuelve un registro de 16 bits, tira la parte alta
			if (isVariable() && getType().equals(ElementoTS.INT)  && symbolTable.get(getElem().substring(1)).isPointer() && !forLeftSideInAssignment) {
				String regEAX = registers.getReg(RegisterTable.NAME_EAX, this, symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regEAX + ", " + getElem());
				regEAX = registers.reduceTo16b(registers.getRegPos(regEAX)); // Comienza utilizar AX
				String regResult = registers.getRegFreeInt(this, symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regResult + ", " + regEAX);
				registers.freeReg(registers.getRegPos(regEAX));
				data = regResult;
			}
		setAlmacenamiento(data);
	}	
}

