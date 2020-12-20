package utils.semanticActions;

import logic.Lexicon;

public class SA_InvalidChar implements SemanticAction{
//Acción semántica asociada a la detección de carácteres inválidos, con aviso al programador
	
	@Override
	public void action(Lexicon Lex) {
		Lex.addMsg("Línea " + Lex.getNewLineCounter() + ": Carácter inválido.");
	}

}