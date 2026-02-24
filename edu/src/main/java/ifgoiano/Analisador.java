package ifgoiano;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Analisador {

    String caminho;
    //Map<String, String[]> tabela_simbolos;
    Map<String, ArrayList<String>> tabela_tokens;

    public Analisador() {
        this.caminho = "edu/src/main/resources/";
        this.tabela_tokens = validar_tokens(); // Preencher a tabela com o método validar_tokens()
    }
    
    public HashMap<String, ArrayList<String>> validar_tokens() {
        //String caminho = "edu/src/main/resources/token_table.txt";
        //System.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        //System.out.println(System.getProperty("user.dir"));
        this.tabela_tokens = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.caminho + "token_table.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                // Removendo colchetes desnecessários e dividindo a linha em tipo e lexemas
                linha = linha.replace("[", "");
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

    private String verificarToken(Map<String, ArrayList<String>> tabela_tokens, String lexema) {
        for (Map.Entry<String, ArrayList<String>> e : tabela_tokens.entrySet()) {
            ArrayList<String> valores = e.getValue();
            if (valores != null) {
                for (String v : valores) {
                    if (v != null && v.equals(lexema)) {
                        //System.out.println("-------------- Token encontrado: " + lexema); // Debug: Verificar se o token foi encontrado
                        return e.getKey();
                    }
                }
            }
        }
        return null;
    }

    // Verificar validade de token não-fixo (Ex: VAR = $[a-zA-Z0-9]+)
    // token não-fixo = token regex
    private String verificarTokenVariavel(Map<String, ArrayList<String>> tabela_tokens, String linha) {
        List<String> keys = Arrays.asList("VAR", "NUMI", "NUMD", "CPIX", "ID");
        // Map<String, ArrayList<String>> sub_map = tabela_tokens.entrySet().stream()
        //     .filter(entry -> keys.contains(entry.getKey()))
        //     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, ArrayList<String>> sub_map = keys.stream()
            .filter(tabela_tokens::containsKey)
            .collect(HashMap::new, (m, k) -> m.put(k, tabela_tokens.get(k)), HashMap::putAll);

        sub_map.forEach((k, v) -> {
            Pattern pattern = Pattern.compile("(" + k + ")"); // Exemplo de regex para tokens de variável
            Matcher matcher = pattern.matcher(linha);
            if (matcher.matches()) {
                return linha;
            }
        });
        // filterKeys.forEach(v -> {
        //     Pattern pattern = Pattern.compile("(" + v + ")"); // Exemplo de regex para tokens de variável
        //     Matcher matcher = pattern.matcher(linha);
        //     if (matcher.matches()) {
        //          System.out.println("--------------------------------: " + linha);
        //          return linha;
        //     }
        // });
        
        return null;
    }

    public void ler_pix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("teste.pix"))) {
            String linha;

            Map<String, ArrayList<String>> tabela_token = validar_tokens();
            LinkedHashMap<Integer, Token> tokens = new LinkedHashMap<>();
            Integer id = 0;
        
            Pattern prioridadeA = Pattern.compile("([a-zA-Z0-9$\\+\\-*\\/%()=<>'\"&@#?|!:{}.]+)"); // Exemplo de regex para tokens de prioridade A

            while ((linha = reader.readLine()) != null) {
                Matcher matcher = prioridadeA.matcher(linha);

                while (matcher.find()) {
                    String lex = matcher.group();
                    String tipo_token = verificarToken(tabela_token, lex); // verifica se existe & retorna tipo (key) do token

                    if (tipo_token == null && !lex.trim().isEmpty()) { // Se o token não for encontrado na tabela, verificar se é um token de variável
                        System.out.println("Resto -> " + lex);
                        String tipo_token_var = verificarTokenVariavel(tabela_token, lex); // verifica se é um token de variável
                    } else {
                        System.out.println(id + " - Token encontrado: " + lex + " -> Tipo: " + tipo_token); // Debug: Verificar se o token foi encontrado e seu tipo
                        tokens.put(id, new Token(id, lex, tipo_token));
                        id++;
                    }

                    // 2º Alternativa para armazenar os tokens com os seus tipos e id: HashSet
                    //String lex2 = linha.substring(lex.length()); // Pega o que vem depois do token encontrado
                }


                // if (!tokens.isEmpty()) {
                //     System.out.println("Tokens linha: " + tokens);
                // }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }
    }
}
