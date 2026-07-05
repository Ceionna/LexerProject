# Lexer and Parser

## Overview
This project implements a lexer and parser that reads program input and determines whether the program is syntactically correct or contains grammar errors.

The lexer breaks the input into tokens, while the parser analyzes those tokens using recursive-descent parsing to validate the program’s structure.

## Features
- Reads program input and checks for syntax correctness
- Identifies syntax errors in invalid programs
- Tokenizes keywords, identifiers, operators, and literals
- Uses recursive-descent parsing to validate grammar rules
- Separates lexical analysis from syntax analysis

## How It Works
The program works in two main stages:

1. **Lexical Analysis**
   - Scans the input program
   - Converts characters into tokens
   - Recognizes keywords, identifiers, operators, literals, and symbols

2. **Parsing**
   - Takes the token stream from the lexer
   - Applies recursive-descent parsing
   - Checks whether the tokens follow the expected grammar rules
   - Reports whether the input is syntactically valid or contains errors

## Technologies Used
- Programming language: [Java]
- Parsing method: Recursive-descent parsing
