/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexico;

/**
 *
 * @author desenv00
 */
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
            STRING = 278;
}
