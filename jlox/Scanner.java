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

}
