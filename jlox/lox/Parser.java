package jlox.lox;

import java.util.List;
import java.util.ArrayList;
import static jlox.lox.TokenType.*;

// NOTE: these down are rules that define how an expression in Lox is defined
// expression     → assignment ;
// assignment     → IDENTIFIER "=" assignment | equality ;
// equality       → comparison ( ( "!=" | "==" ) comparison )* ;
// comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
// term           → factor ( ( "-" | "+" ) factor )* ;
// factor         → unary ( ( "/" | "*" ) unary )* ;
// unary          → ( "!" | "-" ) unary
//                | primary ;
// primary        → NUMBER | STRING | "true" | "false" | "nil"
//                | "(" expression ")" | IDENTIFIER ;
// program        → statement* EOF ;
// declaration    → varDecl
//                | statement ;
// statement      → exprStmt
//                | printStmt
//                | block ;
// block          → "{" declaration* "}" ;
// varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
// exprStmt       → expression ";" ;
// printStmt      → "print" expression ";" ;

class Parser {
	private static class ParseError extends RuntimeException {}

	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}


	List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();
		while (!isAtEnd()) {
			statements.add(declaration());
		}
		return statements;
	}
	// Expr parse() {
	// 	try {
	// 		return expression();
	// 	} catch (ParseError error) {
	// 		return null;
	// 	}
	// }

	// expression     → equality ;
	private Expr expression() {
		return assignment();
	}

	private Stmt declaration() {
		try {
			if (match(VAR)) return varDeclaration();
			return statement();
		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt statement() {
		if (match(PRINT)) return printStatement();
		else if (match(LEFT_BRACE)) return new Stmt.Block(block());
		return expressionStatement();
	}

	// printStmt      → "print" expression ";" ;
	private Stmt printStatement() {
		Expr value = expression();
		consume(SEMICOLON, "Expect ';' after value.");
		return new Stmt.Print(value);
	}

	// varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
	private Stmt varDeclaration() {
		Token name = consume(IDENTIFIER, "Expect variable name.");

		Expr initializer = null;
		if (match(EQUAL)) {
			initializer = expression();
		}
		consume(SEMICOLON, "Expect ';' after variable declaration.");
		return new Stmt.Var(name, initializer);
	}

	// exprStmt       → expression ";" ;
	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(SEMICOLON, "Expect ';' after expression.");
		return new Stmt.Expression(expr);
	}

	// block          → "{" declaration* "}" ;
	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<>();

		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}
		consume(RIGHT_BRACE, "Expect '}' after block.");
		return statements;
	}

	// assignment     → IDENTIFIER "=" assignment | equality ;
	private Expr assignment() {
		Expr expr = equality();

		if (match(EQUAL)) {
			Token equals = previous();
			Expr value = assignment();
			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable)expr).name;
				return new Expr.Assign(name, value);
			}
			error(equals, "Invalid assignment target."); 
		}
		return expr;
	}

	// equality       → comparison ( ( "!=" | "==" ) comparison )* ;
	private Expr equality() {
		Expr expr = comparison();

		while (match(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	// comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
	private Expr comparison() {
		Expr expr = term();

		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	// term           → factor ( ( "-" | "+" ) factor )* ;
	private Expr term() {
		Expr expr = factor();

		while (match(MINUS, PLUS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	// factor         → unary ( ( "/" | "*" ) unary )* ;
	private Expr factor() {
		Expr expr = unary();

		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}

	// unary          → ( "!" | "-" ) unary
	private Expr unary() {
		if (match(BANG, MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}
		return primary();
	}

	// primary        → NUMBER | STRING | "true" | "false" | "nil"
	//                | "(" expression ")" | IDENTIFIER ;
	private Expr primary() {
		if (match(TRUE)) return new Expr.Literal(true);
		else if (match(FALSE)) return new Expr.Literal(false);
		else if (match(NIL)) return new Expr.Literal(null);

		else if (match(STRING, NUMBER))
			return new Expr.Literal(previous().literal);
		else if (match(IDENTIFIER)) {
			return new Expr.Variable(previous());
		}
		else if (match(LEFT_PAREN)) {
			Expr expr = expression();
			consume(RIGHT_PAREN, "Expect ')' after expression.");
			return new Expr.Grouping(expr);
		}
		throw error(peek(), "Expect expression.");
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();

		throw error(peek(), message);
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	private Token advance() {
		if (!isAtEnd()) current++;
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type == EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private ParseError error(Token token, String message) {
		Lox.error(token, message);
		return new ParseError();
	}


	// INFO: Syncronizing a ecursive descent parser:
	// if an error is encountered, this method discards
	// tokens until it thinks it has found a statement
	// boundary (;), ensuring that the user wont get plenty
	// of errors caused by one unexpected token found earlier.
	// and continue parsing the rest of the file.

	private void synchronize() {
		advance();
		while (!isAtEnd()) {
			if (previous().type == SEMICOLON) return;
			switch (peek().type) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}
			advance();
		}
	}

}
