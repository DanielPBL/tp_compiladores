/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexico;

import compilador.CompilationException;

/**
 *
 * @author daniel
 */
public class LexicalException extends CompilationException {

    private Token token;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public LexicalException(int linha, Token token) {
        super(linha);
        this.token = token;

        switch (this.token.tag) {
            case Tag.INV:
                this.msg = "Token inválido '" + ((Word) token).getLexeme() + "'";
                break;
            /*
            case Tag.UNP:
                this.msg = "Token inesperado '" + ((Word) token).getLexeme() + "'";
                break;
             */
            case Tag.EOF:
                this.msg = "Fim de arquivo inesperado";
                break;
            default:
                this.msg = ((Word) token).getLexeme();
        }
    }
}
