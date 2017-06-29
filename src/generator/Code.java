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

    public int PC = 0;

    private Label label = null;
    private final List<String> instructions = new ArrayList<>();
    private String fileName = "out.vm";

    public int add(String inst) {
        this.instructions.add(inst);
        this.PC++;

        return this.PC - 1;
    }

    private Label prepareLabel() {
        if (this.label == null) {
            this.label = Label.nextLabel();
        }

        return this.label;
    }

    public void generate(String fileName) throws IOException {
        this.fileName = fileName;
        this.generate();
    }

    public String addLabel(Integer pc) {
        String inst = this.instructions.get(pc);
        String l;
        
        l = this.prepareLabel().label;
        inst = l + ": " + inst;
        this.instructions.set(pc, inst);
        this.label = null;
        return l;

    }

    public void backpatch(List<Integer> list, Integer addr) {
        String inst;

        if (list == null) {
            return;
        }

        String label = this.addLabel(addr);

        for (Integer pc : list) {
            inst = this.instructions.get(pc);
            inst = inst + label;
            this.instructions.set(pc, inst);
        }
    }

    public void generate() throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName));

        for (String inst : this.instructions) {
            out.write(inst + "\n");
        }

        out.close();
    }

    public static List<Integer> merge(List<Integer> l1, List<Integer> l2) {
        if (l1 == null && l2 == null) {
            return null;
        }
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        l1.addAll(l2);
        return l1;
    }
}
