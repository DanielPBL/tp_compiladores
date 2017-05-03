package sintatico;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        } catch (IOException | LexicalException ex) {
            Logger.getLogger(Syntaxer.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("Erro Sintático");
        System.exit(1);
    }

    public void program() {
        this.eat(Tag.INIT);
        this.programSuffix();
        this.eat(Tag.EOF);
    }

    public void programSuffix() {
        switch (this.token.tag) {
            case Tag.ID:
                this.eat(Tag.ID);
                this.declAssign();
                break;
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                this.otherStmt();
                break;
            default:
                this.error();
        }
    }

    public void declAssign() {
        switch (this.token.tag) {
            case Tag.ATRIB:
                this.eat(Tag.ATRIB);
                this.simpleExpr();
                this.eat(';');
                this.stmtListTail();
                this.eat(Tag.STOP);
                break;
            case ',':
            case Tag.IS:
                this.identListTail();
                this.eat(Tag.IS);
                this.type();
                this.eat(';');
                this.declListTail();
                this.stmtList();
                this.eat(Tag.STOP);
                break;
            default:
                this.error();
        }
    }

    public void otherStmt() {

    }

    public void stmtPrime() {

    }

    public void declListTail() {
    }

    public void decl() {
    }

    public void identList() {
    }

    public void identListTail() {
    }

    public void type() {
    }

    public void stmtList() {
    }

    public void stmtListTail() {
    }

    public void stmt() {
    }

    public void assignStmt() {
    }

    public void ifStmt() {
    }

    public void ifSuffix() {
    }

    public void condition() {
    }

    public void doStmt() {
    }

    public void doSuffix() {
    }

    public void readStmt() {
    }

    public void writeStmt() {
    }

    public void writeble() {
    }

    public void expression() {
    }

    public void expressionSuffix() {
    }

    public void simpleExpr() {
    }

    public void simpleExprPrime() {
    }

    public void term() {
    }

    public void termPrime() {
    }

    public void factorA() {
    }

    public void factor() {
    }

    public void relOp() {
    }

    public void appOp() {
    }

    public void mulOp() {
    }

    public void constant() {
    }

}
