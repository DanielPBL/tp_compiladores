package sintatico;

import java.io.IOException;
import lexico.Lexer;
import lexico.LexicalException;
import lexico.Tag;
import lexico.Token;

public class Syntaxer {

    private final Lexer lexer;
    private Token token;

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
            this.advance();
        } catch (IOException e) {
        }
    }

    private void eat(int tag) {
        if (this.token.tag == tag) {
            this.advance();
        } else {
            this.error();
        }
    }

    private void error() {
        SyntaticException se = new SyntaticException(Lexer.line, this.token, null);
        se.printError();
        System.exit(1);
    }

    public void program() {
        this.eat(Tag.INIT);
        this.declStmtList();
        this.eat(Tag.EOF);
    }
    
    public void declStmtList() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                this.z1();
                this.eat(';');
                this.stmtListTail();
                break;
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.z2();
                this.eat(';');
                this.stmtListTail();
                break;
            default:
                this.error();
        }
    }
    
    public void z1() {
        switch (this.token.tag) {
            case Tag.ATRIB:
                this.eat(Tag.ATRIB);
                this.simpleExpr();
                break;
            case ',':
            case Tag.IS:
                this.identListTail();
                this.eat(Tag.IS);
                this.type();
                this.eat(';');
                this.declListTail();
                break;
            default:
                this.error();
        }
    }

    public void z2() {
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
                this.error();
        }
    }

    public void declListTail() {
        switch (this.token.tag) {
            case Tag.ID:
                this.decl();
                this.eat(';');
                this.declListTail();
                break;
            case ';':
                break;
            default:
                this.error();
        }
    }

    public void decl() {
        switch (this.token.tag) {
            case Tag.ID:
                this.identList();
                this.eat(Tag.IS);
                this.type();
                break;
            default:
                this.error();
        }
    }

    public void identList() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                this.identListTail();
                break;
            default:
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
        }
    }

    public void expression() {
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
                this.error();
        }
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
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
                this.error();
        }
    }

    public void factor() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                break;
            case Tag.NUM:
                this.eat(Tag.NUM);
                break;
            case '(':
                this.eat('(');
                this.expression();
                this.eat(')');
                break;
            default:
                this.error();
        }
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
                this.error();
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
                this.error();
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
                this.error();
        }
    }

    public void constant() {
        switch (this.token.tag) {
            case Tag.NUM:
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                this.eat(Tag.STRING);
                break;
            default:
                this.error();
        }
    }

}
