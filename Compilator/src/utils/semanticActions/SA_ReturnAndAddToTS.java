package utils.semanticActions;

import logic.Lexicon;
import utils.ElementoTS;

public class SA_ReturnAndAddToTS implements SemanticAction{
//Acción semántica asociada a la devolución del último carácter leído al buffer de programa
// y al alta en la tabla de símbolos (Para cadenas)
	
	SA_ReturnChar returnChar = new SA_ReturnChar();
	
	@Override
	public void action(Lexicon lex) {
		returnChar.action(lex);
		//alta en TS
		if(lex.getSymbolTable().containsKey(lex.getCurrentLexeme())) 
			lex.increaseCounterSymbolTable();
		else {
			ElementoTS tupla = new ElementoTS(ElementoTS.CAD,"","");
			lex.putSymbolTable(lex.getCurrentLexeme(), tupla);
		}
	}
}
