package ifgoiano;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Analisador l = new Analisador();

        try (Scanner scanner = new Scanner(System.in)) {
            try {
                System.out.print("Informe o nome ou diretório do arquivo .pix: ");
                String nome = scanner.nextLine().trim();

                if (!nome.toLowerCase().endsWith(".pix")) {
                    System.out.println("Arquivo, nome ou diretório inválido. Tente novamente.");
                } else {
                    l.ler_pix(nome);
                    l.tabela_simbolos_csv();
                    l.tokens_pixobj();
                }
            } catch (Exception e) {
                logException(e);
                System.out.println("Ocorreu um erro. Veja o arquivo error.log para detalhes.");
            }
        }
    }

    private static void logException(Exception e) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data = LocalDateTime.now().format(fmt);

        try (FileWriter fw = new FileWriter("error.log", true);
            PrintWriter pw = new PrintWriter(fw)) {
            pw.println("[" + data + "] " + e.toString());
            
            for (StackTraceElement el : e.getStackTrace()) {
                pw.println("    at " + el.toString());
            }
            pw.println();
        } catch (IOException ioe) {
            System.err.println("Falha ao gravar error.log: " + ioe.getMessage());
        }
    }
}