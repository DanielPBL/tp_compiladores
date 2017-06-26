/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import lexico.Lexer;

/**
 *
 * @author desenv00
 */
public class Expression {

    public Type type;
    public Operation op;

    public Expression() {
        this.type = new Type(Type.ERROR);
    }

    public static Type termTypeVerification(Type type, Expression exp) {
        Type ret = new Type();

        if (exp.type.type == Type.NULL) {
            ret = type;
        } else {
            switch (exp.op.op) {
                case Operation.MUL:
                case Operation.DIV:
                    if (type.type == Type.INTEGER && exp.type.type == Type.INTEGER) {
                        ret.type = Type.INTEGER;
                    } else {
                        SemanticException se = new SemanticException(Lexer.line, null,
                                "Operadores aritméticos só se aplicam ao tipo integer.");
                        se.printError();
                    }
                    break;
                case Operation.AND:
                    if (type.type == Type.BOOLEAN && exp.type.type == Type.BOOLEAN) {
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
}
