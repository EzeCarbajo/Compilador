package utils.semanticActions;

import logic.Lexicon;

public class SA_AddChar implements SemanticAction{

	@Override
	public void action(Lexicon Lex) {
		Lex.addCharacter();
	}

}
