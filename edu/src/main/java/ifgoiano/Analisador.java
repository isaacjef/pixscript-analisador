package ifgoiano;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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
    LinkedHashMap<String, Token> tabela_simbolos = new LinkedHashMap<>();

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
        List<String> keys = Arrays.asList("VAR", "NUMI", "NUMD", "CPIX", "BOL");

        // Map each individual element of the ArrayList to its token type
        Map<String,String> sub_map = tabela_tokens.entrySet().stream()
            .filter(entry -> keys.contains(entry.getKey()))
            .flatMap(entry -> entry.getValue().stream()
                .map(v -> new java.util.AbstractMap.SimpleEntry<>(v, entry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //System.out.println("Sub-mapa para tokens variáveis: " + sub_map); // Debug: Verificar o conteúdo do sub-mapa

        for (Map.Entry<String,String> e : sub_map.entrySet()) {
            String key = "(" + e.getKey() + ")";
            Pattern p = Pattern.compile(key);
            //System.out.println("Verificando token variável: " + p + " contra linha: " + linha.trim());
            Matcher m = p.matcher(linha.trim());

            if (m.find()) {
                //System.out.println(linha + " -> " + m.group()); 
                // "-------------- Token variável encontrado: " + e.getValue()); // Debug: Verificar se o token variável foi encontrado
                return e.getValue();
            }
        }

        return null;
    }

    public void ler_pix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("teste.pix"))) {
            String linha;

            Map<String, ArrayList<String>> tabela_token = t.validar_tokens();
            // LinkedHashMap<String, Token> tokens = new LinkedHashMap<>();
            Integer id = 0;
        
            // desconsiderando |\"\" ("")
            Pattern prioridadeA = Pattern.compile("LEDGER|LET|IF|\\{|\\}|::|\\$>|<-|\"|CLOSE|\\+\\+|\\-\\-|!=|==|\\*\\*|\\/\\/|%%|\\(|\\)|&&|\\|\\||!!|>>|<<|>=|<="); // Exemplo de regex para tokens de prioridade A
            Pattern prioridadeB = Pattern.compile("([\\w_$!#?@áãêõ.]+)");

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
                            // System.out.println("AAA ->  " + lex);

                            // Evita sobrescrever tokens já existentes
                            Token t = tabela_simbolos.putIfAbsent(lex, new Token(id, lex.trim(), tipo_token));
                            if (t == null) {
                                id++;
                            }

                            linha = linha.replaceFirst(Pattern.quote(lex), ""); // Remove the matched lexeme and continue

                            // Verificar mais casos onde isso ocorre. Ex: BOL -> TRUE, FALSE.
                            if (linha.trim().startsWith("(") || linha.trim().startsWith("\"\"") || linha.trim().startsWith("'") || linha.trim().startsWith("{") || linha.trim().startsWith(")")) {
                                continue;
                            }
                        }
                        // if prioridadeA matched but not a fixed token, fall through to prioridadeB
                    }

                    if (linha.trim().startsWith("'")) { // Evita adicionar tokens vazios ou apenas com espaços
                           // System.out.println("TEXTO ->  " + linha);

                            // Adicionar token antes & depois do texto
                            //tokens.putIfAbsent("'", new Token(id, "'", "ASPS"));
                            if (tabela_simbolos.putIfAbsent("'", new Token(id, "'", "ASPS")) == null) {
                                id++;
                            }
                            //linha = linha.splitWithDelimiters("'", 2)[];
                            String aux[] = linha.split("'", 3);
                            if (tabela_simbolos.putIfAbsent(aux[1], new Token(id, aux[1], "TEXTO")) == null) {
                                id++;
                            }

                            linha = aux[2];
                          //  System.out.println("splitWithDelimiters ->  " + linha);
                            //continue;
                    }

                    // Try prioridadeB (identifiers/variables)
                    Matcher matcherB = prioridadeB.matcher(linha);
                    if (matcherB.find()) {
                        String lex = matcherB.group();
                        String tipo_token_var = verificarTokenVariavel(tabela_token, lex); // verifica se é um token variável

                        //System.out.println("BBB ->  " + tipo_token_var);
                        if (tipo_token_var != null) {
                            lex = lex.trim(); // Não é do tipo texto
                            if (tabela_simbolos.put(lex, new Token(id, lex, tipo_token_var)) == null) {
                                id++;
                            }
                            
                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        
                        } else if (!lex.trim().isEmpty()) { // Evita adicionar tokens vazios ou apenas com espaços
                            if (tabela_simbolos.put(lex, new Token(id, lex, "ID")) == null) {
                                id++;
                            }
                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        } else {
                            // Remove os espaços em branco, para evitar loops
                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        }
                    }

                    // Sem matches
                    break;
                }
            }
            
            //System.out.println("Tipo - Lexema - ID" + tabela_simbolos);
            //tabela_simbolos.forEach((key, value) -> System.out.println(value.getTipo() + " - " + key + " - " + value.getId()));
        
            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }
    }

    public void tabela_simbolos_csv() {
        // Implementar método para escrever a tabela de símbolos em um arquivo, se necessário
        //String texto = "Conteúdo a ser escrito no arquivo.";
        try (FileWriter writer = new FileWriter("arquivo.csv")) {
            writer.write("id,lexema,token\n");
            tabela_simbolos.forEach((key, value) -> {
                try {
                    writer.write(value.getId() + "," + key + "," + value.getTipo() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Arquivo escrito com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
