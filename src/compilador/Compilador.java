package compilador;

import java.io.FileNotFoundException;
import lexico.Lexer;
import sintatico.Syntaxer;

public class Compilador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Argumentos insuficientes.");
            System.exit(1);
        }

        Lexer lexer;
        Syntaxer syntaxer;

        try {
            lexer = new Lexer(args[0]);
            syntaxer = new Syntaxer(lexer);

            syntaxer.program();
        } catch (FileNotFoundException e) {

        }

    }

}
