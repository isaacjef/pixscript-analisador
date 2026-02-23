package ifgoiano;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Analisador {

    String caminho;
    Map<String, String[]> tabela_simbolos;

    public Analisador() {
        this.caminho = "edu/src/main/resources/";
        this.tabela_simbolos = validar_tokens(); // Preencher a tabela com o método validar_tokens()
    }
    
    public HashMap<String, String[]> validar_tokens() {
        //String caminho = "edu/src/main/resources/token_table.txt";
        //System.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        //System.out.println(System.getProperty("user.dir"));
        this.tabela_simbolos = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.caminho + "token_table.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                // Removendo colchetes desnecessários e dividindo a linha em tipo e lexemas
                linha = linha.replace(" [", "");
                linha = linha.substring(0, linha.lastIndexOf("]"));

                String[] tipo = linha.split(":");
                String[] lexemas = tipo[1].split(", ");

                tabela_simbolos.put(tipo[0], lexemas);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }

        return new HashMap<>(tabela_simbolos);
    }

    private String verificarToken(Map<String, String[]> tabela, String lexema) {
        for (Map.Entry<String, String[]> e : tabela.entrySet()) {
            String[] valores = e.getValue();
            if (valores != null) {
                for (String v : valores) {
                    if (v != null && v.equals(lexema)) {
                        return e.getKey();
                    }
                }
            }
        }
        return null;
    }

    // private String verificarTokenVariavel(Map<String, String[]> tabela, String linha) {
    //     for (Map.Entry<String, String[]> e : tabela.entrySet()) {
    //         String[] valores = e.getValue();
    //         if (valores != null) {
    //             for (String v : valores) {
    //                 if (v != null && v.equals(lexema)) {
    //                     return e.getKey();
    //                 }
    //             }
    //         }
    //     }
    //     return null;
    // }

    public void ler_pix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("teste.pix"))) {
            String linha;

            Map<String, String[]> tabela_token = validar_tokens();

            Pattern prioridadeA = Pattern.compile("([a-zA-Z0-9$>'\"=<:|!&{}]+)"); // Exemplo de regex para tokens de prioridade A
            // Alternativa para não ter que percorrer o Map de tokens toda vez
            List<String> tokens = new ArrayList<>();
            while ((linha = reader.readLine()) != null) {
                Matcher matcher = prioridadeA.matcher(linha);

                while (matcher.find()) {
                    String lex = matcher.group();
                    String tipo = verificarToken(tabela_token, lex); // verifica se existe & retorna tipo (key) do token
                    System.out.println("Encontrado: " + lex + " => " + (tipo != null ? tipo : "UNKWN"));
                    if (tipo != null) {
                        tokens.add("<" + tipo + ", " + lex + ">");
                    } else {
                        Pattern special_param = Pattern.compile("("+ tabela_token.get("VAR") +")"); // Exemplo de regex para tokens de prioridade A
                    }
                }


                if (!tokens.isEmpty()) {
                    System.out.println("Tokens linha: " + tokens);
                }
                //Pattern pattern = Pattern.compile("[A-Z]+");
                //Matcher matcher = pattern.matcher(linha);
                //System.out.println(matcher.find() ? "Encontrado: " + matcher.group() : "Não encontrado.") ;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }
    }
}
