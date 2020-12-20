package utils;

public class Token {
	private int tokenValue; //Valor en código ASCII o código dado por BYACC/J
	private String lexeme = null;
	
	public Token(int tokenValue, String lexeme) { 
		this.tokenValue = tokenValue;
		this.lexeme = lexeme;
	}

	public Token(int tokenValue) {
		this.tokenValue = tokenValue;
	}

	public int getTokenValue() { return tokenValue;	}

	public String getLexeme() {	return lexeme; }
}
