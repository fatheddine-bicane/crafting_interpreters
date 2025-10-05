package jlox.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static final Interpreter INTERPRETER = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	public static void main(String[] args) throws IOException {
		// if no argument are passed to the interpreter
		if (1 < args.length) {
			System.out.println("Usage: jlox [script]");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));

		run(new String(bytes, Charset.defaultCharset()));
		if (hadError) System.exit(65);
		else if (hadRuntimeError) System.exit(70);
	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		while (true) {
			System.out.print("> ");
			String line = reader.readLine();
			if (null == line)
				break;
			run(line);
			hadError = false;
		}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser  = new Parser(tokens);
		List<Stmt> statements = parser.parse();

		// stop if there was a syntax error
		if (hadError) return;

		// interprete expression
		INTERPRETER.interpret(statements);

		// print expression
		// System.out.println(new AstPrinter().print(expression));
	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.out.println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, " at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() +
				"\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}
}
