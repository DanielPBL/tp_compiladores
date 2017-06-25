package compilador;

import java.io.FileNotFoundException;
import lexico.Lexer;
import semantico.Command;
import semantico.Type;
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

            Command c = syntaxer.program();

            if (syntaxer.success && c.type.type == Type.NULL) {
                System.out.println("Análise terminada com sucesso.");
            } else {
                System.out.println("Análise terminada com erro(s).");
            }

        } catch (FileNotFoundException e) {

        }

    }

}
