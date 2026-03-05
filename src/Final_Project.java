import java.util.*;
/*
README
WHAT THIS PROGRAM DOES
This program checks if an input program follows the grammar rules from the assignment.
It does NOT execute the program. It only checks if the syntax is correct.
If the input is correct, it prints:
Parse SUCCESS
If there is an error, it prints a message explaining what went wrong and shows the line and column number of the error.

HOW TO COMPILE
	1	Open Terminal.
	2	Go to the folder where Final_Project.java is saved.
	3	Run this command:
javac Final_Project.java
If there are no errors, the program will compile successfully.

HOW TO RUN
After compiling, run:
java Final_Project
Then:
	1	Type or paste your test program into the terminal.
	2	Make sure your program ends with:
end_program
	3	Press Enter.
The program will then print either:
Parse SUCCESS
OR
An error message explaining what is wrong.

EXAMPLE INPUT YOU CAN TEST
x = 5; y = 10; z = x + y; end_program

IMPORTANT RULES
	•	Every assignment must end with a semicolon ;
	•	if statements must end with end_if
	•	loop statements must end with end_loop
	•	The program must start with program
	•	The program must end with end_program

That’s it.Compile  —> Run —> Paste program —> Check output.
 */
public class Final_Project {

    enum TokenType {
        PROGRAM, END_PROGRAM, IF, END_IF, LOOP, END_LOOP,
        IDENT, NUMBER,
        ASSIGN, PLUS, MINUS, STAR, SLASH, PERCENT,
        LPAREN, RPAREN, SEMI, COLON,
        EQEQ, NOTEQ, GT, LT, GTE, LTE,
        EOF
    }

    static class Token {
        final TokenType type;
        final String lexeme;
        final int line;
        final int col;

        Token(TokenType type, String lexeme, int line, int col) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
            this.col = col;
        }

