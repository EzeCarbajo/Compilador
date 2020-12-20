package utils.semanticActions;

import logic.Lexicon;

public class SA_ReturnChar implements SemanticAction{
//Acción semántica asociado a la devolución del último carácter leído al buffer de programa

	@Override
	public void action(Lexicon Lex) {
		Lex.returnCharacter();
	}

}