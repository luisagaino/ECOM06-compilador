package br.edu.unifei.ecom06;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
	private final String name;
	private final Pattern pattern;
	
	public Token(String name, String pattern) {
		this.name = name;
		this.pattern = Pattern.compile(String.format("^%s$", pattern));
	}
	
	public boolean match(String value) {
		Matcher matcher = this.pattern.matcher(value);
		return matcher.matches();
	}
	
	public String getName() {
		return this.name;
	}
	
}
