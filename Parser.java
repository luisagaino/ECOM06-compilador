package br.edu.unifei.ecom06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private Files file;
	private Files tokenFile;

	private List<TokenValues> tokens;

	public Parser(String name) {
		file = new Files();
		tokenFile = new Files();
		try {
			tokenFile.create(name.replace(".luisa", ".txt"));
			file.create(name.replace(".luisa", ".cpp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void verify(List<TokenValues> tokenList) throws Exception {
		tokens = new ArrayList<TokenValues>(tokenList);
		int n = tokens.size();

		for (int i = 0; i < tokens.size(); i++)
			tokenFile.write("(" + i + ") " + tokens.get(i).getToken().getName() + "\n");

		int main = 0;
		for (TokenValues token : tokens)
			if (token.getToken().getName().equals("MAIN"))
				main++;
		if (main < 1)
			throw new Exception("Não há main");
		else if (main > 1)
			throw new Exception("Há mais de uma main");

		file.write("#include <iostream>\n\nusing namespace std;");
		commands(0, n - 1);
	}

	private boolean validate(int position, String name) {
		return tokens.get(position).getToken().getName().equals(name);
	}

	private String name(int position) {
		return tokens.get(position).getToken().getName();
	}

	private String value(int position) {
		return tokens.get(position).getValue();
	}

	private void commands(int left, int right) throws Exception {
		int position = left;

		while (position < right) {
			switch (name(position)) {
			case "INTEGER_T":
				if (validate(position + 2, "INTEGER_T") || validate(position + 2, "REAL_T")
						|| validate(position + 2, "CHARACTER_T") || validate(position + 2, "BEGIN"))
					position = function(position);
				else
					position = declaration(position);
				break;
			case "REAL_T":
				if (validate(position + 2, "INTEGER_T") || validate(position + 2, "REAL_T")
						|| validate(position + 2, "CHARACTER_T") || validate(position + 2, "BEGIN"))
					position = function(position);
				else
					position = declaration(position);
				break;
			case "CHARACTER_T":
				if (validate(position + 2, "INTEGER_T") || validate(position + 2, "REAL_T")
						|| validate(position + 2, "CHARACTER_T") || validate(position + 2, "BEGIN"))
					position = function(position);
				else
					position = declaration(position);
				break;
			case "VOID":
				position = function(position);
				break;
			case "INPUT":
				position = input(position);
				break;
			case "OUTPUT":
				position = output(position);
				break;
			case "VARIABLE":
				file.write("\n\t");
				position = attribution(position);
				break;
			case "AT":
				file.write("\n\t");
				position = functionCall(position);
				break;
			case "IF":
				position = conditional(position);
				break;
			case "WHILE":
				position = repetitionWhile(position);
				break;
			case "FOR":
				position = repetitionFor(position);
				break;
			case "MAIN":
				position = program(position);
				break;
			case "RETURN":
				position = returns(position);
				break;
			default:
				throw new Exception("Token " + name(position) + " na posição " + position + " não reconhecido");
			}

			position++;
		}
	}

	private int getEndPosition(int begin) throws Exception {
		int beginCount = 0, endCount = 0, position = begin;
		if (validate(position, "BEGIN"))
			beginCount++;
		while (beginCount != endCount) {
			position++;
			if (validate(position, "BEGIN"))
				beginCount++;
			if (validate(position, "END"))
				endCount++;
			if (position > tokens.size())
				throw new Exception("Token de final da função não encontrado");
		}
		return position;
	}

	private int function(int position) throws Exception {
		// tipo VARIABLE listaParametros BEGIN comandos END
		switch (name(position)) {
		case "INTEGER_T":
			file.write("\n\nint ");
			break;
		case "REAL_T":
			file.write("\n\ndouble ");
			break;
		case "CHARACTER_T":
			file.write("\n\nchar ");
			break;
		case "VOID":
			file.write("\n\nvoid ");
			break;
		default:
			throw new Exception("Tipo não reconhecido");
		}

		if (!validate(++position, "VARIABLE"))
			throw new Exception("Nome de função inválido");
		file.write(value(position) + "(");

		position++;
		int aux;
		if (validate(position, "BEGIN")) {
			file.write(") {");
			aux = getEndPosition(position);
		} else {
			position = parameters(position);
			if (!validate(position, "BEGIN"))
				throw new Exception("Token de início de função inválido");
			file.write(") {");
			aux = getEndPosition(position);
		}

		position++;
		if (position != aux)
			commands(position, aux - 1);
		file.write("\n}");

		return aux;
	}

	private int returns(int position) throws Exception {
		// RETURN arith.
		if (!validate(position, "RETURN"))
			throw new Exception("Token de retorno inválido");
		file.write("\n\treturn ");

		position = arithmeticExpression(++position);

		if (!validate(++position, "PERIOD"))
			throw new Exception("Símbolo não reconhecido");
		file.write(";");

		return position;
	}

	private int parameters(int position) throws Exception {
		// tipo VARIABLE, tipo VARIABLE...
		while (true) {
			switch (name(position)) {
			case "INTEGER_T":
				file.write("int ");
				break;
			case "REAL_T":
				file.write("double ");
				break;
			case "CHARACTER_T":
				file.write("char ");
				break;
			default:
				throw new Exception("Tipo não reconhecido");
			}

			if (!validate(++position, "VARIABLE"))
				throw new Exception("Nome de variável inválido");
			file.write(value(position));

			position++;
			if (validate(position, "SEMICOLON")) {
				file.write(", ");
				position++;
				continue;
			} else if (validate(position, "BEGIN"))
				break;
			else
				throw new Exception("Lista de parâmetros não reconhecida");
		}

		return position;
	}

	private int declaration(int position) throws Exception {
		// tipo VARIABLE.
		// tipo VARIABLE: valor.
		switch (name(position)) {
		case "INTEGER_T":
			file.write("\n\tint ");
			break;
		case "REAL_T":
			file.write("\n\tdouble ");
			break;
		case "CHARACTER_T":
			file.write("\n\tchar ");
			break;
		default:
			throw new Exception("Tipo não reconhecido");
		}

		if (!validate(++position, "VARIABLE"))
			throw new Exception("Nome de variável inválido");
		file.write(value(position));

		switch (name(++position)) {
		case "PERIOD":
			file.write(";");
			break;
		case "COLON":
			file.write(" = ");
			switch (name(++position)) {
			case "INTEGER":
				file.write(value(position));
				break;
			case "REAL":
				file.write(value(position));
				break;
			case "CHARACTER":
				file.write(value(position));
				break;
			default:
				throw new Exception("Tipo não reconhecido");
			}
			if (!validate(++position, "PERIOD"))
				throw new Exception("Símbolo não reconhecido");
			file.write(";");
			break;
		default:
			throw new Exception("Símbolo não reconhecido");
		}

		return position;
	}

	private int input(int position) throws Exception {
		// INPUT VARIABLE
		if (!validate(position, "INPUT"))
			throw new Exception("Token de entrada não reconhecido");
		file.write("\n\tcin >> ");

		if (!validate(++position, "VARIABLE"))
			throw new Exception("Nome de variável inválido");
		file.write(value(position));

		if (!validate(++position, "PERIOD"))
			throw new Exception("Símbolo não reconhecido");
		file.write(";");

		return position;
	}

	private int output(int position) throws Exception {
		// OUTPUT arithmeticExpression
		if (!validate(position, "OUTPUT"))
			throw new Exception("Token de saída não reconhecido");
		file.write("\n\tcout << ");

		position++;
		if (validate(position, "AT")) {
			position = arithmeticExpression(position);
			return position;
		} else if (validate(position, "DOUBLEQUOTE")) {
			file.write("\"");
			while(validate(position + 1, "VARIABLE") || validate(position + 1, "INTEGER")) {
				file.write(value(++position) + " ");
			}
			if(!validate(++position, "DOUBLEQUOTE"))
				throw new Exception("Aspas duplas não fechadas");
			file.write("\"");
		} else
			position = arithmeticExpression(position);

		if (!validate(++position, "PERIOD")) {
			System.out.println(name(position));
			throw new Exception("Símbolo não reconhecido");
		}
		file.write(";");

		return position;
	}

	private int functionCall(int position) throws Exception {
		// @VARIABLE parameters.
		if (!validate(position, "AT"))
			throw new Exception("Símbolo de chamadas de função não reconhecido");

		if (!validate(++position, "VARIABLE"))
			throw new Exception("Nome da função inválido");
		file.write(value(position) + "(");

		while (true) {
			position++;
			if (validate(position, "VARIABLE") || validate(position, "INTEGER") || validate(position, "REAL")) {
				position = arithmeticExpression(position);
				if (validate(position + 1, "SEMICOLON")) {
					file.write(", ");
					position++;
				}
			} else if (validate(position, "PERIOD")) {
				file.write(");");
				break;
			} else if (validate(position, "OPENPARENTHESIS")) {
				position = arithmeticExpression(position);
				if (validate(position + 1, "SEMICOLON")) {
					file.write(", ");
					position++;
				} else {
					if (validate(position + 1, "PERIOD")) {
						file.write(");");
						position++;
						break;
					} else {
						file.write(")");
						break;
					}
				}

			} else
				throw new Exception("Parâmetros inválidos");
		}

		return position;
	}

	private int attribution(int position) throws Exception {
		// VARIABLE: (arithmeticExpression/CHARACTER/functionCall)
		if (!validate(position, "VARIABLE"))
			throw new Exception("Nome de variável inválido");
		file.write(value(position));

		if (!validate(++position, "COLON"))
			throw new Exception("Símbolo de atribuição não reconhecido");
		file.write(" = ");

		position++;
		if (validate(position, "CHARACTER"))
			file.write(value(position));
		else if (validate(position, "AT")) {
			position = functionCall(position);
			return position;
		} else {
			position = arithmeticExpression(position);

			if (!validate(++position, "PERIOD"))
				throw new Exception("Símbolo não reconhecido");
			file.write(";");
		}

		return position;
	}

	private int conditional(int position) throws Exception {
		// IF expLog BEGIN comandos END ELSE BEGIN comandos END
		if (!validate(position, "IF"))
			throw new Exception("Condicional inválido");
		file.write("\n\tif(");

		position = logicalExpression(++position);

		if (!validate(++position, "BEGIN"))
			throw new Exception("Token de início da função inválido");
		int aux = getEndPosition(position);
		file.write(") {");

		position++;
		if (position != aux)
			commands(position, aux - 1);
		file.write("\n\t}");

		position = aux;
		if (validate(position + 1, "ELSE")) {
			position++;
			file.write(" else ");

			if (!validate(++position, "BEGIN"))
				throw new Exception("Token de início da função inválido");
			file.write(" {");
			aux = getEndPosition(position);

			position++;
			if (position != aux)
				commands(position, aux - 1);
			file.write("\n\t}");
		}
		position = aux;

		return position;
	}

	private int repetitionWhile(int position) throws Exception {
		// WHILE expLog BEGIN comandos END
		if (!validate(position, "WHILE"))
			throw new Exception("While inválido");
		file.write("\n\twhile(");

		position = logicalExpression(++position);

		if (!validate(++position, "BEGIN"))
			throw new Exception("Token de início da função inválido");
		int aux = getEndPosition(position);
		file.write(") {");

		position++;
		if (position != aux)
			commands(position, aux - 1);
		file.write("\n\t}");

		return aux;
	}

	private int repetitionFor(int position) throws Exception {
		// FOR atribuicao expLog PONTO atribuicao BEGIN comandos END
		if (!validate(position, "FOR"))
			throw new Exception("For inválido");
		file.write("\n\tfor(");

		position = attribution(++position);
		file.write(" ");
		position = logicalExpression(++position);
		if (!validate(++position, "PERIOD"))
			throw new Exception("Símbolo não reconhecido");
		file.write("; ");
		position = attribution(++position);

		if (!validate(++position, "BEGIN"))
			throw new Exception("Token de início da função inválido");
		int aux = getEndPosition(position);
		file.write(") {");

		position++;
		if (position != aux)
			commands(position, aux - 1);
		file.write("\n\t}");

		return aux;
	}

	private int program(int position) throws Exception {
		// MAIN BEGIN comandos END
		if (!validate(position, "MAIN"))
			throw new Exception("Token de main inválido");
		file.write("\n\nint main()");

		if (!validate(++position, "BEGIN"))
			throw new Exception("Token de início da função inválido");
		file.write(" {");
		int aux = getEndPosition(position);

		position++;
		if (position != aux)
			commands(position, aux - 1);
		file.write("\n\treturn 0;\n}");

		return aux;
	}

	private int arithmeticExpression(int position) throws Exception {
		// ( arithmeticExpression )
		// arithmeticExpression +/-/*///% arithmeticExpression
		// INTEGER/REAL/VARIABLE
		String[] operations = new String[] { "ADD", "SUB", "MUL", "DIV", "MOD" };

		if (validate(position, "OPENPARENTHESIS")) {
			file.write("(");
			position = arithmeticExpression(++position);
			if (!validate(++position, "CLOSEPARENTHESIS"))
				throw new Exception("Parêntesis não fechado");
			file.write(")");
			if (validate(position + 1, "INTEGER") || validate(position + 1, "REAL"))
				if (value(++position).startsWith("-"))
					file.write(" - " + value(position).replace("-", ""));
			for (String operation : operations)
				if (validate(position + 1, operation)) {
					file.write(" " + value(++position) + " ");
					position = arithmeticExpression(++position);
				}
		} else if (validate(position, "AT")) {
			position = functionCall(position);
			if (validate(position + 1, "INTEGER") || validate(position + 1, "REAL"))
				if (value(++position).startsWith("-"))
					file.write(" - " + value(position).replace("-", ""));
			for (String operation : operations)
				if (validate(position + 1, operation)) {
					file.write(" " + value(++position) + " ");
					position = arithmeticExpression(++position);
				}
		} else if (validate(position, "INTEGER") || validate(position, "REAL") || validate(position, "VARIABLE")) {
			file.write(value(position));
			if (validate(position + 1, "INTEGER") || validate(position + 1, "REAL"))
				if (value(++position).startsWith("-"))
					file.write(" - " + value(position).replace("-", ""));
			for (String operation : operations)
				if (validate(position + 1, operation)) {
					file.write(" " + value(++position) + " ");
					position = arithmeticExpression(++position);
				}
		} else
			throw new Exception("Expressão aritmética incorreta");

		return position;
	}

	private int relationalExpression(int position) throws Exception {
		// arithmeticExpression operator arithmeticExpression
		String[] operators = new String[] { "EQUAL", "GREATEREQUAL", "LESSEQUAL", "GREATER", "LESS", "NOTEQUAL" };
		boolean valid = false;

		position = arithmeticExpression(position);

		for (String operator : operators)
			if (validate(position + 1, operator)) {
				if (validate(++position, "EQUAL"))
					file.write(" " + value(position) + value(position) + " ");
				else
					file.write(" " + value(position) + " ");

				valid = true;
			}

		if (!valid)
			throw new Exception("Expressão relacional inválida");

		position = arithmeticExpression(++position);

		return position;
	}

	private int logicalExpression(int position) throws Exception {
		// (expLog)
		// expLog AND/OR expLog
		// NOT expLog
		// expRel

		if (validate(position, "OPENPARENTHESIS")) {
			file.write("(");
			position = logicalExpression(++position);
			if (!validate(++position, "CLOSEPARENTHESIS"))
				throw new Exception("Parêntesis não fechado");
			file.write(")");
			if (validate(position + 1, "AND") || validate(position + 1, "OR")) {
				file.write(" " + value(++position) + value(position) + " ");
				position = logicalExpression(++position);
			}
		} else if (validate(position, "NOT")) {
			file.write("!");
			position = logicalExpression(++position);
		} else {
			position = relationalExpression(position);
			if (validate(position + 1, "AND") || validate(position + 1, "OR")) {
				file.write(" " + value(++position) + value(position) + " ");
				position = logicalExpression(++position);
			}
		}

		return position;
	}

}
