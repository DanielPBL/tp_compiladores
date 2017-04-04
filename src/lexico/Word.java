/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexico;

/**
 *
 * @author desenv00
 */
public class Word extends Token {

    private String lexeme = "";

    public String getLexeme() {
        return this.lexeme;
    }

    public static final Word
            AND = new Word("and", Tag.AND),
            OR = new Word("or", Tag.OR),
            ATRIB = new Word(":=", Tag.ATRIB),
            GTE = new Word(">=", Tag.GTE),
            LTE = new Word("<=", Tag.LTE),
            DIFF = new Word("<>", Tag.DIFF);

    public Word(String lexema, int tag) {
        super(tag);
        this.lexeme = lexema;
    }

    @Override
    public String toString() {
        return "" + this.lexeme;
    }
}
