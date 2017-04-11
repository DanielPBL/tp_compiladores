package compilador;

import java.io.FileNotFoundException;
import java.io.IOException;
import lexico.Lexer;
import lexico.LexicalException;
import lexico.Tag;
import lexico.Token;

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

        try {
            lexer = new Lexer(args[0]);
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            Token token;

            do {
                try {
                    token = lexer.scan();
                    System.out.println(token);
                } catch (LexicalException e) {
                    token = e.getToken();
                    e.printError();
                } 
            } while (token.tag != Tag.EOF);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nTabela de Símbolos:");
        lexer.printTable();

    }

}
