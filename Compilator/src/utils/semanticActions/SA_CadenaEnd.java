package utils.semanticActions;

import java.util.Set;

import logic.Lexicon;
import utils.ElementoTS;

public class SA_CadenaEnd implements SemanticAction{
	//Acción semántica cuando se cierra una cadena con '}'
	
	@Override
	public void action(Lexicon lex) {
		
		if(lex.getSymbolTable().contains(lex.getCurrentLexeme())) {
			Set<String> keys = lex.getSymbolTable().keySet();
			String cadName = "";
			for(String key : keys) {
				if ((lex.getSymbolTable()).get(key).getValue().equals(lex.getCurrentLexeme()) && (lex.getSymbolTable()).get(key).getTokenClass().equals(ElementoTS.CAD))
					cadName = key;
			}
			System.out.println("Repetida en cad end: " + lex.getCurrentLexeme());
			lex.setLexeme(cadName);
			lex.increaseCounterSymbolTable();
		} else {
			ElementoTS tupla = new ElementoTS(ElementoTS.CAD, lex.getCurrentLexeme(), "");
			String cadName = "@cad" + ElementoTS.getNewId();
			lex.putSymbolTable(cadName, tupla);
			lex.setLexeme(cadName);
		}
	}
}
