# pixscript-compiler

projeto
A partir da linguagem apresentada, deseja-se criar um analisador léxico para geração de
código tokenizado e da tabela de símbolos. A seguir as características do programa:
- O analisador léxico poderá ser escrito nas linguagens: Java; Python; Javascript; C
ou C++;
- O programa deverá receber um arquivo no formato pix. O conteúdo do arquivo é
um texto;
- O programa deverá gerar um arquivo no formato pixobj. O conteúdo do arquivo é
um texto contendo o código tokenizado;
- O programa deverá gerar um arquivo no formato csv. O conteúdo do arquivo é um
texto no formato CSV contendo a tabela de símbolos;
- Caso haja algum erro durante o processo da análise léxica, será gerado um arquivo
log contendo o erro que foi gerado.

Exemplo de um código completo escrito em PIX Script.
```
LEDGER transferencia
 LET @nome = 'Denecley Alvim'
 LET @produto = 'Placa Nvidia 5070RTX'
 LET $valor = 4999.99
 LET !pix = "soinformatica@gmail.com"
 IF (!pix != "") {
 $> 'Realizar transferência'
 }
 :: {
 $> 'Aguardando chave PIX'
 }
CLOSE
```

# 📦 Estrutura do Programa
     .
     ├── turing_machine
     │   └── edu
     │       └── src
     │           └── main
     |                └── java/ifgoiano
     |                |   ├── Analisador_Lexico.java
     |                |   ├── Leitor.java
     |                |   └── Main.java
     |                └── resources
     ├── target         ├── token_table.txt
     ├── README.md
     ├── pom.xml
     └── teste.pix
     .

# 📦 Etapas do Programa

1º [X] - Criar a lista de tokens válidos.

![Print da token_table.txt](image.png)

Token.java
public HashMap<String, ArrayList<String>> validar_tokens()

Tabela de lista de tokens válidos. (Por enquanto) O token do tipo ID (IDENTIFIER), deve ficar na última posição,
para que outros tipos não sejam detectados pelo mesmo, ocorrendo incongruencias entre os tipos.
**(Criar tipo NUL: NULO, e TEX: '[...]')

2º [] - Ler todos os símbolos do código de entrada.



3º - Reunir todos lexemas, menos espaçamentos.
4º - Tokenizar.
5º - Criar a tabela de símbolos.
6º - Substituir os lexemas por id.
