/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexico;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author desenv00
 */
public class Lexer {

    public static int line = 1;
    private char ch = ' ';
    private FileReader file;

    private HashMap<String, Token> words = new HashMap<>();

    private void reserve(Word w) {
        this.words.put(w.getLexeme(), w);
    }

    private void readch() throws IOException {
        ch = (char) this.file.read();
    }

    private boolean readch(char c) throws IOException {
        this.readch();

        if (this.ch != c) {
            return false;
        }

        this.ch = ' ';
        return true;
    }

    public Lexer(String fileName) throws FileNotFoundException {
        try {
            this.file = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");
            throw e;
        }

        //Palavras reservadas
        this.reserve(new Word("init", Tag.INIT));
        this.reserve(new Word("stop", Tag.STOP));
        this.reserve(new Word("is", Tag.IS));
        this.reserve(new Word("integer", Tag.T_INTEGER));
        this.reserve(new Word("string", Tag.T_STRING));
        this.reserve(new Word("if", Tag.IF));
        this.reserve(new Word("begin", Tag.BEGIN));
        this.reserve(new Word("end", Tag.END));
        this.reserve(new Word("else", Tag.ELSE));
        this.reserve(new Word("do", Tag.DO));
        this.reserve(new Word("while", Tag.WHILE));
        this.reserve(new Word("read", Tag.READ));
        this.reserve(new Word("write", Tag.WRITE));
        this.reserve(new Word("not", Tag.NOT));
        this.reserve(new Word("or", Tag.OR));
        this.reserve(new Word("and", Tag.AND));
    }

    public Token scan() throws IOException {
        //Desconsidera delimitadores na entrada
        OUTTER:
        while (true) {
            this.readch();
            switch (this.ch) {
                case ' ':
                case '\t':
                case '\r':
                case '\b':
                    continue;
                case '\n':
                    Lexer.line++;
                default:
                    break OUTTER;
            }
        }

        //Desconsidera comentários de uma linha
        if (this.ch == '/') {
            if (this.readch('/')) {
                //Ignora tudo até encontrar o \n
                while (this.ch != '\n') {
                    this.readch();
                }
                Lexer.line++;
                return this.scan();
            }
        }

        //Desconsidera blocos de comentários
        if (this.ch == '{') {
            do {
                this.readch();
                if (this.ch == '\n') {
                    Lexer.line++;
                }
            } while (this.ch != '}');
            return this.scan();
        }

        Token t = new Token(this.ch);
        this.ch = ' ';
        return t;
    }
}
