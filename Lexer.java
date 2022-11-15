package br.edu.unifei.ecom06;

public class Lexer {
    private final Token[] tokens = new Token[]{
    		//palavras reservadas
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
    		new Token("DIV", "/"), 
    		new Token("MUL", "\\*"), 
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
    		new Token("COMMA", ","), 
    		new Token("SEMICOLON", ";"), 
    		new Token("COLON", ":"),
    		new Token("OPENPARENTHESIS", "\\("), 
    		new Token("CLOSEPARENTHESIS", "\\)"),
    		new Token("AT", "@"),
    		new Token("DOUBLEQUOTE", "\""),
    		new Token("QUOTE", "\'"),
    		//tipos e variaveis
    		new Token("VARIABLE", "[a-zA-Z][a-zA-Z0-9]*"), 
    		new Token("REAL", "-?[0-9]+,[0-9]+"),
    		new Token("INTEGER", "-?[0-9]+"), 
    		//espaço
    		new Token("SPACE", "\\s")};
    private final String input;
    private int start = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public TokenValues nextToken() throws Exception {
        if (this.start == this.input.length())
            return null;
        
        int end = this.start + 1;
        while (end <= this.input.length() && this.firstMatch(this.input.substring(this.start, end)) != null)
            ++end;
        
        String value = this.input.substring(this.start, end - 1);
        Token token = this.firstMatch(value);
        if (token == null)
            throw new Exception("Token não reconhecido");
        
        this.start = end - 1;
        if (!token.getName().equals("SPACE"))
            return new TokenValues(token, value);
        return this.nextToken();
    }

    private Token firstMatch(String value) {
        for(Token token : tokens)
        	if(token.match(value))
        		return token;
        return null;
    }
}

