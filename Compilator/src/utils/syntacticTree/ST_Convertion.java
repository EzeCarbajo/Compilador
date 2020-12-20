

package utils.syntacticTree;

import java.util.Hashtable;

import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Convertion extends SyntacticTree{


	public ST_Convertion(String lexeme, SyntacticTree nodo) {
		super(lexeme);
		super.setHijoIzq(nodo);
	}
	
	public static final String ITOL = "itol"; 			// Para convertir int en long
	public static final String PTOV = "pointerToVal";	// Para extraer el valor apuntado por una variable puntero

	@Override
	public void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm,
			Hashtable<String, ElementoTS> symbolTable, String blankPrefix) {
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		//transforma un int en long
		System.out.println("entro a nodo conver " + getElem());
		String data = "";
		getHijoIzq().recorreArbol(registers,assemblerCode,comInterm,symbolTable, blankPrefix + getBlankSpace());
		data = getHijoIzq().getAlmacenamiento();
		if(getElem() == ITOL) {
			if(getHijoIzq().isVariableOrConst()) {
				if (data.charAt(0) == '_') { //es id
					String regData = registers.getReg(RegisterTable.NAME_AX, getHijoIzq(), symbolTable, assemblerCode);
					assemblerCode.addMsg("mov " + regData + ", " + data);
					assemblerCode.addMsg("cwde");
					regData = registers.extendTo32b(registers.getRegPos(RegisterTable.NAME_AX));
					setAlmacenamiento(regData);
				}else { //es cte
					String regConverted = registers.getRegFreeLong(getHijoIzq(), symbolTable, assemblerCode);
					assemblerCode.addMsg("mov " + regConverted + ", " + data);
					setAlmacenamiento(regConverted);
				}
			}else {//es un registro de 16b
				String regAX = registers.getReg(RegisterTable.NAME_AX, getHijoIzq(), symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + regAX + ", " + data);
				assemblerCode.addMsg("cwde");
				regAX = registers.extendTo32b(registers.getRegPos(RegisterTable.NAME_AX));
				setAlmacenamiento(regAX);
			}
		}
		if(getElem() == PTOV) {// extraccion de valor en memoria apuntado por una variable ("pointToVal")
			String reg;
			data = getHijoIzq().getElem();
			String regPosicion = registers.getRegFreeLong(getHijoIzq(), symbolTable, assemblerCode);
			assemblerCode.addMsg("mov " + regPosicion + ", " + data);
			if(symbolTable.get(data.substring(1)).getVariableType() == ElementoTS.INT) {
				reg = registers.getRegFreeInt(getHijoIzq(), symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + reg + ", word ptr [" + regPosicion + "]");
				System.out.println("extrae entero del indice: " + data);
			} else {
				reg = registers.getRegFreeLong(getHijoIzq(), symbolTable, assemblerCode);
				assemblerCode.addMsg("mov " + reg + ", dword ptr [" + regPosicion + "]");
				System.out.println("extrae long del indice: " + data);
			}
			setAlmacenamiento(reg);
		}
	}
}

