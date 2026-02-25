package ifgoiano;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Token {
    private Integer id;
    private final String lexema;
    private final String tipo;
    Map<String, ArrayList<String>> tabela_tokens;

    public HashMap<String, ArrayList<String>> validar_tokens() {
        this.tabela_tokens = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/edu/src/main/resources/token_table.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                // Removendo colchetes desnecessários e dividindo a linha em tipo e lexemas
                linha = linha.replaceFirst("\\[", "");
                linha = linha.substring(0, linha.lastIndexOf("]"));

                String[] tipo = linha.split(": ");
                //String[] lexemas = tipo[1].split(", ");
                ArrayList<String> lexemas = new ArrayList<>();
                for (String lex : tipo[1].split(", ")) {
                    lexemas.add(lex.trim());
                }

                tabela_tokens.put(tipo[0], lexemas);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }

        return new HashMap<>(tabela_tokens);
    }

    public Token(Integer id, String lexema, String tipo) {
        this.id = id;
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public Token(String lexema, String tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }
}
