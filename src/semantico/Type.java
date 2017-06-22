/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

/**
 *
 * @author desenv00
 */
public class Type {

    public final static int
            ERROR = 1,
            NULL = 0,
            INTEGER = 1,
            STRING = 2,
            BOOLEAN = 3;
    
    public int type;
    public int width;
    
    public Type(int type, int width) {
        this.type = type;
        this.width = width;
    }
    
    public Type(int type) {
        this.type = type;
        this.width = 0;
    }
    
    public Type() {
        this.type = Type.ERROR;
        this.width = 0;
    }
}
