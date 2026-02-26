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
        t.validar_tokens();
    }

     // Verifica se o token é válido & retorna o tipo (key) do token
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
    private String verificarTokenVariavel(Map<String, ArrayList<String>> tabela_tokens, String lexema) {
        List<String> keys = Arrays.asList("VAR", "NUMI", "NUMD", "NUL", "BOL");

        // "Mapeia" cada elemento do ArrayList para a sua respectiva chave (key)
        Map<String,String> sub_map = tabela_tokens.entrySet().stream()
            .filter(entry -> keys.contains(entry.getKey()))
            .flatMap(entry -> entry.getValue().stream()
                .map(v -> new java.util.AbstractMap.SimpleEntry<>(v, entry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<String,String> e : sub_map.entrySet()) {
            String key = "(" + e.getKey() + ")";
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(lexema.trim());

            if (m.find()) {
                return e.getValue();
            }
        }

        return null;
    }

    /**
     * Esta classe realiza a leitura de arquivos do tipo .pix, oriundos da linguagem de programação
     * PixScript. É a principal classe do Analisador Léxico, e, de fato, é um analisador léxico
     * propriamente dito.
     * 
     * As palavras-chaves, tipos e nomes de variáveis, operadores, e etc são capturados via REGEX.
     * Optamos por utilizar duas expressão regulares gerais, que distinguim os tokens encontrados
     * em duas prioridades: 
     * A- Captura somente tokens fixos, que não tem os seus "nomes" modificados;
     * B- Captura tokens variáveis, os quais os nomes não são fixos, mas sim do tipo REGEX.
     * 
     * @param filename
     * @throws IOException
     */
    public void ler_pix(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String linha;

            Map<String, ArrayList<String>> tabela_token = t.validar_tokens();
            Integer id = 0;
        
            Pattern prioridadeA = Pattern.compile("LEDGER|LET|IF|\\{|\\}|::|\\$>|<-|CLOSE|\\+\\+|\\-\\-|!=|==|\\*\\*|\\/\\/|%%|\\(|\\)|&&|\\|\\||!!|>>|<<|>=|<="); // Exemplo de regex para tokens de prioridade A
            //Pattern prioridadeB = Pattern.compile("([\\w_$!#?@áãêõ.]+)");
            Pattern prioridadeB = Pattern.compile("(\\w+\\.\\w+)|([\\S])+");

            while ((linha = reader.readLine()) != null) {
                // Análise quais palavras batem (match) com a exp. regular até que não reste mais caracteres.
                while (linha != null && !linha.isEmpty()) {

                    Matcher matcherA = prioridadeA.matcher(linha);
                    if (matcherA.find()) {
                        String lex = matcherA.group();
                        String tipo_token = verificarToken(tabela_token, lex); 

                        if (tipo_token != null) {
                            // Evita sobrescrever tokens já existentes
                            if (this.tabela_simbolos.putIfAbsent(lex, new Token(id, lex.trim(), tipo_token)) == null) {
                                id++;
                            }
                            this.tokens.add(new Token(lex.trim(), tipo_token));

                            // Remove a parte encontrada (matched)
                            linha = linha.replaceFirst(Pattern.quote(lex), ""); 

                            // Verificar mais casos onde isso ocorre. Ex: BOL -> TRUE, FALSE.
                            // Como as prioridades B (tokens variáveis) acontecem depois de A
                            // Quando há dois caracteres fixos 
                            if (linha.trim().startsWith("(") ||
                                linha.trim().startsWith("++") ||
                                linha.trim().startsWith("**") ||
                                linha.trim().startsWith("--") ||
                                linha.trim().startsWith("//") ||
                                linha.trim().startsWith("%%") ||
                                linha.trim().startsWith("!=") ||
                                linha.trim().startsWith("==") ||
                                linha.trim().startsWith(">>") ||
                                linha.trim().startsWith("<<") ||
                                linha.trim().startsWith(">=") ||
                                linha.trim().startsWith("<=") ||
                                linha.trim().startsWith("!!") ||
                                linha.trim().startsWith("&&") ||
                                linha.trim().startsWith("||") ||
                                linha.trim().startsWith("{") ||
                                linha.trim().startsWith(")") ||
                                linha.trim().startsWith("}")) {
                                continue;
                            }
                        }
                    }

                    // Para armazenar tokens do tipo TEXTO
                    if (linha.trim().startsWith("'")) {

                            // Adicionar token antes & depois do texto
                            if (this.tabela_simbolos.putIfAbsent("'", new Token(id, "'", "ASPS")) == null) {
                                id++;
                            }
                            this.tokens.add(new Token("'", "ASPS"));

                            String aux[] = linha.split("'", 3);
                            if (this.tabela_simbolos.putIfAbsent(aux[1], new Token(id, aux[1], "TEXTO")) == null) {
                                id++;
                            }
                            this.tokens.add(new Token(aux[1], "TEXTO"));
                            this.tokens.add(new Token("'", "ASPS"));

                            linha = aux[2];
                            continue;
                    }

                    if (linha.trim().startsWith("\"")) {

                            // Adicionar token antes & depois do texto
                            if (this.tabela_simbolos.putIfAbsent("\"", new Token(id, "'", "ASPD")) == null) {
                                id++;
                            }
                            this.tokens.add(new Token("\"", "ASPD"));

                            String aux[] = linha.split("\"", 3);
                            if (this.tabela_simbolos.putIfAbsent(aux[1], new Token(id, aux[1], "CPIX")) == null) {
                                id++;
                            }
                            this.tokens.add(new Token(aux[1], "CPIX"));
                            this.tokens.add(new Token("\"", "ASPD"));

                            linha = aux[2];
                            continue;
                    }


                    Matcher matcherB = prioridadeB.matcher(linha);
                    if (matcherB.find()) {
                        String lex = matcherB.group();
                        // Verifica se o token é válido
                        String tipo_token_var = verificarTokenVariavel(tabela_token, lex);

                        if (tipo_token_var != null) {
                            lex = lex.trim(); // Não é do tipo texto
                            if (this.tabela_simbolos.putIfAbsent(lex, new Token(id, lex, tipo_token_var)) == null) {
                                id++;
                            }
                            this.tokens.add(new Token(lex, tipo_token_var));

                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        } else if (!lex.trim().isEmpty()) { // Evita adicionar tokens vazios ou apenas com espaços
                            if (this.tabela_simbolos.putIfAbsent(lex, new Token(id, lex, "ID")) == null) {
                                id++;
                            }
                            this.tokens.add(new Token(lex, "ID"));

                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        } else {
                            // Remove os espaços em branco, para evitar loops
                            linha = linha.replaceFirst(Pattern.quote(lex), "");
                            continue;
                        }
                    }

                    // Linha = ""
                    break;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void tokens_pixobj() {
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
            e.printStackTrace();
        }
    }
}