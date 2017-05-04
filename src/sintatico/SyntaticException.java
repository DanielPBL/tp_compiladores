/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sintatico;

import compilador.CompilationException;
import lexico.Tag;
import lexico.Token;
import lexico.Word;

/**
 *
 * @author desenv00
 */
public class SyntaticException extends CompilationException {
    private Token token;
    private Token[] exp;
    
    public SyntaticException(int linha, Token token, Token[] exp) {
        super(linha);
        this.token = token;
        this.exp = exp;
        
        switch (this.token.tag) {
            case Tag.EOF:
                this.msg = "Fim de arquivo inesperado";
                break;
            default:
                this.msg = "Token inesperado " + token;
        }
    }
    
}
