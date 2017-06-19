package sintatico;

import java.io.IOException;
import lexico.Lexer;
import lexico.LexicalException;
import lexico.Tag;
import lexico.Token;
import lexico.Word;
import semantico.Type;

public class Syntaxer {

    private final Lexer lexer;
    private Token token;

    public boolean success = true;

    public Syntaxer(Lexer lexer) {
        this.lexer = lexer;
        this.advance();
    }

    private void advance() {
        try {
            this.token = this.lexer.scan();
        } catch (LexicalException e) {
            token = e.getToken();
            e.printError();
            this.success = false;
            this.advance();
        } catch (IOException e) {
        }
    }

    private void eat(int tag) {
        if (this.token.tag == tag) {
            this.advance();
        } else {
            int[] expected = {tag};
            this.skipTo(expected, expected);
            this.advance();
        }
    }

    private boolean contains(Token token, int[] follow) {
        for (int tag : follow) {
            if (tag == token.tag) {
                return true;
            }
        }

        return false;
    }

    private void skipTo(int[] expected, int[] follow) {
        SyntaticException se = new SyntaticException(Lexer.line, this.token, expected);
        se.printError();

        //Evitar erro de EOF propagando pela pilha de recursão
        if (this.token.tag == Tag.EOF) {
            System.out.println("Análise sintática terminada com erro(s).");
            System.exit(1);
        }

        this.success = false;

        do {
            this.advance();
        } while (!this.contains(this.token, follow) && this.token.tag != Tag.EOF);
    }

    public void program() {
        switch (this.token.tag) {
            case Tag.INIT:
                this.eat(Tag.INIT);
                this.declStmtList();
                this.eat(Tag.STOP);
                this.eat(Tag.EOF);
                break;
            default:
                int[] expected = {Tag.INIT};
                int[] follow = {Tag.EOF};
                this.skipTo(expected, follow);
        }
    }

