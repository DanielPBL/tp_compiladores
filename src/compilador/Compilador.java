/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.FileNotFoundException;
import java.io.IOException;
import lexico.Lexer;
import lexico.Tag;
import lexico.Token;

/**
 *
 * @author desenv00
 */
public class Compilador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Argumentos insuficientes.");
            System.exit(1);
        }

        Lexer lexer = null;

        try {
            lexer = new Lexer(args[0]);
        } catch (FileNotFoundException e) {
            System.exit(1);
        }

        try {
            Token token;

            do {
                token = lexer.scan();
                System.out.println(token);
            } while (token.tag != Tag.EOF);
        } catch (CompilationException e) {
            e.printError();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
