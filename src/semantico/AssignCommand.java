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
public class AssignCommand extends Command {
    public Type assignType;
    
    public AssignCommand(Type type, Type assignType) {
        this.type = type;
        this.assignType = assignType;
    }
}
