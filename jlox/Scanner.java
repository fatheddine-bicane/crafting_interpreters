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
			// check if '=' appeared after (!,<,> or =) if not its an assignment
			case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
			case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
			case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
			case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
			// check if '/' is followd by '/' that wpuld be a comment
			case '/':
				if (match('/')) {
					// comment goes all the way to '\n'
					while (peek() != '\n' && !isAtEnd()) {
						advance();
					}
				} else {
					addToken(SLASH);
				}
			// skip meaningless characters (spaces tabse...)
			case ' ':
			case '\t':
			case '\r':
				break;
			// if encountring newLine skip and increment line
			case '\n':
				line++;
				break;
	}

	// check if the charcter at the current position matches the expected
	private boolean match(char expected) {
		if (isAtEnd()) return (false);
		if (source.charAt(current) != expected) return (false);

		current++;
		return (true);
	}

	// take a peek and return the char at the current position
	private char peek() {
		if (isAtEnd()) return ('\0');
		return (source.charAt(current));
	}
	// consume the current character
	private char advance() {
		return (source.charAt(current++));
	}

	// adds a non value token to the list tokens
	private void addToken(TokenType type) {
		addToken(type, null);
	}
}
