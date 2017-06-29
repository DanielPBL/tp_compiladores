package sintatico;

import generator.Code;
import java.io.IOException;
import java.util.List;
import lexico.Lexer;
import lexico.LexicalException;
import lexico.Num;
import lexico.Tag;
import lexico.Token;
import lexico.Word;
import semantico.Command;
import semantico.DeclarationCommand;
import semantico.Expression;
import semantico.Operation;
import semantico.SemanticException;
import semantico.Type;

public class Syntaxer {

    private final Lexer lexer;
    private Token token;
    public Code code;

    public boolean success = true;

    public Syntaxer(Lexer lexer) {
        this.lexer = lexer;
        this.advance();
        this.code = new Code();
        Expression.code = this.code;
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

    private boolean eat(int tag) {
        if (this.token.tag == tag) {
            this.advance();
            return true;
        } else {
            int[] expected = {tag};
            this.skipTo(expected, expected);
            this.advance();
            return false;
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
                code.add("START");
                command = this.declStmtList();
                this.eat(Tag.STOP);
                command.M = code.PC;
                code.add("STOP");
                code.backpatch(command.nextList, command.M);
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
                command = this.assignOrDecl(id);
                break;
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                Command c1 = this.stmtNoAssign();
                this.eat(';');
                c1.M = code.PC;
                Command c2 = this.stmtListTail();
                if (c1.type.type == Type.NULL && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    if (c1.M < code.PC) {
                        code.backpatch(c1.nextList, c1.M);
                        command.nextList = c2.nextList;
                    } else {
                        command.nextList = Code.merge(c1.nextList, c2.nextList);
                    }
                }
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.STOP};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command assignOrDecl(Word id) {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ATRIB:
                this.eat(Tag.ATRIB);
                Expression expr = this.simpleExpr();
                if (id.type.type == Type.NULL) {
                    SemanticException se = new SemanticException(Lexer.line, id,
                            "Identificador %s não declarado.");
                    se.printError();
                } else if (id.type.type != expr.type.type) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Tipos não compatíveis.");
                    se.printError();
                }
                code.add("STOREG " + id.offset);
                this.eat(';');
                Command c1 = this.stmtListTail();
                if (c1.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    command.nextList = c1.nextList;
                }
                break;
            case ',':
            case Tag.IS:
                DeclarationCommand dc = this.identListTail();
                this.eat(Tag.IS);
                Type type = this.type();
                dc.add(id);
                dc.resolve(type);
                this.eat(';');
                dc.gen(code);
                Command c2 = this.declStmtListTail();
                if (dc.type.type == Type.NULL && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    command.nextList = c2.nextList;
                }
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
                command = this.ifStmt();
                break;
            case Tag.DO:
                command = this.doStmt();
                break;
            case Tag.READ:
                command = this.readStmt();
                break;
            case Tag.WRITE:
                command = this.writeStmt();
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
                command = this.declStmtList();
                break;
            case Tag.STOP:
                command.type.type = Type.NULL;
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
                Token tk = this.token;
                if (this.eat(Tag.ID)) { //De fato era um identificador
                    Word id = (Word) tk;
                    dc.add(id);
                }
                DeclarationCommand dc1 = this.identListTail();
                if (dc1.type.type == Type.NULL) {
                    dc.merge(dc1.ids);
                    dc.type.type = Type.NULL;
                }
                break;
            case Tag.IS:
                dc.type.type = Type.NULL;
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

    public Command stmtList() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                Command c1 = this.stmt();
                this.eat(';');
                c1.M = code.PC;
                Command c2 = this.stmtListTail();
                if (c1.type.type == Type.NULL && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    if (c1.M < code.PC) {
                        code.backpatch(c1.nextList, c1.M);
                        command.nextList = c2.nextList;
                    } else {
                        command.nextList = Code.merge(c1.nextList, c2.nextList);
                    }
                }
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {Tag.END, Tag.WHILE};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command stmtListTail() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.IF:
            case Tag.DO:
            case Tag.READ:
            case Tag.WRITE:
                Command c1 = this.stmt();
                c1.M = code.PC;
                this.eat(';');
                Command c2 = this.stmtListTail();
                if (c1.type.type == Type.NULL && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    if (c1.M < code.PC) {
                        code.backpatch(c1.nextList, c1.M);
                        command.nextList = c2.nextList;
                    } else {
                        command.nextList = Code.merge(c1.nextList, c2.nextList);
                    }
                }
                break;
            case Tag.END:
            case Tag.WHILE:
            case Tag.STOP:
                command.type.type = Type.NULL;
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE, Tag.END, Tag.WHILE, Tag.STOP};
                int[] follow = {Tag.END, Tag.STOP, Tag.WHILE};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command stmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ID:
                command = this.assignStmt();
                break;
            case Tag.IF:
                command = this.ifStmt();
                break;
            case Tag.DO:
                command = this.doStmt();
                break;
            case Tag.READ:
                command = this.readStmt();
                break;
            case Tag.WRITE:
                command = this.writeStmt();
                break;
            default:
                int[] expected = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command assignStmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ID:
                Word id = (Word) this.token;
                this.eat(Tag.ID);
                this.eat(Tag.ATRIB);
                Expression exp = this.simpleExpr();
                if (id.type.type == Type.NULL) {
                    SemanticException se = new SemanticException(Lexer.line, id,
                            "Identificador %s não declarado.");
                    se.printError();
                } else if (id.type.type != exp.type.type) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Tipos incompatíveis.");
                    se.printError();
                } else {
                    code.add("STOREG " + id.offset);
                    command.type.type = Type.NULL;
                }
                break;
            default:
                int[] expected = {Tag.ID};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command ifStmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.IF:
                this.eat(Tag.IF);
                this.eat('(');
                Expression exp = this.condition();
                this.eat(')');
                exp.M = code.PC;
                this.eat(Tag.BEGIN);
                Command c1 = this.stmtList();
                this.eat(Tag.END);
                Command c2 = this.ifSuffix();
                if (exp.type.type == Type.BOOLEAN && c1.type.type == Type.NULL
                        && c2.type.type == Type.NULL) {
                    command.type.type = Type.NULL;
                    code.backpatch(exp.trueList, exp.M);
                    code.backpatch(exp.falseList, c2.M);
                    List<Integer> temp = Code.merge(c1.nextList, c2.N.nextList);
                    command.nextList = Code.merge(temp, c2.nextList);
                }
                break;
            default:
                int[] expected = {Tag.IF};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command ifSuffix() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.ELSE:
                command.N = new Command();
                command.N.addDep(code.PC);
                code.add("JUMP ");
                this.eat(Tag.ELSE);
                command.M = code.PC;
                this.eat(Tag.BEGIN);
                Command c2 = this.stmtList();
                command.type = c2.type;
                command.nextList = c2.nextList;
                this.eat(Tag.END);
                break;
            case ';':
                command.type.type = Type.NULL;
                break;
            default:
                int[] expected = {Tag.ELSE, ';'};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Expression condition() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                exp = this.expression();
                if (exp.type.type != Type.BOOLEAN) {
                    exp.type.type = Type.ERROR;
                }
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }

        return exp;
    }

    public Command doStmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.DO:
                this.eat(Tag.DO);
                command.M = code.PC;
                Command c1 = this.stmtList();
                c1.M = code.PC;
                Expression exp = this.doSuffix();
                if (c1.type.type == Type.NULL && exp.type.type == Type.BOOLEAN) {
                    command.type.type = Type.NULL;
                    code.backpatch(c1.nextList, c1.M);
                    code.backpatch(exp.trueList, command.M);
                    command.nextList = exp.falseList;
                    code.add("JUMP " + code.addLabel(c1.M));
                }
                break;
            default:
                int[] expected = {Tag.DO};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Expression doSuffix() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.WHILE:
                this.eat(Tag.WHILE);
                this.eat('(');
                exp = this.condition();
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.WHILE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return exp;
    }

    public Command readStmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.READ:
                this.eat(Tag.READ);
                this.eat('(');
                Token tk = this.token;
                if (this.eat(Tag.ID)) {
                    Word id = (Word) tk;
                    if (id.type.type == Type.NULL) {
                        SemanticException se = new SemanticException(Lexer.line, id,
                                "Identificador %s não declarado.");
                        se.printError();
                    } else {
                        switch (id.type.type) {
                            case Type.INTEGER:
                                code.add("READ");
                                code.add("ATOI");
                                code.add("STOREG " + id.offset);
                                break;
                            case Type.STRING:
                                code.add("READ");
                                code.add("STOREG " + id.offset);
                                break;
                        }
                        command.type.type = Type.NULL;
                    }
                }
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.READ};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Command writeStmt() {
        Command command = new Command();

        switch (this.token.tag) {
            case Tag.WRITE:
                this.eat(Tag.WRITE);
                this.eat('(');
                Expression exp = this.writable();
                if (exp.type.type != Type.ERROR) {
                    switch (exp.type.type) {
                        case Type.BOOLEAN:
                        case Type.INTEGER:
                            code.add("WRITEI");
                            break;
                        case Type.STRING:
                            code.add("WRITES");
                            break;
                    }
                    command.type.type = Type.NULL;
                }
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.WRITE};
                int[] follow = {';'};
                this.skipTo(expected, follow);
        }

        return command;
    }

    public Expression writable() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                exp = this.simpleExpr();
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {')'};
                this.skipTo(expected, follow);
        }

        return exp;
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
                exp1.M = code.PC;
                Expression exp2 = this.expressionSuffix();
                exp = Expression.expressionTypeVerification(exp1, exp2, null);
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
                Expression exp1 = this.simpleExpr();
                exp.type = exp1.type;
                exp.trueList = exp1.trueList;
                exp.falseList = exp1.falseList;
                switch (exp.op.op) {
                    case Operation.GT:
                        code.add("SUP");
                        break;
                    case Operation.EQUAL:
                        code.add("EQUAL");
                        break;
                    case Operation.GTE:
                        code.add("SUPEQ");
                        break;
                    case Operation.LT:
                        code.add("INF");
                        break;
                    case Operation.LTE:
                        code.add("INFEQ");
                        break;
                    case Operation.DIFF:
                        code.add("EQUAL");
                        code.add("NOT");
                        break;
                }
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
                exp1.M = code.PC;
                Expression exp2 = this.simpleExprTail();
                exp = Expression.simpleExprTypeVerification(exp1, exp2, null);
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
                switch (exp.op.op) {
                    case Operation.ADD:
                    case Operation.OR:
                        code.add("ADD");
                        break;
                    case Operation.SUB:
                        code.add("SUB");
                        break;
                }
                exp1.M = code.PC;
                Expression exp2 = this.simpleExprTail();
                exp = Expression.simpleExprTypeVerification(exp1, exp2, exp.op);
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

    public Expression term() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
            case Tag.NOT:
            case '-':
                Expression exp1 = this.factorA();
                exp1.M = code.PC;
                Expression exp2 = this.termTail();
                exp = Expression.termTypeVerification(exp1, exp2, null);
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
                Expression exp1 = this.factorA();
                switch (exp.op.op) {
                    case Operation.MUL:
                    case Operation.AND:
                        code.add("MUL");
                        break;
                    case Operation.DIV:
                        code.add("DIV");
                        break;
                }
                exp1.M = code.PC;
                Expression exp2 = this.termTail();
                exp = Expression.termTypeVerification(exp1, exp2, exp.op);
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

    public Expression factorA() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
            case Tag.NUM:
            case Tag.STRING:
            case '(':
                exp = this.factor();
                break;
            case Tag.NOT:
                this.eat(Tag.NOT);
                Expression exp1 = this.factor();
                exp.falseList = exp1.trueList;
                exp.trueList = exp1.falseList;
                if (exp.type.type != Type.BOOLEAN) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores lógicos só se aplicam ao tipo boolean.");
                    se.printError();
                }
                code.add("NOT");
                break;
            case '-':
                this.eat('-');
                exp = this.factor();
                if (exp.type.type != Type.INTEGER) {
                    SemanticException se = new SemanticException(Lexer.line, null,
                            "Operadores aritméticos só se aplicam ao tipo integer.");
                    se.printError();
                }
                code.add("PUSHI -1");
                code.add("MUL");
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '(', Tag.NOT, '-'};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return exp;
    }

    public Expression factor() {
        Expression exp = new Expression();

        switch (this.token.tag) {
            case Tag.ID:
                Word id = (Word) this.token;
                if (id.type.type == Type.NULL) {
                    SemanticException se = new SemanticException(Lexer.line,
                            this.token, "Identificador %s não declarado.");
                    se.printError();
                } else {
                    code.add("PUSHG " + id.offset);
                    exp.type = id.type;
                }
                this.eat(Tag.ID);
                break;
            case Tag.NUM:
                exp.type.type = Type.INTEGER;
                code.add("PUSHI " + ((Num) this.token).getValue());
                this.eat(Tag.NUM);
                break;
            case Tag.STRING:
                exp.type.type = Type.STRING;
                code.add("PUSHS \"" + ((Word) this.token).getLexeme() + "\"");
                this.eat(Tag.STRING);
                break;
            case '(':
                this.eat('(');
                exp = this.expression();
                this.eat(')');
                break;
            default:
                int[] expected = {Tag.ID, Tag.NUM, Tag.STRING, '('};
                int[] follow = {'*', '/', Tag.AND, Tag.OR, '+', '-', ';', ')', '>', '=', Tag.GTE, '<', Tag.LTE, Tag.DIFF};
                this.skipTo(expected, follow);
        }

        return exp;
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
