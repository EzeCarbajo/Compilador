package utils.semanticActions;

import logic.Lexicon;

public class SA_CountLineAndAdd implements SemanticAction {
	SA_AddChar add = new SA_AddChar();
	SA_CountLine count = new SA_CountLine();
	
	@Override
	public void action(Lexicon lex) {
		add.action(lex);
		count.action(lex);
	}

}
