package lexico;

public class Num extends Token {

    public final int value;

    public int getValue() {
        return value;
    }

    public Num(int valor) {
        super(Tag.NUM);
        this.value = valor;
    }

    @Override
    public String toString() {
        return "'" + this.value + "'";
    }

}
