package utils.syntacticTree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import utils.ElementoTS;
import utils.MsgStack;
import utils.RegisterTable;

public class ST_Control extends SyntacticTree {

	private static List<String> etiquetas = new ArrayList<String>();
	private static int contEtiquetas = 0;

	public ST_Control(String lexeme, SyntacticTree nodo) {
		super(lexeme);
		setHijoIzq(nodo);
	}

	@Override
	public void recorreArbol(RegisterTable registers, MsgStack assemblerCode, MsgStack comInterm,
			Hashtable<String, ElementoTS> symbolTable, String blankPrefix) {
		System.out.println("entro a ctrl " + getElem());
		comInterm.addMsg(blankPrefix + "Nodo: " + getElem());
		if(getElem() == "COND_FOREACH") { //cond del foreach
			contEtiquetas++;
			assemblerCode.addMsg("_label" + contEtiquetas + ":");
			etiquetas.add("_label" + contEtiquetas); //etiqueta de inicio de condicion de foreach
		}
		getHijoIzq().recorreArbol(registers, assemblerCode, comInterm,symbolTable, blankPrefix + getBlankSpace());
		System.out.println("resuelve a ctrl " + getElem());
		if(getElem() == "COND_IF") {
			contEtiquetas++;
			if(getHijoIzq().getAlmacenamiento() == "<") {
				assemblerCode.addMsg("jge _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
			else if(getHijoIzq().getAlmacenamiento() == ">") {
				assemblerCode.addMsg("jle _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
			else if(getHijoIzq().getAlmacenamiento() == "LET") {
				assemblerCode.addMsg("jg _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
			else if(getHijoIzq().getAlmacenamiento() == "GET") {
				assemblerCode.addMsg("jl _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
			else if(getHijoIzq().getAlmacenamiento() == "EQ") {
				assemblerCode.addMsg("jne _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
			else if(getHijoIzq().getAlmacenamiento() == "DIF") {
				assemblerCode.addMsg("je _label" + contEtiquetas);
				etiquetas.add("_label" + contEtiquetas);
			}
		}
		else if(getElem() == "COND_FOREACH") {
			contEtiquetas++;
			assemblerCode.addMsg("jge _label" + contEtiquetas); //salto en caso de que se termine el arreglo
			etiquetas.add("_label" + contEtiquetas);//etiqueta de fin de foreach
		}
		else if(getElem() == "THEN") {
			assemblerCode.addMsg(etiquetas.get(etiquetas.size()-1) + ":");//se agrega la etiqueta de fin de if
			etiquetas.remove(etiquetas.size()-1);
		}
		else if(getElem() == "THEN_ELSE") {
			contEtiquetas++;
			assemblerCode.addMsg("jmp _label" + contEtiquetas);//se agrega el salto al fin del if (y se agrega despues su etiqueta)
			assemblerCode.addMsg(etiquetas.get(etiquetas.size()-1) + ":");//se agrega etiqueta de inicio de else
			etiquetas.remove(etiquetas.size()-1);
			etiquetas.add("_label" + contEtiquetas);
		}
		else if(getElem() == "ELSE") {
			assemblerCode.addMsg(etiquetas.get(etiquetas.size()-1) + ":");//se agrega la etiqueta de fin de if
			etiquetas.remove(etiquetas.size()-1);
		}
		else if(getElem() == "CUERPOAVANCE") {
			assemblerCode.addMsg("jmp " + etiquetas.get(etiquetas.size()-2));//salto al principio de la condicion de foreach
			assemblerCode.addMsg(etiquetas.get(etiquetas.size()-1) + ":");//creacion de etiqueta de fin de foreach
			etiquetas.remove(etiquetas.size()-1);
			etiquetas.remove(etiquetas.size()-1);
		}
		setAlmacenamiento("");
	}	
}
