/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import generator.Code;
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

    public static Code code;

    public Integer M;

    public List<Integer> trueList;
    public List<Integer> falseList;

    public Expression() {
        this.type = new Type(Type.ERROR);
        //this.op = new Operation();
        //this.op.op = -1;
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

    public static Expression termTypeVerification(Expression exp1, Expression exp2, Operation op) {
        Expression exp = new Expression();

        if (exp2.type.type == Type.NULL) {
            exp = exp1;
        } else {
            switch (exp2.op.op) {
                case Operation.MUL:
                case Operation.DIV:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        exp.type.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.AND:
                    if (exp1.type.type == Type.BOOLEAN && exp2.type.type == Type.BOOLEAN) {
                        exp.type.type = Type.BOOLEAN;
                        Expression.code.backpatch(exp1.trueList, exp1.M);
                        exp.trueList = exp2.trueList;
                        exp.falseList = Code.merge(exp1.falseList, exp2.falseList);
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores lógicos só se aplicam ao tipo boolean.");
                        se.printError();
                    }
                    break;
            }
        }
        exp.op = op;
        
        return exp;
    }

    public static Expression expressionTypeVerification(Expression exp1, Expression exp2, Operation op) {
        Expression exp = new Expression();

        if (exp2.type.type == Type.NULL) {
            exp = exp1;
        } else {
            switch (exp2.op.op) {
                case Operation.GT:
                case Operation.GTE:
                case Operation.LT:
                case Operation.LTE:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        exp.type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores de comparação de magnitude só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.EQUAL:
                case Operation.DIFF:
                    if (exp1.type.type == exp2.type.type) {
                        exp.type.type = Type.BOOLEAN;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores de igual/desigualdade só se aplicam a tipos iguais.");
                        se.printError();
                    }
                    break;
            }
            //code.add("NOT");
            exp.addFalseList(code.add("JZ "));
            exp.addTrueList(code.add("JUMP "));
        }
        exp.op = op;
        
        return exp;
    }

    public static Expression simpleExprTypeVerification(Expression exp1, Expression exp2, Operation op) {
        Expression exp = new Expression();

        if (exp2.type.type == Type.NULL) {
            exp = exp1;
        } else {
            switch (exp2.op.op) {
                case Operation.ADD:
                case Operation.SUB:
                    if (exp1.type.type == Type.INTEGER && exp2.type.type == Type.INTEGER) {
                        exp.type.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.OR:
                    if (exp1.type.type == Type.BOOLEAN && exp2.type.type == Type.BOOLEAN) {
                        exp.type.type = Type.BOOLEAN;
                        Expression.code.backpatch(exp1.falseList, exp1.M);
                        exp.trueList = Code.merge(exp1.trueList, exp2.trueList);
                        exp.falseList = exp2.falseList;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores lógicos só se aplicam ao tipo boolean.");
                        se.printError();
                    }
                    break;
            }
        }
        exp.op = op;

        return exp;
    }
}
