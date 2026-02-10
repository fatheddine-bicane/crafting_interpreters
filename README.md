# JLox - A Tree-Walk Interpreter for the Lox Language

JLox is a Java implementation of an interpreter for the Lox programming language.

## About This Project

This project is developed by following the excellent book [**"Crafting Interpreters"**](https://craftinginterpreters.com/) by **Robert Nystrom**. The book provides a hands-on guide to building programming language interpreters from scratch, and this repository contains my implementation of the **JLox** interpreter (the tree-walk interpreter written in Java) as described in Part II of the book.

If you're interested in learning how interpreters and compilers work, I highly recommend reading the book — it's available for free online!

## Table of Contents

- [Installation](#installation)
- [Running the Interpreter](#running-the-interpreter)
- [Examples](#examples)
- [Language Features](#language-features)
  - [Data Types](#data-types)
  - [Variables](#variables)
  - [Operators](#operators)
  - [Control Flow](#control-flow)
  - [Functions](#functions)
  - [Built-in Functions](#built-in-functions)
- [Limitations](#limitations)

---

## Installation

### Method 1: Using Docker (Recommended)

The easiest way to run JLox is using the pre-built Docker image.

#### Prerequisites

- **Docker** installed on your machine

#### Running the Interpreter with Docker

**Interactive Mode (REPL):**
```bash
docker run -it fatheddinebicane/jlox:v1
```

**Running a Script File:**
```bash
docker run -v $(pwd):/scripts fatheddinebicane/jlox:v1 /scripts/your_script.lox
```

This mounts your current directory to `/scripts` inside the container, allowing you to run your local `.lox` files.

---

### Method 2: Building from Source

If you prefer to compile the interpreter on your machine:

#### Prerequisites

- **Java 21** or higher
- **Maven** (for building the project)

#### Building

1. Clone the repository:
   ```bash
   git clone git@github.com:fatheddine-bicane/crafting_interpreters.git
   cd crafting_interpreters
   ```

2. Build the project using Maven:
   ```bash
   mvn clean package
   ```

   This will compile the code and create the JAR file at `target/jlox-1.0.jar`.

---

## Running the Interpreter

> **Note:** If you're using Docker, see the [Docker instructions above](#method-1-using-docker-recommended).

### Interactive Mode (REPL)

Run the interpreter without any arguments to start the interactive prompt:

```bash
java -jar target/jlox-1.0.jar
```

You'll see a `>` prompt where you can type Lox code line by line:

```
> print "Hello, World!";
Hello, World!
> var x = 10;
> print x + 5;
15
```

### Running a Script File

Pass a `.lox` file as an argument to execute a script:

```bash
java -jar target/jlox-1.0.jar path/to/script.lox
```

---

## Examples

Check out the [`lox_scripts/`](lox_scripts/) folder for example programs showcasing various Lox features:

| File | Description |
|------|-------------|
| [`basics.lox`](lox_scripts/basics.lox) | Data types, variables, operators, and logical operations |
| [`control_flow.lox`](lox_scripts/control_flow.lox) | If-else statements, while loops, and for loops |
| [`functions.lox`](lox_scripts/functions.lox) | Function declaration, parameters, return values, and first-class functions |
| [`closures.lox`](lox_scripts/closures.lox) | Closures, factories, private state pattern, and function composition |
| [`recursion.lox`](lox_scripts/recursion.lox) | Recursive algorithms (factorial, fibonacci, Tower of Hanoi, etc.) |
| [`loop.lox`](lox_scripts/loop.lox) | Fibonacci sequence using a for loop |

Run any example with:

```bash
# Using Docker
docker run -v $(pwd):/scripts fatheddinebicane/jlox:v1 /scripts/lox_scripts/basics.lox

# Or locally
java -jar target/jlox-1.0.jar lox_scripts/basics.lox
```

---

## Language Features

### Data Types

Lox supports the following data types:

| Type | Description | Examples |
|------|-------------|----------|
| **Numbers** | Double-precision floating point | `42`, `3.14`, `-7.5` |
| **Strings** | Text enclosed in double quotes | `"Hello"`, `"Lox"` |
| **Booleans** | Logical true/false values | `true`, `false` |
| **Nil** | Represents the absence of a value | `nil` |

### Variables

Variables are declared using the `var` keyword. They can be optionally initialized at declaration:

```lox
// Declaration without initialization (defaults to nil)
var name;

// Declaration with initialization
var age = 25;
var greeting = "Hello";
var isActive = true;

// Reassignment
age = 26;
name = "John";
```

**Note:** All statements must end with a semicolon (`;`).

### Operators

#### Arithmetic Operators

```lox
var a = 10;
var b = 3;

print a + b;   // Addition: 13
print a - b;   // Subtraction: 7
print a * b;   // Multiplication: 30
print a / b;   // Division: 3.333...
print -a;      // Negation: -10
```

#### String Concatenation

```lox
var first = "Hello";
var second = "World";
print first + " " + second;  // "Hello World"
```

#### Comparison Operators

```lox
print 5 > 3;    // true
print 5 >= 5;   // true
print 3 < 5;    // true
print 3 <= 3;   // true
print 5 == 5;   // true
print 5 != 3;   // true
```

#### Logical Operators

```lox
print !true;           // false (NOT)
print true and false;  // false (AND)
print true or false;   // true (OR)
```

**Short-circuit evaluation:** `and` and `or` use short-circuit evaluation and return the value that determines the result.

### Control Flow

#### If-Else Statements

```lox
var age = 18;

if (age >= 18) {
    print "You are an adult";
} else {
    print "You are a minor";
}
```

#### While Loops

```lox
var i = 0;
while (i < 5) {
    print i;
    i = i + 1;
}
```

#### For Loops

```lox
for (var i = 0; i < 5; i = i + 1) {
    print i;
}
```

The for loop supports:
- Optional initializer (variable declaration or expression)
- Optional condition (defaults to `true` if omitted)
- Optional increment expression

### Functions

Functions are declared using the `fun` keyword:

```lox
// Simple function
fun greet() {
    print "Hello!";
}

greet();  // Call the function

// Function with parameters
fun add(a, b) {
    return a + b;
}

var result = add(3, 4);
print result;  // 7

// Function with return value
fun square(n) {
    return n * n;
}

print square(5);  // 25
```

#### Closures

Functions in Lox are first-class citizens and support closures:

```lox
fun makeCounter() {
    var count = 0;
    fun increment() {
        count = count + 1;
        return count;
    }
    return increment;
}

var counter = makeCounter();
print counter();  // 1
print counter();  // 2
print counter();  // 3
```

#### Recursion

Functions can call themselves recursively:

```lox
fun fibonacci(n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

print fibonacci(10);  // 55
```

### Built-in Functions

| Function | Description | Example |
|----------|-------------|---------|
| `clock()` | Returns the current time in seconds since the Unix epoch | `print clock();` |

---

## Limitations

> ⚠️ **Important:** This implementation has the following limitations:

1. **No Classes:** Object-oriented programming features (classes, inheritance, methods) are **not currently supported**.

2. **Limited I/O:** The `print` statement is the **only I/O operation available**. There is no support for:
   - Reading user input
   - File operations
   - Network operations

3. **No Arrays/Lists:** There are no built-in collection types.

4. **No Modules/Imports:** All code must be in a single file.

---

## Example Program

Here's a complete example demonstrating various features:

```lox
// Variables and data types
var name = "Lox";
var version = 1.0;
var isAwesome = true;

print "Welcome to " + name + "!";

// Functions
fun factorial(n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

// Loops
for (var i = 1; i <= 5; i = i + 1) {
    print factorial(i);
}

// Closures
fun createMultiplier(factor) {
    fun multiply(x) {
        return x * factor;
    }
    return multiply;
}

var double = createMultiplier(2);
var triple = createMultiplier(3);

print double(5);  // 10
print triple(5);  // 15
```

---

## License

This project is for educational purposes, following the "Crafting Interpreters" book.
