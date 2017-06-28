/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desenv00
 */
public class Code {
    private Label label = null;
    private final List<String> instructions = new ArrayList<>();
    private String fileName = "out.vm";
    
    public void add(String inst) {
        if (label != null) {
            this.instructions.add(label.label + inst);
            this.label = null;
        } else {
            this.instructions.add(inst);
        }
    }
    
    public Label prepareLabel() {
        this.label = Label.nextLabel();
        return this.label;
    }
    
    public void generate(String fileName) throws IOException {
        this.fileName = fileName;
        this.generate();
    }
    
    public void generate() throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName));
        
        for (String inst : this.instructions) {
            out.write(inst + "\n");
        }
        
        out.close();
    }
}
