/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import generator.Code;
import java.util.LinkedList;
import java.util.List;
import lexico.Lexer;
import lexico.Word;

/**
 *
 * @author desenv00
 */
public class DeclarationCommand extends Command {

    public final List<Word> ids;
    public Type idType;
    private static int offset = 0;    
    
    public DeclarationCommand() {
        super();
        this.ids = new LinkedList<>();
    }

    public DeclarationCommand(Type type, Type idType) {
        super();
        this.ids = new LinkedList<>();
        this.type = type;
    }

    public void add(Word id) {
        id.offset = DeclarationCommand.offset;
        DeclarationCommand.offset++;
        this.ids.add(id);
    }

    public void merge(List<Word> list) {
        this.ids.addAll(list);
    }

    public void resolve(Type idType) {
        this.idType = idType;
        
        this.ids.forEach((id) -> {
            if (id.type.type != Type.NULL) {
                SemanticException se = new SemanticException(Lexer.line, id,
                        "Identificador %s já declarado.");
                se.printError();
                this.type.type = Type.ERROR;
            } else {
                id.type = idType;
            }
        });
    }
    
    public void gen(Code code) {
        switch (this.idType.type) {
            case Type.STRING:
                for (int i = 0; i < this.ids.size(); i++) {
                    code.add("PUSHS \"\"");
                }
                break;
            case Type.INTEGER:
                code.add("PUSHN " + this.ids.size());
                break;
            default:
                System.out.println("Algo muito errado ocorreu!!!");
                System.exit(1);
        }
    }
}
