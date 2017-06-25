package sintatico;

import java.io.IOException;
import lexico.Lexer;
import lexico.LexicalException;
import lexico.Tag;
import lexico.Token;
import lexico.Word;
import semantico.AssignCommand;
import semantico.Command;
import semantico.DeclarationCommand;
import semantico.Expression;
import semantico.Operation;
import semantico.SemanticException;
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

    public Command program() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.INIT:
                this.eat(Tag.INIT);
                command.type = this.declStmtList().type;
                this.eat(Tag.STOP);
                this.eat(Tag.EOF);
                break;
            default:
                int[] expected = {Tag.INIT};
                int[] follow = {Tag.EOF};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command declStmtList() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ID:
                Word id = (Word) this.token;
                this.eat(Tag.ID);
                command = this.assignOrDecl();
                if (command instanceof DeclarationCommand) { //Declaração
                    DeclarationCommand dc = (DeclarationCommand) command;
                    dc.add(id);
                    dc.resolve();
                } else { //Atribuição
                    AssignCommand ac = (AssignCommand) command;
                    if (ac.assignType.type == id.type.type) {
                        command.type = ac.type;
                    }
                }
                break;
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                Command c1;
                Command c2;
                c1 = this.stmtNoAssign();
                this.eat(';');
                c2 = this.stmtListTail();
                if (c1.type.type == Type.NULL && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                }
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command assignOrDecl() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ATRIB:
                this.eat(Tag.ATRIB);
                Expression expr = this.simpleExpr();
                this.eat(';');
                Command c1 = this.stmtListTail();
                command = new AssignCommand(expr.type, c1.type);
                break;
            case ',':
            case Tag.IS:
                DeclarationCommand dc = this.identListTail();
                this.eat(Tag.IS);
                Type type = this.type();
                this.eat(';');
                Command c2 = this.declStmtListTail();
                break;
            default:
                int[] expected = {Tag.ATRIB, ',', Tag.IS};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command stmtNoAssign() {
        Command command = new Command();

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

        return command;
    }

    public Command declStmtListTail() {
        Command command = new Command();

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

        return command;
    }

    public DeclarationCommand identListTail() {
        DeclarationCommand dc = new DeclarationCommand();

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

        return dc;
    }

    public Type type() {
        Type type = new Type(Type.ERROR);

        switch (this.token.tag) {
            case Tag.T_INTEGER:
                type.type = Type.INTEGER;
                this.eat(Tag.T_INTEGER);
                break;
            case Tag.T_STRING:
                type.type = Type.STRING;
                this.eat(Tag.T_STRING);
                break;
            default:
                int[] expected = {Tag.T_INTEGER, Tag.T_STRING};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return type;
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

    public Command stmtListTail() {
        Command command = new Command();

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

        return command;
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

    private Type expressionTypeVerification(Expression exp1, Expression exp2) {
        Type type = new Type();
        
        if (exp2.type.type == Type.NULL) {
            type = exp1.type;
        } else {
            switch(exp2.op.op) {
                case Operation.GT:
                case Operation.GTE:
                case Operation.LT:
                case Operation.LTE:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores de comparação de magnitude só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.EQUAL:
                case Operation.DIFF:
                    if (exp1.type.type == exp2.type.type) {
                        type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores de igual/desigualdade só se aplicam a tipos igual.");
                        se.printError();
                    }
                    break;
            }
        }
        
        return type;
    }
    
    public Expression expression() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                Expression exp1 = this.simpleExpr();
                Expression exp2 = this.expressionSuffix();
                exp.type = this.expressionTypeVerification(exp1, exp2);                
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }

        return exp;
    }

    public Expression expressionSuffix() {
        Expression exp = new Expression();
        
        switch (this.token.tag) {
            case '>':
            case '=':
            case Tag.GTE:
            case '<':
            case Tag.LTE:
            case Tag.DIFF:
                exp.op = this.relOp();
                exp.type = this.simpleExpr().type;
                break;
            case ')':
                exp.type.type = Type.NULL;
                break;
            default:
                int[] expected = {'>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF, ')'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }
        
        return exp;
    }
    
    private Type SimpleExprTypeVerification(Expression exp1, Expression exp2) {
        Type type = new Type();
        
        if (exp2.type.type == Type.NULL) {
            type = exp1.type;
        } else {
            switch(exp2.op.op) {
                case Operation.ADD:
                case Operation.SUB:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        type.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.OR:
                    if (exp1.type.type == Type.BOOLEAN && exp2.type.type == Type.BOOLEAN) {
                        type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores lógicos só se aplicam ao tipo boolean.");
                        se.printError();
                    }
                    break;
            }
        }
        
        return type;
    }
    
    public Expression simpleExpr() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                Expression exp1 = this.term();
                Expression exp2 = this.simpleExprTail();
                exp.type = this.SimpleExprTypeVerification(exp1, exp2);
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return exp;
    }

    public Expression simpleExprTail() {
        Expression exp = new Expression();
        
        switch (this.token.tag) {
            case '+':
            case '-':
            case Tag.OR:
                exp.op = this.addOp();
                Expression exp1 = this.term();
                Expression exp2 = this.simpleExprTail();
                exp.type = this.SimpleExprTypeVerification(exp1, exp2);
                break;
            case ';':
            case ')':
            case '>':
            case '=':
            case Tag.GTE:
            case '<':
            case Tag.LTE:
            case Tag.DIFF:
                exp.type.type = Type.NULL;
                break;
            default:
                int[] expected = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
        
        return exp;
    }
    
    private Type termTypeVerification(Type type, Expression exp) {
        Type ret = new Type();
        
        if (exp.type.type == Type.NULL) {
            ret = type;
        } else {
            switch(exp.op.op) {
                case Operation.MUL:
                case Operation.DIV:
                    if (type.type == Type.INTEGER && exp.type.type == Type.INTEGER) {
                        type.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.AND:
                    if (type.type == Type.BOOLEAN && exp.type.type == Type.BOOLEAN) {
                        type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores lógicos só se aplicam ao tipo boolean.");
                        se.printError();
                    }
                    break;
            }
        }
        
        return ret;
    }
    
    public Expression term() {
        Expression exp = new Expression();
        
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                Type type = this.factorA();
                Expression exp1 = this.termTail();
                exp.type = this.termTypeVerification(type, exp1);
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
        
        return exp;
    }

    public Expression termTail() {
        Expression exp = new Expression();
        
        switch (this.token.tag) {
            case '*':
            case '/':
            case Tag.AND:
                exp.op = this.mulOp();
                Type type = this.factorA();
                Expression exp1 = this.termTail();
                exp.type = this.termTypeVerification(type, exp1);
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
                exp.type.type = Type.NULL;
                break;
            default:
                int[] expected = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
        
        return exp;
    }

    public Type factorA() {
        Type type = new Type();
        
        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
                type = this.factor();
                break;
            case Tag.NOT:
                this.eat(Tag.NOT);
                type = this.factor();
                if (type.type != Type.BOOLEAN) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores lógicos só se aplicam ao tipo boolean.");
                    se.printError();
                }
                break;
            case '-':
                this.eat('-');
                type = this.factor();
                if (type.type != Type.INTEGER) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores aritméticos só se aplicam ao tipo integer.");
                    se.printError();
                }
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }
        
        return type;
    }

    public Type factor() {
        Type type = new Type(Type.ERROR);

        switch (this.token.tag) {
            case Tag.ID:
                type = ((Word) this.token).type;
                if (type == null) {
                    SemanticException se = new SemanticException(Lexer.line,
                            this.token, "Identificador '%s' não declarado.");
                    se.printError();
                }
                this.eat(Tag.ID);
                break;
            case Tag.NUM:
                type.type = Type.INTEGER;
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                type.type = Type.STRING;
                this.eat(Tag.STRING);
                break;
            case '(':
                this.eat('(');
                type = this.expression().type;
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '('};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return type;
    }

    public Operation relOp() {
        Operation op = new Operation();

        switch (this.token.tag) {
            case '=':
                op.op = Operation.EQUAL;
                this.eat('=');
                break;
            case '>':
                op.op = Operation.GT;
                this.eat('>');
                break;
            case Tag.GTE:
                op.op = Operation.GTE;
                this.eat(Tag.GTE);
                break;
            case '<':
                op.op = Operation.LT;
                this.eat('<');
                break;
            case Tag.LTE:
                op.op = Operation.LTE;
                this.eat(Tag.LTE);
                break;
            case Tag.DIFF:
                op.op = Operation.DIFF;
                this.eat(Tag.DIFF);
                break;
            default:
                int[] expected = {'>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }

        return op;
    }

    public Operation addOp() {
        Operation op = new Operation();

        switch (this.token.tag) {
            case '+':
                op.op = Operation.ADD;
                this.eat('+');
                break;
            case '-':
                op.op = Operation.SUB;
                this.eat('-');
                break;
            case Tag.OR:
                op.op = Operation.OR;
                this.eat(Tag.OR);
                break;
            default:
                int[] expected = {Tag.OR, '+', '-'};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }

        return op;
    }

    public Operation mulOp() {
        Operation op = new Operation();

        switch (this.token.tag) {
            case '*':
                op.op = Operation.MUL;
                this.eat('*');
                break;
            case '/':
                op.op = Operation.DIV;
                this.eat('/');
                break;
            case Tag.AND:
                op.op = Operation.AND;
                this.eat(Tag.AND);
                break;
            default:
                int[] expected = {'*', '/', Tag.AND};
                int[] follow = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                this.skipTo(expected, follow);
        }

        return op;
    }

    public Type constant() {
        Type type = new Type(Type.ERROR);

        switch (this.token.tag) {
            case Tag.NUM:
                type.type = Type.INTEGER;
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                type.type = Type.INTEGER;
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
