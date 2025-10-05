package jlox.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
	final Environment ENCLOSING;
	private final Map<String, Object> VALUES = new HashMap<>();

	Environment() {
		ENCLOSING = null;
	}

	Environment(Environment enclosing) {
		ENCLOSING = enclosing;
	}

	Object get(Token name) {
		if (VALUES.containsKey(name.lexeme)) {
			return VALUES.get(name.lexeme);
		}
		else if (ENCLOSING != null) return ENCLOSING.get(name);
		throw new RuntimeError(name,
				"Undefined variable '" + name.lexeme + "'.");
	}

	void assign(Token name, Object value) {
		if (VALUES.containsKey(name.lexeme)) {
			VALUES.put(name.lexeme, value);
			return;
		}
		else if (ENCLOSING != null) {
			ENCLOSING.assign(name, value);
			return;
		}
		throw new RuntimeError(name,
				"Undefined variable '" + name.lexeme + "'.");
	}

	void define(String name, Object value) {
		VALUES.put(name, value);
	}
}
