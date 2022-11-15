package br.edu.unifei.ecom06;

public class Lexer {
	private Token[] tokens = new Token[] {
			// palavras reservadas
			new Token("BEGIN", "begin"),
			new Token("END", "end"),
			new Token("MAIN", "main"),
			new Token("INTEGER_T", "integer"),
			new Token("REAL_T", "real"),
			new Token("CHARACTER_T", "character"),
			new Token("VOID", "void"),
			new Token("INPUT", "input"),
			new Token("OUTPUT", "output"),
			new Token("IF", "if"),
			new Token("ELSE", "else"),
			new Token("FOR", "for"),
			new Token("WHILE", "while"),
			new Token("RETURN", "return"),
			// simbolos
			new Token("ADD", "\\+"),
			new Token("SUB", "-"),
			new Token("MUL", "\\*"),
			new Token("DIV", "/"),
			new Token("MOD", "%"),
			new Token("EQUAL", "="),
			new Token("GREATEREQUAL", ">="),
			new Token("GREATER", ">"),
			new Token("LESSEQUAL", "<="),
			new Token("LESS", "<"),
			new Token("NOTEQUAL", "!="),
			new Token("AND", "&"),
			new Token("OR", "\\|"),
			new Token("NOT", "!"),
			new Token("PERIOD", "\\."),
			new Token("SEMICOLON", ";"),
			new Token("COLON", ":"),
			new Token("COMMA", ","),
			new Token("OPENPARENTHESIS", "\\("),
			new Token("CLOSEPARENTHESIS", "\\)"),
			new Token("AT", "@"),
			// tipos e variáveis
			new Token("REAL", "-?[0-9]+(,[0-9]+)"),
			new Token("INTEGER", "-?[0-9]+"),
			new Token("CHARACTER", "'[a-zA-Z0-9]'"), 
			new Token("VARIABLE", "[a-z][a-zA-Z0-9]*"),
			// espaço
			new Token("SPACE", "\\s")
	};
	private final String value;
	private int begin = 0;
	
	public Lexer(String value) {
		this.value = value;
	}
	
	private Token matchTokens(String value) {
		for(Token token : tokens)
			if(token.match(value))
				return token;
		
		return null;
	}
	
	public TokenValues nextToken() throws Exception {
		if (this.begin == this.value.length())
            return null;
		
        int end = this.begin + 1;
        while (end <= this.value.length() && this.matchTokens(this.value.substring(this.begin, end)) != null)
            ++end;
        
        String value = this.value.substring(this.begin, end - 1);
        Token token = this.matchTokens(value);
        if (token == null)
            throw new Exception("Token não reconhecido");
            
        this.begin = end - 1;
        if (!token.getName().equals("SPACE"))
            return new TokenValues(token, value);
        
        return this.nextToken();
	}
	
}
