package br.edu.unifei.ecom06;

import java.util.ArrayList;

public class Compiler {

	public static void main(String[] args) {
		if(args[0].equals(null))
			System.out.println("Arquivo não informado");
		else {
			String file = args[0];
			
			try {
                TokenValues token;
                Files files = new Files();
                String value = files.read(file);
                Lexer lexer = new Lexer(value);
                ArrayList<TokenValues> result = new ArrayList<TokenValues>();
                while ((token = lexer.nextToken()) != null)
                    result.add(token);
                Parser parser = new Parser(file);
                parser.verify(result);
            } catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
