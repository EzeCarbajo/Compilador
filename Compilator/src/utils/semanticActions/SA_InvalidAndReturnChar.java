package utils.semanticActions;

import logic.Lexicon;

public class SA_InvalidAndReturnChar implements SemanticAction {
//Acción semántica asociada a la asignación incompleta, esta acción no la completa ya que se detecta en la máquina de estados

	SA_InvalidChar invalidChar = new SA_InvalidChar();
	
	@Override
	public void action(Lexicon lex) {
		invalidChar.action(lex);
		lex.returnCharacter();
	}

}
