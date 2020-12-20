package utils;

import java.util.Hashtable;
import java.util.Set;

public class AssemblerGenerator {

	public static MsgStack getAssembler(Hashtable<String, ElementoTS> symbolTable, MsgStack programCode) {
		MsgStack asm = new MsgStack();
		asm.addMsg(".386");
		asm.addMsg(".model flat, stdcall");
		asm.addMsg("option casemap :none");
		asm.addMsg("include \\masm32\\include\\windows.inc");
		asm.addMsg("include \\masm32\\include\\kernel32.inc");
		asm.addMsg("include \\masm32\\include\\user32.inc");
		asm.addMsg("includelib \\masm32\\lib\\kernel32.lib");
		asm.addMsg("includelib \\masm32\\lib\\user32.lib");
		asm.addMsg(".data");
		asm.addAll(getDataAssembler(symbolTable));
		asm.addMsg(".code");
		asm.addMsg("start:");
		asm.addAll(programCode);
		asm.addMsg("jmp _finDelPrograma");
		asm.addAll(getExtraFunctions(symbolTable));
		asm.addMsg("end start");
		return asm;
	}

	private static MsgStack getDataAssembler(Hashtable<String, ElementoTS> symbolTable) {
		MsgStack data = new MsgStack();
		Set<String> keys = symbolTable.keySet();
		for(String key: keys){
			ElementoTS element = symbolTable.get(key);
			if (element.getTokenClass().equals(ElementoTS.ID)) { 
				String word = "";
				// Define tamaño a usar DW 16 bits para int - DD 32 bits para long
				if (element.getVariableType().equals(ElementoTS.INT) && !element.isPointer())
					word = "dw";
				if (element.getVariableType().equals(ElementoTS.LONG) || element.isPointer())
					word = "dd";
				if (element.getIdentifierClass().equals(ElementoTS.VAR)) // Si es variable 
					data.addMsg( "_" + key + " " + word + " ?");
				else if (element.getIdentifierClass().equals(ElementoTS.COL)) { //Sino es una colección
					String cadElements = element.getElemsCollection();
					cadElements = cadElements.replace('_', '?');
					data.addMsg( "_" + key + " " + word + " " + element.getCSize() + " dup (" + cadElements + ")");
				}	
			}
			if (element.getTokenClass().equals(ElementoTS.CAD)) {
				String cadena = element.getValue().replaceAll("\n", "\", 10, 13,\"");
				data.addMsg("_" + key + " db \"" + cadena + "\"" + ", 0" );
			}	
		}
		data.addMsg("_@ErrorDivisionPorCero" + " db \"Error: Se dividio por cero\", 0");
		data.addMsg("_@ErrorArregloFueraDeRango" + " db \"Error: Arreglo fuera de rango\", 0");
		return data;
	}

	private static MsgStack getExtraFunctions(Hashtable<String, ElementoTS> symbolTable) {
		MsgStack extra = new MsgStack();
		extra.addMsg("_print:");
		extra.addMsg("invoke MessageBox, NULL, eax, eax, MB_OK");
		extra.addMsg("ret");

		extra.addMsg("_msgDivisionPorCero:");
		extra.addMsg("invoke MessageBox, NULL, addr _@ErrorDivisionPorCero, addr _@ErrorDivisionPorCero, MB_OK");
		extra.addMsg("jmp _finDelPrograma");

		extra.addMsg("_msgArregloFueraDeRango:");
		extra.addMsg("invoke MessageBox, NULL, addr _@ErrorArregloFueraDeRango, addr _@ErrorArregloFueraDeRango, MB_OK");
		extra.addMsg("jmp _finDelPrograma");

		extra.addMsg("_finDelPrograma:");
		extra.addMsg("invoke ExitProcess, 0");
		return extra;
	}
}
