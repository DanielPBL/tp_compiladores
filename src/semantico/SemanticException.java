/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import compilador.CompilationException;
import lexico.Token;

/**
 *
 * @author desenv00
 */
public class SemanticException extends CompilationException {
    private Token token;
    
    public SemanticException(int line, Token token, String msg) {
        super(line, "semântico");
        
        if (token != null) {
            this.msg = String.format(msg, token.toString());
        } else {
            this.msg = msg;
        }
    }
    
}
