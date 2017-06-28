/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

/**
 *
 * @author desenv00
 */
public class Label {
    public String label;
    private static int count = 0;
    
    private Label() {
        this.label = "L";
    }
    
    public static Label nextLabel() {
        Label label = new Label();
        label.label = label.label + Label.count + ": ";
        Label.count++;
        
        return label;
    }
}
