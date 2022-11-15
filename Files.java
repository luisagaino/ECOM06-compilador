package br.edu.unifei.ecom06;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Files {
	private File file;
	private String name;
	
	public void create(String name) throws IOException {
		file = new File(name);
		setName(name);
		file.delete();
		if(file.createNewFile())
			System.out.println("Arquivo " + name + " criado com sucesso");
	}
	
	public void write(String code) throws IOException {
		FileWriter writer = new FileWriter(name, true);
		writer.write(code);
		writer.close();
	}
	
	public String read(String name) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileReader(name)).useDelimiter("\\n");
		String code = "";
		while(scanner.hasNext())
			code += scanner.next();
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
