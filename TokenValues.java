package br.edu.unifei.ecom06;

public class TokenValues {
	private final Token token;
	private final String value;
	
	public TokenValues(Token token, String value) {
		this.token = token;
		this.value = value;
	}
	
	public Token getToken() {
		return token;
	}
	
	public String getValue() {
		return value;
	}

}
