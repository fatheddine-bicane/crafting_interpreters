package jlox.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static jlox.lox.TokenType.*;

class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private static final Map <String, TokenType> keywords;

	// INFO: these fields are to hel the loop keep track
	// of where the scanner is in the source code
	private int start = 0;
	private int current = 0;
	private int line = 1;

	static {
		keywords = new HashMap<>();
		keywords.put("and",    AND);
		keywords.put("class",  CLASS);
		keywords.put("else",   ELSE);
		keywords.put("false",  FALSE);
		keywords.put("for",    FOR);
		keywords.put("fun",    FUN);
		keywords.put("if",     IF);
		keywords.put("nil",    NIL);
		keywords.put("or",     OR);
		keywords.put("print",  PRINT);
		keywords.put("return", RETURN);
		keywords.put("super",  SUPER);
		keywords.put("this",   THIS);
		keywords.put("true",   TRUE);
		keywords.put("var",    VAR);
		keywords.put("while",  WHILE);
	}

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
			// string (longer lexeme)
			case '"': string(); break;
			// source code contain a charachter unsupported in Lox
			default:
				if (isDigit(c)) {
					number();
				} else if (isAlpha(c)) {
					identifier();
				} else {
					Lox.error(line, "Unexpected character.");
				}
				break;
		}
	}

	private void identifier() {
		// consume the identifier
		while (isAlphaNumeric(peek())) advance();

		// NOTE: we cant tokenize keywords like we did for '=' because:
		// the user might name a variable 'orichid' and if did tokenize
		// like befaure we will match the first two characters 'or' and
		// leave 'ichid'. To fix that we use a concept called "maximal munch"
		// will consume the entier word and check if its a keyword
		// wee will use a hashmap for that (look at the keywords object above)
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		addToken(type);
	}

	private void number() {
		// consume digits
		while (isDigit(peek())) advance();
		// consume '.' if it exist and followd by a digit
		if (peek() == '.' && isDigit(peekNext())) advance();
		// consume the digits after '.'
		while (isDigit(peek())) advance();
		// add the token
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	private void string() {
		// consume characters aslong as the quote didnt close
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') line++;
			advance();
		}
		// end reached and quote not closed
		if (isAtEnd()) {
			Lox.error(line, "Unterminated string");
		}
		// peeked '"' (quote closed)
		advance();
		// trim sourounding quotes
		String value = source.substring(start + 1, current - 1);
		addToken(STRING, value);
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

	// same as peek() (the function above) but peeks at character in position curent + 1
	private char peekNext() {
		if (current + 1 >= source.length()) return ('\0');
		return (source.charAt(current + 1));
	}

	private boolean isAlpha(char c) {
		return ((c >= 'a' && c <= 'z') ||
				(c >= 'A' && c <= 'Z') ||
				(c == '-'));
	}

	private boolean isAlphaNumeric(char c) {
		return (isDigit(c) || isAlpha(c));
	}

	// check a character if its a digit
	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	// consume the current character
	private char advance() {
		return (source.charAt(current++));
	}

	// adds a non value token to the list tokens
	private void addToken(TokenType type) {
		addToken(type, null);
	}

	// add a token to the tokens list 
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

	// checks if the the end of source reached
	private boolean isAtEnd() {
		return (current >= source.length());
	}
}
