/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import generator.Label;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author desenv00
 */
public class Command {
    public Type type;
    public List<Integer> nextList;
    
    public Label M;
    public Command N;
    public Expression E;
    
    public Command() {
        this.type = new Type();
    }
    
    public void addDep(Integer addr) {
        if (this.nextList == null) {
            this.nextList = new LinkedList<>();
        }
        
        this.nextList.add(addr);
    }
}
