package ifgoiano;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Analisador l = new Analisador();
        l.ler_pix();

        // l.validar_tokens();
        //l.ler_pix();

        // String linha = "LEDGER transferencia";

        // Pattern pattern = Pattern.compile("([a-zA-Z0-9$>'\\\"=<:|!&{}]+)"); // Exemplo de regex para tokens de variável
        // Matcher matcher = pattern.matcher(linha);

        // if (matcher.find()) {
        //     System.out.println("Token encontrado: " + matcher.group(1)); // Debug: Verificar se o token VAR foi encontrado
        //     String[] aux = linha.split(matcher.group());

        //     for (String a : aux) {
        //         System.out.println("Aux: " + a);
        //     }
        // } else {
        //     System.out.println("Nenhum token encontrado");
        // }
    }
}