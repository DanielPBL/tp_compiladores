package lexico;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Lexer {

    public static int line = 1;
    private char ch = ' ';
    private FileReader file;

    public HashMap<String, Token> words = new HashMap<>();

    private void reserve(Word w) {
        this.words.put(w.getLexeme().toLowerCase(), w);
    }

    private void readch() throws IOException, LexicalException {
        int read = this.file.read();

        if (read == -1) {
            throw new LexicalException(Lexer.line, new Token(Tag.EOF));
        }

        this.ch = (char) read;
    }

    private boolean readch(char c) throws IOException, LexicalException {
        this.readch();

        if (this.ch != c) {
            return false;
        }

        this.ch = ' ';
        return true;
    }

    public void printTable() {
        Iterator iterator = this.words.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry<String, Word>) iterator.next();
            System.out.printf("%s = %s\n", pair.getKey(), pair.getValue());
            iterator.remove();
        }
    }

    public Lexer(String fileName) throws FileNotFoundException {
        try {
            this.file = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.printf("Arquivo '%s' não encontrado.\n", fileName);
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

    public Token scan() throws IOException, LexicalException {
        try {
            //Desconsidera delimitadores na entrada
            OUTTER:
            while (true) {
                switch (this.ch) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\b':
                        this.readch();
                        break;
                    case '\n':
                        Lexer.line++;
                        this.readch();
                        break;
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

                    return this.scan();
                } else {
                    return new Token('/');
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
                this.ch = ' ';

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
                    this.ch = ' ';

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
                String literal = "";

                this.readch();
                while (this.ch != '\n' && this.ch != '"') {
                    literal += this.ch;
                    this.readch();
                }

                if (this.ch == '"') { //Tudo certo, literal fechado corretamente
                    this.ch = ' ';
                    return new Word(literal, Tag.STRING);
                } else {
                    throw new LexicalException(Lexer.line, new Word("Literal não terminado", 0));
                }
            }

            //Identificadores
            if (Character.isLetter(this.ch)) {
                String id = "";

                do {
                    id += ch;
                    this.readch();
                } while (Character.isLetterOrDigit(this.ch) || this.ch == '_');

                Word word = (Word) words.get(id.toLowerCase());

                if (word != null) {
                    return word;
                }

                word = new Word(id, Tag.ID);
                this.reserve(word);
                return word;
            }
        } catch (LexicalException le) {
            if (le.getToken().tag == Tag.EOF) {
                return le.getToken();
            } else {
                throw le;
            }
        }

        switch (this.ch) {
            case ';':
            case '(':
            case ')':
            case '=':
            case '-':
            case '+':
            case '*':
            case ',':
            case '_':
                Token t = new Token(this.ch);
                this.ch = ' ';
                return t;
            default:
                throw new LexicalException(Lexer.line, new Word("" + this.ch, Tag.INV));
        }
    }
}
