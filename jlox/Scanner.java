package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static jlox.TokenType.*;

class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();

	// INFO: these fields are to hel the loop keep track
	// of where the scanner is in the source code
	private int start = 0;
	private int current = 0;
	private int line = 1;

	// constractor
	Scanner(String source) {
		// source is the source code
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme
			start = current;
			scanToken();
		}
		tokens.add(new Token(EOF, "", null, line));
		return (tokens);
	}

	private void scanToken() {
		char c = advance();

		switch (c) {
			// single characters tokenizing
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
	}
	// consume the current character
	private char advance() {
		return (source.charAt(current++));
	}

	// adds a token to the list tokens using the the method below
	private void addToken(TokenType type) {
		addToken(type, null);
	}
}
