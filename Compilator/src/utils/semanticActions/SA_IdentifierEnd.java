package utils.semanticActions;

import logic.Lexicon;
import utils.ElementoTS;

public class SA_IdentifierEnd implements SemanticAction{
	// Acción semántica asociado al final de un Identificador o una Palabra reservada
	private static int MAX_CANT_ID = 25;
	
	@Override
	public void action(Lexicon Lex) {
		Lex.returnCharacter();
		String lexeme = Lex.getCurrentLexeme();
		if(lexeme.length()>25) {
			lexeme = lexeme.substring(0, MAX_CANT_ID);
			Lex.addMsg("Línea " + Lex.getNewLineCounter() + ": Warning: se trunco el nombre de variable por exceder de los caracteres permitidos (máx. 25)");
		}
		Lex.updateLexeme(lexeme);
		if(Lex.getSymbolTable().containsKey(lexeme)) 
			Lex.increaseCounterSymbolTable();
		else {
			ElementoTS tupla = new ElementoTS(ElementoTS.ID,"","");
			Lex.putSymbolTable(lexeme, tupla);
		}
	}
}
