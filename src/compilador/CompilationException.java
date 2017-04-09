package compilador;

public abstract class CompilationException extends Exception {

    protected int line;
    protected String msg;
    
    public CompilationException(int line) {
        this.line = line;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
    
    public void printError() {
        System.out.printf("Erro na linha %d: %s\n", this.line, this.msg);
    }
}
