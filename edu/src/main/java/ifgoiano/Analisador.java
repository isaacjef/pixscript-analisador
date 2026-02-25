package ifgoiano;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Analisador {

    Token t = new Token(null, null);
    //Map<String, String[]> tabela_simbolos;
    //Map<String, ArrayList<String>> tabela_tokens;

    public Analisador() {
        //this.caminho = "edu/src/main/resources/";
        //this.tabela_tokens = validar_tokens(); // Preencher a tabela com o método validar_tokens()
        t.validar_tokens();
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
        List<String> keys = Arrays.asList("VAR", "NUMI", "NUMD", "CPIX");

        // Map each individual element of the ArrayList to its token type
        Map<String,String> sub_map = tabela_tokens.entrySet().stream()
            .filter(entry -> keys.contains(entry.getKey()))
            .flatMap(entry -> entry.getValue().stream()
                .map(v -> new java.util.AbstractMap.SimpleEntry<>(v, entry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //System.out.println("Sub-mapa para tokens variáveis: " + sub_map); // Debug: Verificar o conteúdo do sub-mapa

        for (Map.Entry<String,String> e : sub_map.entrySet()) {
            Pattern pattern = Pattern.compile("(" + e.getKey() + ")");
            Matcher matcher = pattern.matcher(linha);

            if (matcher.find()) {
                //System.out.println(matcher.group() + "-------------- Token variável encontrado: " + e.getValue()); // Debug: Verificar se o token variável foi encontrado
                return e.getValue();
            }
        }

        return null;
    }

    public void ler_pix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("teste.pix"))) {
            String linha;

            Map<String, ArrayList<String>> tabela_token = t.validar_tokens();
            LinkedHashMap<String, Token> tokens = new LinkedHashMap<>();
            Integer id = 0;
        
            Pattern prioridadeA = Pattern.compile("([A-Z<\\-$<>(){}=!:\"]+)"); // Exemplo de regex para tokens de prioridade A
            Pattern prioridadeB = Pattern.compile("([\\w _$!#?@áãêõ.]+)");

            //A tratar -> ("") NIL, ("), ('), ('TEXTO'), ("PIX")
            while ((linha = reader.readLine()) != null) {
                // Iteratively match tokens on the current line until no more matches
                while (linha != null && !linha.isEmpty()) {
                    // Try prioridadeA first
                    Matcher matcherA = prioridadeA.matcher(linha);
                    if (matcherA.find()) {
                        String lex = matcherA.group();
                        String tipo_token = verificarToken(tabela_token, lex); // verifica se existe & retorna tipo (key) do token

                        if (tipo_token != null) {
                            System.out.println("AAA ->  " + lex);

                            // Evita sobrescrever tokens já existentes
                            Token t = tokens.putIfAbsent(lex, new Token(id, lex.trim(), tipo_token));
                            if (t == null) {
                                id++;
                            }

                            linha = linha.replaceFirst(Pattern.quote(lex), ""); // Remove the matched lexeme and continue
                            continue;
                        }
                        // if prioridadeA matched but not a fixed token, fall through to prioridadeB
                    }

                    // Try prioridadeB (identifiers/variables)
                    // Matcher matcherB = prioridadeB.matcher(linha);
                    // if (matcherB.find()) {
                    //     String lex = matcherB.group();
                    //     String tipo_token_var = verificarTokenVariavel(tabela_token, lex); // verifica se é um token variável

                    //     //System.out.println("BBB ->  " + lex);
                    //     if (tipo_token_var != null) {
                    //         lex = lex.trim(); // Não é do tipo texto
                    //         tokens.put(lex, new Token(id, lex, tipo_token_var));
                    //         id++;
                    //         linha = linha.replaceFirst(Pattern.quote(lex), "");
                    //         continue;
                    //     } else if (!lex.trim().isEmpty()) { // Evita adicionar tokens vazios ou apenas com espaços
                    //         tokens.put(lex, new Token(id, lex, "ID"));
                    //         id++;
                    //         linha = linha.replaceFirst(Pattern.quote(lex), "");
                    //         continue;
                    //     } else {
                    //         // Remove os espaços em branco, para evitar loops
                    //         linha = linha.replaceFirst(Pattern.quote(lex), "");
                    //         continue;
                    //     }
                    // }

                    // // No matcher found any more lexemes on this line
                    break;
                }
            }
            
            //System.out.println("Tipo - Lexema - ID" + tokens);
            tokens.forEach((key, value) -> System.out.println(value.getTipo() + " - " + key + " - " + value.getId()));
        
            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }
    }
}