        @Override
        public String toString() {
            return type + "('" + lexeme + "')@" + line + ":" + col;
        }
    }

    static class Lexer {
        private final String src;
        private int i = 0;
        private int line = 1;
        private int col = 1;

        Lexer(String src) {
            this.src = src;
        }

        private boolean atEnd() {
            return i >= src.length();
        }

        private char peek() {
            return atEnd() ? '\0' : src.charAt(i);
        }

        private char advance() {
            char c = src.charAt(i++);
            if (c == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
            return c;
        }

        private void skipWhitespace() {
            while (!atEnd()) {
                char c = peek();
                if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                    advance();
                } else {
                    break;
                }
            }
        }

        private boolean isAlpha(char c) {
            return Character.isLetter(c) || c == '_';
        }

        private boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        private boolean isAlnum(char c) {
            return isAlpha(c) || isDigit(c);
        }

        Token nextToken() {
            skipWhitespace();
            if (atEnd()) return new Token(TokenType.EOF, "", line, col);

            int startLine = line;
            int startCol = col;

            char c = advance();

            if (isAlpha(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (isAlnum(peek())) sb.append(advance());
                String text = sb.toString();

                switch (text) {
                    case "program":     return new Token(TokenType.PROGRAM, text, startLine, startCol);
                    case "end_program": return new Token(TokenType.END_PROGRAM, text, startLine, startCol);
                    case "if":          return new Token(TokenType.IF, text, startLine, startCol);
                    case "end_if":      return new Token(TokenType.END_IF, text, startLine, startCol);
                    case "loop":        return new Token(TokenType.LOOP, text, startLine, startCol);
                    case "end_loop":    return new Token(TokenType.END_LOOP, text, startLine, startCol);
                    default:            return new Token(TokenType.IDENT, text, startLine, startCol);
                }
            }

            if (isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (isDigit(peek())) sb.append(advance());
                return new Token(TokenType.NUMBER, sb.toString(), startLine, startCol);
            }

            if (c == '=' && peek() == '=') { advance(); return new Token(TokenType.EQEQ, "==", startLine, startCol); }
            if (c == '!' && peek() == '=') { advance(); return new Token(TokenType.NOTEQ, "!=", startLine, startCol); }
            if (c == '>' && peek() == '=') { advance(); return new Token(TokenType.GTE, ">=", startLine, startCol); }
            if (c == '<' && peek() == '=') { advance(); return new Token(TokenType.LTE, "<=", startLine, startCol); }
            if (c == '=' && peek() == '<') { advance(); return new Token(TokenType.LTE, "=<", startLine, startCol); }

            switch (c) {
                case '=': return new Token(TokenType.ASSIGN, "=", startLine, startCol);
                case '+': return new Token(TokenType.PLUS, "+", startLine, startCol);
                case '-': return new Token(TokenType.MINUS, "-", startLine, startCol);
                case '*': return new Token(TokenType.STAR, "*", startLine, startCol);
                case '/': return new Token(TokenType.SLASH, "/", startLine, startCol);
                case '%': return new Token(TokenType.PERCENT, "%", startLine, startCol);
                case '(': return new Token(TokenType.LPAREN, "(", startLine, startCol);
                case ')': return new Token(TokenType.RPAREN, ")", startLine, startCol);
                case ';': return new Token(TokenType.SEMI, ";", startLine, startCol);
                case ':': return new Token(TokenType.COLON, ":", startLine, startCol);
                case '>': return new Token(TokenType.GT, ">", startLine, startCol);
                case '<': return new Token(TokenType.LT, "<", startLine, startCol);
            }

            throw new RuntimeException(
                    "LEXER ERROR at " + startLine + ":" + startCol +
                            " - Invalid character: '" + c + "'"
            );
        }

        List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();
            while (true) {
                Token t = nextToken();
                tokens.add(t);
                if (t.type == TokenType.EOF) break;
            }
            return tokens;
        }
    }

    static class Parser {
        private final List<Token> tokens;
        private int pos = 0;

        Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        private Token peek() {
            return tokens.get(pos);
        }

        private boolean atEnd() {
            return peek().type == TokenType.EOF;
        }

        private Token advance() {
            if (!atEnd()) pos++;
            return tokens.get(pos - 1);
        }

        private boolean check(TokenType t) {
            return peek().type == t;
        }

        private Token expect(TokenType t, String expectedHuman) {
            if (check(t)) return advance();
            Token got = peek();
            throw new RuntimeException(
                    "PARSER ERROR at " + got.line + ":" + got.col +
                            " - Expected " + expectedHuman +
                            " but found " + got.type + " ('" + got.lexeme + "')"
            );
        }

        void parseProgram() {
            expect(TokenType.PROGRAM, "'program'");

            while (!check(TokenType.END_PROGRAM)) {
                if (atEnd()) {
                    Token got = peek();
                    throw new RuntimeException(
                            "PARSER ERROR at " + got.line + ":" + got.col +
                                    " - Missing 'end_program' before end of file"
                    );
                }
                parseStatement();
            }

            expect(TokenType.END_PROGRAM, "'end_program'");
            expect(TokenType.EOF, "end of file");
        }

        private void parseStatement() {
            if (check(TokenType.IF)) {
                parseIf();
                return;
            }

            if (check(TokenType.LOOP)) {
                parseLoop();
                return;
            }

            parseAssignment();
            expect(TokenType.SEMI, "';'");
        }

        private void parseAssignment() {
            if (!check(TokenType.IDENT)) {
                Token got = peek();
                throw new RuntimeException(
                        "PARSER ERROR at " + got.line + ":" + got.col +
                                " - Expected identifier at start of assignment (identifiers cannot start with a digit)"
                );
            }

            advance();
            expect(TokenType.ASSIGN, "'='");
            parseExpr();
        }

        private void parseIf() {
            expect(TokenType.IF, "'if'");
            expect(TokenType.LPAREN, "'('");
            parseLogicExpr();
            expect(TokenType.RPAREN, "')'");

            while (!check(TokenType.END_IF)) {
                if (atEnd()) {
                    Token got = peek();
                    throw new RuntimeException(
                            "PARSER ERROR at " + got.line + ":" + got.col +
                                    " - Missing 'end_if' before end of file"
                    );
                }
                parseStatement();
            }

            expect(TokenType.END_IF, "'end_if'");
        }

        private void parseLoop() {
            expect(TokenType.LOOP, "'loop'");
            expect(TokenType.LPAREN, "'('");

            expect(TokenType.IDENT, "identifier");
            expect(TokenType.ASSIGN, "'='");

            parseVar();
            expect(TokenType.COLON, "':'");
            parseVar();

            expect(TokenType.RPAREN, "')'");

            while (!check(TokenType.END_LOOP)) {
                if (atEnd()) {
                    Token got = peek();
                    throw new RuntimeException(
                            "PARSER ERROR at " + got.line + ":" + got.col +
                                    " - Missing 'end_loop' before end of file"
                    );
                }
                parseStatement();
            }

            expect(TokenType.END_LOOP, "'end_loop'");
        }

        private void parseLogicExpr() {
            parseVar();
            parseRelop();
            parseVar();
        }

        private void parseRelop() {
            if (check(TokenType.EQEQ) || check(TokenType.NOTEQ) ||
                    check(TokenType.GT) || check(TokenType.LT) ||
                    check(TokenType.GTE) || check(TokenType.LTE)) {
                advance();
                return;
            }

            Token got = peek();
            throw new RuntimeException(
                    "PARSER ERROR at " + got.line + ":" + got.col +
                            " - Expected relational operator (==, !=, >, <, >=, <=, =<)"
            );
        }

        private void parseVar() {
            if (check(TokenType.IDENT) || check(TokenType.NUMBER)) {
                advance();
                return;
            }

            Token got = peek();
            throw new RuntimeException(
                    "PARSER ERROR at " + got.line + ":" + got.col +
                            " - Expected variable (identifier or number)"
            );
        }

        private void parseExpr() {
            parseTerm();
            while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
                advance();
                parseTerm();
            }
        }

        private void parseTerm() {
            parseFactor();
            while (check(TokenType.STAR) || check(TokenType.SLASH) || check(TokenType.PERCENT)) {
                advance();
                parseFactor();
            }
        }

        private void parseFactor() {
            if (check(TokenType.NUMBER) || check(TokenType.IDENT)) {
                advance();
                return;
            }

            if (check(TokenType.LPAREN)) {
                advance();
                parseExpr();
                expect(TokenType.RPAREN, "')'");
                return;
            }

            Token got = peek();
            throw new RuntimeException(
                    "PARSER ERROR at " + got.line + ":" + got.col +
                            " - Expected NUMBER, IDENT, or '(' expression ')'"
            );
        }
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        try (Scanner sc = new Scanner(System.in)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                sb.append(line).append("\n");
                if (line.trim().equals("end_program")) break;
            }
        }

        String input = sb.toString();

        try {
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            parser.parseProgram();

            System.out.println("Parse SUCCESS");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}