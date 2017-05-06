package lexico;

public class Token {

    public final int tag;

    public Token(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "'" + Tag.getName(this.tag) + "'";
    }
}
