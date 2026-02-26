package ifgoiano;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Analisador l = new Analisador();
        //l.validar_tokens();
        l.ler_pix();
        l.tabela_simbolos_csv();

    }
}