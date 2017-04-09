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
                str = "INIT";
                break;
            case STOP:
                str = "STOP";
                break;
            case IS:
                str = "IS";
                break;
            case T_INTEGER:
                str = "T_INTEGER";
                break;
            case T_STRING:
                str = "T_STRING";
                break;
            case IF:
                str = "IF";
                break;
            case BEGIN:
                str = "BEGIN";
                break;
            case END:
                str = "END";
                break;
            case ELSE:
                str = "ELSE";
                break;
            case DO:
                str = "DO";
                break;
            case WHILE:
                str = "WHILE";
                break;
            case READ:
                str = "READ";
                break;
            case WRITE:
                str = "WRITE";
                break;
            case NOT:
                str = "NOT";
                break;
            case OR:
                str = "OR";
                break;
            case AND:
                str = "AND";
                break;
            case ATRIB:
                str = "ATRIB";
                break;
            case GTE:
                str = "GTE";
                break;
            case LTE:
                str = "LTE";
                break;
            case DIFF:
                str = "DIFF";
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
