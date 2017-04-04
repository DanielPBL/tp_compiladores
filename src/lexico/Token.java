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
public class Token {

    public final int tag;

    public Token(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "" + this.tag;
    }
}
