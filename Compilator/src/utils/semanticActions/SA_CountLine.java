package utils.semanticActions;

import logic.Lexicon;

public class SA_CountLine implements SemanticAction{
//Acción semántca asociada al conteo de salto de líneas

	@Override
	public void action(Lexicon Lex) {
		Lex.addNlCounter();
	}

}