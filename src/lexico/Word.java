package lexico;

import semantico.Type;

public class Word extends Token {

    private String lexeme = "";
    public Type type;

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
        this.type = new Type(Type.NULL);
    }

    @Override
    public String toString() {
        return "'" + this.lexeme + "'";
    }
}
