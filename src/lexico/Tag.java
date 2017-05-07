package lexico;

public class Tag {

    public final static int //Palavras reservadas
            INIT = 256,
            STOP = 257,
            IS = 258,
            T_INTEGER = 259,
            T_STRING = 260,
            IF = 261,
            BEGIN = 262,
            END = 263,
            ELSE = 264,
            DO = 265,
            WHILE = 266,
            READ = 267,
            WRITE = 268,
            NOT = 269,
            OR = 270,
            AND = 271,
            //Operadores e pontuação
            ATRIB = 272,
            GTE = 273,
            LTE = 274,
            DIFF = 275,
            // Outros tokens
            NUM = 276,
            ID = 277,
            STRING = 278,
            //Erros
            INV = -1,
            UNP = -2,
            EOF = -3;

    public static String getName(int token) {
        String str = "";

        switch (token) {
            case INIT:
                str = "init";
                break;
            case STOP:
                str = "stop";
                break;
            case IS:
                str = "is";
                break;
            case T_INTEGER:
                str = "integer";
                break;
            case T_STRING:
                str = "string";
                break;
            case IF:
                str = "if";
                break;
            case BEGIN:
                str = "begin";
                break;
            case END:
                str = "end";
                break;
            case ELSE:
                str = "else";
                break;
            case DO:
                str = "do";
                break;
            case WHILE:
                str = "while";
                break;
            case READ:
                str = "read";
                break;
            case WRITE:
                str = "write";
                break;
            case NOT:
                str = "not";
                break;
            case OR:
                str = "or";
                break;
            case AND:
                str = "and";
                break;
            case ATRIB:
                str = ":=";
                break;
            case GTE:
                str = ">=";
                break;
            case LTE:
                str = "<=";
                break;
            case DIFF:
                str = "<>";
                break;
            case NUM:
                str = "NUM";
                break;
            case ID:
                str = "ID";
                break;
            case STRING:
                str = "STRING";
                break;
            case EOF:
                str = "EOF";
                break;
            default:
                str += (char) token;
        }

        return str;
    }
}
