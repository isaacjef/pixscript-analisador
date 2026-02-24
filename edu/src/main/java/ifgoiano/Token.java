package ifgoiano;

public class Token {
    private Integer id;
    private String lexema;
    private String tipo;

    public Token(Integer id, String lexema, String tipo) {
        this.id = id;
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public Token(String lexema, String tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }
}
