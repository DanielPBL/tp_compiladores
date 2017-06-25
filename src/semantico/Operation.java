/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

/**
 *
 * @author danie
 */
public class Operation {
    public final static int
            EQUAL = 0,
            GT = 1,
            GTE = 2,
            LT = 3,
            LTE = 4,
            DIFF = 5,
            ADD = 6,
            SUB = 7,
            OR = 8,
            MUL = 9,
            DIV = 10,
            AND = 11;
    
    public int op;
}