    public void declStmtList() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                this.assignOrDecl();
                break;
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.stmtNoAssign();
                this.eat(';');
                this.stmtListTail();
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }
    }

    public void assignOrDecl() {
        switch (this.token.tag) {
            case Tag.ATRIB:
                this.eat(Tag.ATRIB);
                this.simpleExpr();
                this.eat(';');
                this.stmtListTail();
                break;
            case ',':
            case Tag.IS:
                this.identListTail();
                this.eat(Tag.IS);
                this.type();
                this.eat(';');
                this.declStmtListTail();
                break;
            default:
                int[] expected = {Tag.ATRIB, ',', Tag.IS};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }
    }

    public void stmtNoAssign() {
        switch (this.token.tag) {
            case Tag.IF:
                this.ifStmt();
                break;
            case Tag.DO:
                this.doStmt();
                break;
            case Tag.READ:
                this.readStmt();
                break;
            case Tag.WRITE:
                this.writeStmt();
                break;
            default:
                int[] expected = {Tag.IF, Tag.DO, Tag.READ, Tag.WRITE, Tag.STOP};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void declStmtListTail() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.declStmtList();
                break;
            case Tag.STOP:
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }
    }

    public void identListTail() {
        switch (this.token.tag) {
            case ',':
                this.eat(',');
                this.eat(Tag.ID);
                this.identListTail();
                break;
            case Tag.IS:
                break;
            default:
                int[] expected = {',', Tag.IS};
                int[] follow = {Tag.IS};
                this.skipTo(expected, follow);
        }
    }

    public void type() {
        switch (this.token.tag) {
            case Tag.T_INTEGER:
                this.eat(Tag.T_INTEGER);
                break;
            case Tag.T_STRING:
                this.eat(Tag.T_STRING);
                break;
            default:
                int[] expected = {Tag.T_INTEGER, Tag.T_STRING};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void stmtList() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.stmt();
                this.eat(';');
                this.stmtListTail();
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.END, Tag.WHILE};
                this.skipTo(expected, follow);
        }
    }

    public void stmtListTail() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.stmt();
                this.eat(';');
                this.stmtListTail();
                break;
            case Tag.END:
            case Tag.WHILE:
            case Tag.STOP:
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE, Tag.END, Tag.WHILE, Tag.STOP};
                int[] follow = {Tag.END, Tag.STOP, Tag.WHILE};
                this.skipTo(expected, follow);
        }
    }

    public void stmt() {
        switch (this.token.tag) {
            case Tag.ID:
                this.assignStmt();
                break;
            case Tag.IF:
                this.ifStmt();
                break;
            case Tag.DO:
                this.doStmt();
                break;
            case Tag.READ:
                this.readStmt();
                break;
            case Tag.WRITE:
                this.writeStmt();
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void assignStmt() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                this.eat(Tag.ATRIB);
                this.simpleExpr();
                break;
            default:
                int[] expected = {Tag.ID};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void ifStmt() {
        switch (this.token.tag) {
            case Tag.IF:
                this.eat(Tag.IF);
                this.eat('(');
                this.condition();
                this.eat(')');
                this.eat(Tag.BEGIN);
                this.stmtList();
                this.eat(Tag.END);
                this.ifSuffix();
                break;
            default:
                int[] expected = {Tag.IF};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void ifSuffix() {
        switch (this.token.tag) {
            case Tag.ELSE:
                this.eat(Tag.ELSE);
                this.eat(Tag.BEGIN);
                this.stmtList();
                this.eat(Tag.END);
                break;
            case ';':
                break;
            default:
                int[] expected = {Tag.ELSE, ';'};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void condition() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                this.expression();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }
    }

    public void doStmt() {
        switch (this.token.tag) {
            case Tag.DO:
                this.eat(Tag.DO);
                this.stmtList();
                this.doSuffix();
                break;
            default:
                int[] expected = {Tag.DO};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void doSuffix() {
        switch (this.token.tag) {
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.eat('(');
                this.condition();
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.WHILE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void readStmt() {
        switch (this.token.tag) {
            case Tag.READ:
                this.eat(Tag.READ);
                this.eat('(');
                this.eat(Tag.ID);
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.READ};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void writeStmt() {
        switch (this.token.tag) {
            case Tag.WRITE:
                this.eat(Tag.WRITE);
                this.eat('(');
                this.writable();
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.WRITE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }
    }

    public void writable() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                this.simpleExpr();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }
    }

    public Type expression() {
        Type type = Type.ERROR;
        
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                this.simpleExpr();
                this.expressionSuffix();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }
        
        return type;
    }

    public void expressionSuffix() {
        switch (this.token.tag) {
            case '>':
            case '=':
            case Tag.GTE:
            case '<':
            case Tag.LTE:
            case Tag.DIFF:
                this.relOp();
                this.simpleExpr();
                break;
            case ')':
                break;
            default:
                int[] expected = {'>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF, ')'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }
    }

    public void simpleExpr() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                this.term();
                this.simpleExprTail();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
    }

    public void simpleExprTail() {
        switch (this.token.tag) {
            case '+':
            case '-':
            case Tag.OR:
                this.addOp();
                this.term();
                this.simpleExprTail();
                break;
            case ';':
            case ')':
            case '>':
            case '=':
            case Tag.GTE:
            case '<':
            case Tag.LTE:
            case Tag.DIFF:
                break;
            default:
                int[] expected = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
    }

    public void term() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                this.factorA();
                this.termTail();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
    }

    public void termTail() {
        switch (this.token.tag) {
            case '*':
            case '/':
            case Tag.AND:
                this.mulOp();
                this.factorA();
                this.termTail();
                break;
            case '+':
            case '-':
            case Tag.OR:
            case ';':
            case ')':
            case '>':
            case '=':
            case Tag.GTE:
            case '<':
            case Tag.LTE:
            case Tag.DIFF:
                break;
            default:
                int[] expected = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
    }

    public void factorA() {
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
                this.factor();
                break;
            case Tag.NOT:
                this.eat(Tag.NOT);
                this.factor();
                break;
            case '-':
                this.eat('-');
                this.factor();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
    }

    public Type factor() {
        Type type = Type.ERROR;

        switch (this.token.tag) {
            case Tag.ID:
                type = ((Word) this.token).getType();
                if (type == Type.NULL) {
                    //TODO: Excessão semântica, identificador não declarado.
                    type = Type.ERROR;
                }
                this.eat(Tag.ID);
                break;
            case Tag.NUM:
                type = Type.INTEGER;
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                type = Type.STRING;
                this.eat(Tag.STRING);
                break;
            case '(':
                this.eat('(');
                type = this.expression();
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '('};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return type;
    }

    public void relOp() {
        switch (this.token.tag) {
            case '=':
                this.eat('=');
                break;
            case '>':
                this.eat('>');
                break;
            case Tag.GTE:
                this.eat(Tag.GTE);
                break;
            case '<':
                this.eat('<');
                break;
            case Tag.LTE:
                this.eat(Tag.LTE);
                break;
            case Tag.DIFF:
                this.eat(Tag.DIFF);
                break;
            default:
                int[] expected = {'>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }
    }

    public void addOp() {
        switch (this.token.tag) {
            case '+':
                this.eat('+');
                break;
            case '-':
                this.eat('-');
                break;
            case Tag.OR:
                this.eat(Tag.OR);
                break;
            default:
                int[] expected = {Tag.OR, '+', '-'};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }
    }

    public void mulOp() {
        switch (this.token.tag) {
            case '*':
                this.eat('*');
                break;
            case '/':
                this.eat('/');
                break;
            case Tag.AND:
                this.eat(Tag.AND);
                break;
            default:
                int[] expected = {'*', '/', Tag.AND};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }
    }

    public Type constant() {
        Type type = Type.ERROR;

        switch (this.token.tag) {
            case Tag.NUM:
                type = Type.INTEGER;
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                type = Type.STRING;
                this.eat(Tag.STRING);
                break;
            default:
                int[] expected = {Tag.NUM, Tag.STRING};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return type;
    }

}
