/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import generator.Label;
import java.util.LinkedList;
import java.util.List;
import lexico.Lexer;

/**
 *
 * @author desenv00
 */
public class Expression {

    public Type type;
    public Operation op;
    
    public Label M;
    
    public List<Integer> trueList;
    public List<Integer> falseList;

    public Expression() {
        this.type = new Type(Type.ERROR);
        this.op = new Operation();
    }
    
    public void addTrueList(Integer addr) {
        if (this.trueList == null) {
            this.trueList = new LinkedList<>();
        }
        
        this.trueList.add(addr);
    }
    
    public void addFalseList(Integer addr) {
        if (this.falseList == null) {
            this.falseList = new LinkedList<>();
        }
        
        this.falseList.add(addr);
    }

    public static Type termTypeVerification(Expression exp1, Expression exp2) {
        Type ret = new Type();

        if (exp2.type.type == Type.NULL) {
            ret = exp1.type;
        } else {
            switch (exp2.op.op) {
                case Operation.MUL:
                case Operation.DIV:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        ret.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.AND:
                    if (exp1.type.type == Type.BOOLEAN && exp2.type.type == Type.BOOLEAN) {
                        ret.type = Type.BOOLEAN;
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

    public static Type expressionTypeVerification(Expression exp1, Expression exp2) {
        Type type = new Type();

        if (exp2.type.type == Type.NULL) {
            type = exp1.type;
        } else {
            switch (exp2.op.op) {
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
                                "Operadores de igual/desigualdade só se aplicam a tipos iguais.");
                        se.printError();
                    }
                    break;
            }
        }

        return type;
    }
    
    public static Type simpleExprTypeVerification(Expression exp1, Expression exp2) {
        Type type = new Type();

        if (exp2.type.type == Type.NULL) {
            type = exp1.type;
        } else {
            switch (exp2.op.op) {
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
}
