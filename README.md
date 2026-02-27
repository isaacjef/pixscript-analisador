# pixscript-analisador

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
     ├── pixscript-analisador
     │   └── edu
     │       └── src
     │           └── main
     |                └── java/ifgoiano
     |                |   ├── Analisador.java
     |                |   ├── Token.java
     |                |   └── Main.java
     |                └── resources
     ├── target         └── token_table.txt
     ├── README.md
     ├── pom.xml
     └── teste.pix
     .

# 🖥️ Diagrama de Classes

<p align="center">
 <img width="490" height="309" alt="image" src="https://github.com/user-attachments/assets/cfa5f7a0-1c96-4256-856b-8157b5027a30" />
</p>

# 📜 Etapas do Programa

1º - Criar a lista de tokens válidos.
<p align="center">
 <img width="918" height="793" alt="image" src="https://github.com/user-attachments/assets/c959d09e-532a-4053-8bfb-8d86411dd1f1" />
</p>
2º - Ler todos os símbolos da tabela definidora de tokens.
<p align="center">
 <img width="1033" height="699" alt="image" src="https://github.com/user-attachments/assets/d601c534-95c9-484e-b841-665849fda9cb" />
</p>
3º - Analisar caracteres do código .pix

Método ler_pix()

4º - Criar arquivo .csv da tabela de símbolos.
<p align="center">
 <img width="1018" height="555" alt="image" src="https://github.com/user-attachments/assets/9f0fdf5f-c840-4569-94f7-3d9e174f92c4" />
</p>

5º - Criar arquivo .pixobj para todos os tokens.
<p align="center">
 <img width="919" height="635" alt="image" src="https://github.com/user-attachments/assets/16c42470-2b33-4d6f-a3ab-1478ed3db774" />
</p>
