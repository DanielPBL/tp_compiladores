package lexico;

import compilador.CompilationException;

public class LexicalException extends CompilationException {

    private final Token token;

    public Token getToken() {
        return token;
    }

    public LexicalException(int linha, Token token) {
        super(linha, "léxico");
        this.token = token;

        switch (this.token.tag) {
            case Tag.INV:
                this.msg = "Token inválido '" + ((Word) token).getLexeme() + "'.";
                break;
            default:
                this.msg = ((Word) token).getLexeme();
        }
    }
}
