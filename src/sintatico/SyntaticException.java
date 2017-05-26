/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sintatico;

import compilador.CompilationException;
import lexico.Tag;
import lexico.Token;

/**
 *
 * @author desenv00
 */
public class SyntaticException extends CompilationException {

    private final Token token;
    private final Token[] expected;

    public Token getToken() {
        return this.token;
    }

    public Token[] getExp() {
        return this.expected;
    }

    public SyntaticException(int linha, Token token, int[] tags) {
        super(linha, "sintático");
        this.token = token;
        this.expected = new Token[tags.length];

        if (this.token.tag == Tag.EOF) {
            this.msg = "Fim de arquivo inesperado. Esperando ";
        } else {
            this.msg = "Token " + this.token + " inesperado. Esperando ";
        }

        for (int i = 0; i < expected.length; i++) {
            this.expected[i] = new Token(tags[i]);
            this.msg += this.expected[i] + ", ";
        }

        this.msg = this.msg.substring(0, this.msg.length() - 2) + ".";
    }

}
