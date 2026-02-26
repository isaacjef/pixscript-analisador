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
    List<Token> tokens = new ArrayList<>();

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

        for (Map.Entry<String,String> e : sub_map.entrySet()) {
            String key = "(" + e.getKey() + ")";
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(linha.trim());

            if (m.find()) {
                return e.getValue();
            }
        }

        return null;
    }

    public void ler_pix(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String linha;

            Map<String, ArrayList<String>> tabela_token = t.validar_tokens();
            Integer id = 0;
        
            // desconsiderando |\"\" ("")
            Pattern prioridadeA = Pattern.compile("LEDGER|LET|IF|\\{|\\}|::|\\$>|<-|\"|CLOSE|\\+\\+|\\-\\-|!=|==|\\*\\*|\\/\\/|%%|\\(|\\)|&&|\\|\\||!!|>>|<<|>=|<="); // Exemplo de regex para tokens de prioridade A
            Pattern prioridadeB = Pattern.compile("([\\w_$!#?@áãêõ.]+)");

            //A tratar -> ("") NIL, ("), ('), ("PIX")
            while ((linha = reader.readLine()) != null) {
                // Análise quais palavras batem (match) com a exp. regular até que não reste mais caracteres.
                while (linha != null && !linha.isEmpty()) {
                    // Tenta prioridadeA primeiro
                    Matcher matcherA = prioridadeA.matcher(linha);
                    if (matcherA.find()) {
                        String lex = matcherA.group();
                        String tipo_token = verificarToken(tabela_token, lex); 
                        // verifica se existe & retorna tipo (key) do token

                        if (tipo_token != null) {
                            // Evita sobrescrever tokens já existentes
                            if (this.tabela_simbolos.putIfAbsent(lex, new Token(id, lex.trim(), tipo_token)) == null) {
                                this.tokens.add(new Token(lex.trim(), tipo_token));
                                id++;
                            }

                            linha = linha.replaceFirst(Pattern.quote(lex), ""); // Remove the matched lexeme and continue

                            // Verificar mais casos onde isso ocorre. Ex: BOL -> TRUE, FALSE.
                            //linha.trim().startsWith("\"\"") || 
                            if (linha.trim().startsWith("(") || 
                                linha.trim().startsWith("\"") || 
                                linha.trim().startsWith("'") || 
                                linha.trim().startsWith("{") || 
                                linha.trim().startsWith(")")) {
                                continue;
                            }
                        }
                        // if prioridadeA matched but not a fixed token, fall through to prioridadeB
                    }

                    // Evita adicionar tokens vazios ou apenas com espaços
                    if (linha.trim().startsWith("'")) {

                            // Adicionar token antes & depois do texto
                            if (this.tabela_simbolos.putIfAbsent("'", new Token(id, "'", "ASPS")) == null) {
                                this.tokens.add(new Token("'", "ASPS"));
                                id++;
                            }

                            String aux[] = linha.split("'", 3);
                            if (this.tabela_simbolos.putIfAbsent(aux[1], new Token(id, aux[1], "TEXTO")) == null) {
                                this.tokens.add(new Token(aux[1], "TEXTO"));
                                id++;
                            }
                            this.tokens.add(new Token("'", "ASPS"));

                            linha = aux[2];
                            continue;
                    }

                    // REGEX com prioridadeB (IDENTIFIERS/VAR)
                    Matcher matcherB = prioridadeB.matcher(linha);
                    if (matcherB.find()) {
                        String lex = matcherB.group();
                        String tipo_token_var = verificarTokenVariavel(tabela_token, lex); // verifica se é um token variável

                        //System.out.println("BBB ->  " + tipo_token_var);
                        if (tipo_token_var != null) {
                            lex = lex.trim(); // Não é do tipo texto
                            if (this.tabela_simbolos.put(lex, new Token(id, lex, tipo_token_var)) == null) {
                                this.tokens.add(new Token(lex, tipo_token_var));
                                id++;
                            }
                            
                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        
                        } else if (!lex.trim().isEmpty()) { // Evita adicionar tokens vazios ou apenas com espaços
                            if (this.tabela_simbolos.put(lex, new Token(id, lex, "ID")) == null) {
                                this.tokens.add(new Token(lex, "ID"));
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
            
            //tabela_simbolos.forEach((key, value) -> System.out.println(value.getTipo() + " - " + key + " - " + value.getId()));
            //tokens.forEach(token -> System.out.println("<"+ token.getTipo() + ", " + token.getId() + ">"));

            reader.close();
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de leitura
        }
    }

    // Método para escrever a tabela de símbolos em um arquivo, se necessário
    public void tabela_simbolos_csv() {
        try (FileWriter writer = new FileWriter("arquivo.csv")) {
            writer.write("id,lexema,token\n");
            tabela_simbolos.forEach((key, value) -> {
                try {
                    writer.write(value.getId() + "," + key + "," + value.getTipo() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Arquivo .csv escrito com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para escrever o arquivo .pixobj
    public void pixobj() {
        try (FileWriter writer = new FileWriter("arquivo.pixobj")) {
            tokens.forEach(token -> {
                try {
                    if (tabela_simbolos.containsKey(token.getLexema())) {
                        Token t = tabela_simbolos.get(token.getLexema());
                        writer.write("<" + t.getTipo() + ", " + t.getId() + ">\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Arquivo .pixobj escrito com sucesso.");
        } catch (IOException e) {
            e.printStackTrace(); // Trata erros de escrita
        }
    }
}
