/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author daniel
 */
public abstract class CompilationException extends Exception {

    protected int line;
    protected String msg;
    
    public CompilationException(int line) {
        this.line = line;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
    
    public void printError() {
        System.out.printf("Erro na linha %d: %s\n", this.line, this.msg);
    }
}
