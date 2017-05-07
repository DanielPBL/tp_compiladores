package compilador;

public abstract class CompilationException extends Exception {

    protected int line;
    protected String msg;
    protected String type;
    
    public CompilationException(int line, String type) {
        this.line = line;
        this.type = type;
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
        System.out.printf("Erro %s na linha %d: %s\n", this.type, this.line, this.msg);
    }
}
