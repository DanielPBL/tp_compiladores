package lexico;

import compilador.CompilationException;

public class LexicalException extends CompilationException {

    private Token token;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public LexicalException(int linha, Token token) {
        super(linha);
        this.token = token;

        switch (this.token.tag) {
            case Tag.INV:
                this.msg = "Token inválido '" + ((Word) token).getLexeme() + "'";
                break;
            case Tag.EOF:
                this.msg = "Fim de arquivo inesperado";
                break;
            default:
                this.msg = ((Word) token).getLexeme();
        }
    }
}
