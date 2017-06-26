/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import compilador.CompilationException;
import lexico.Token;
import lexico.Word;

/**
 *
 * @author desenv00
 */
public class SemanticException extends CompilationException {

    private Token token;
    private boolean print = true;

    public SemanticException(int line, Token token, String msg) {
        super(line, "semântico");

        if (token instanceof Word) {
            if (((Word) token).reported) {
                print = false;
            }
            ((Word) token).reported = true;
        }

        if (token != null) {
            this.msg = String.format(msg, token.toString());
        } else {
            this.msg = msg;
        }
    }

    @Override
    public void printError() {
        if (this.print) {
            System.out.printf("Erro %s na linha %d: %s\n", this.type, this.line, this.msg);
        }
    }

}
