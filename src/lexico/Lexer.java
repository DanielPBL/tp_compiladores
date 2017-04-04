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

    public Token scan() throws IOException, Exception {
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

        //Operadores
        switch (this.ch) {
            case ':':
                if (this.readch('=')) {
                    return Word.ATRIB;
                } else {
                    return new Token(':');
                }
            case '>':
                if (this.readch('=')) {
                    return Word.GTE;
                } else {
                    return new Token('>');
                }
            case '<':
                if (this.readch('=')) {
                    return Word.LTE;
                } else if (this.ch == '>') {
                    return Word.DIFF;
                } else {
                    return new Token('<');
                }
        }

        //Constantes inteiras
        if (Character.isDigit(this.ch)) {
            if (this.ch == '0') {
                return new Num(0);
            }

            int value = 0;
            do {
                value = 10 * value + Character.digit(this.ch, 10);
                this.readch();
            } while (Character.isDigit(this.ch));

            return new Num(value);
        }

        //Literais
        if (this.ch == '"') {
            String literal = "olá";

            this.readch();
            while (this.ch != '\n' && this.ch != '"') {
                literal += this.ch;
                this.readch();
            }

            if (this.ch == '"') { //Tudo certo, literal fechado corretamente
                this.ch = ' ';
                return new Word(literal, Tag.STRING);
            } else {
                throw new Exception("Literal não terminado");
            }
        }

        //Identificadores
        Token t = new Token(this.ch);
        this.ch = ' ';
        return t;
    }
}
