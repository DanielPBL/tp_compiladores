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
        return "" + this.value;
    }

}
