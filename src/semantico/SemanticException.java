/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import compilador.CompilationException;

/**
 *
 * @author desenv00
 */
public class SemanticException extends CompilationException {
    
    public SemanticException(int line, String type) {
        super(line, type);
    }
    
}
