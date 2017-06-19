package lexico;

import semantico.Type;

public class Word extends Token {

    private String lexeme = "";
    private Type type = Type.NULL;

    public String getLexeme() {
        return this.lexeme;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(Type type) {
        this.type = type;
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
        return "'" + this.lexeme + "'";
    }
}
