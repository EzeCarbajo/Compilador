package utils;

import utils.semanticActions.SemanticAction;

public class StateAndSemAction {
	private int state;
	private SemanticAction sAction;
	
	public StateAndSemAction(int state, SemanticAction sAction) {
		this.state=state;
		this.sAction=sAction;
	}
	
	public int getState() {
		return this.state;
	}
	
	public SemanticAction getSemanticAction() {
		return this.sAction;
	}
}
