/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantico;

import java.util.LinkedList;
import java.util.List;
import lexico.Word;

/**
 *
 * @author desenv00
 */
public class DeclarationCommand extends Command {

    private final List<Word> ids;

    public DeclarationCommand() {
        super();
        this.ids = new LinkedList<>();
    }

    public void add(Word id) {
        this.ids.add(id);
    }
    
    public void merge(List<Word> list) {
        this.ids.addAll(list);
    }

    public void resolve() {
        this.ids.forEach((id) -> {
            id.type = this.type;
        });
    }
}
