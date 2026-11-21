# 🖨️ Integração Java com Impressora Elgin i9
### Controlando uma Impressora Térmica Elgin
---

## 📘 Sobre o Projeto

Programa em Java, todo no arquivo `Main.java`, que se comunica com a impressora térmica **Elgin i8 ou i9** pela biblioteca oficial da Elgin (`E1_Impressora01.dll`). O Java conversa com a DLL por meio do **JNA** (`jna-5.15.0.jar`).

O programa pede login, mostra um menu em laço e manda a impressora imprimir texto, QR Code, código de barras e cupons SAT, apitar, abrir a gaveta, avançar o papel e informar o status. Todo comando tem o retorno conferido, e nenhuma opção de impressão faz perguntas enquanto a conexão estiver fechada.

Trabalho da disciplina de **Lógica de Programação**.

---

## 🧰 Tecnologias Utilizadas

- **Java 11 ou mais novo**
- **IntelliJ IDEA**
- **JNA 5.15.0** (ponte entre o Java e a DLL)
- **DLL E1_Impressora01** da Elgin (64 bits)

---

## 📁 Estrutura do Projeto

```
Main.java              → código-fonte (único arquivo editado)
E1_Impressora01.dll    → biblioteca da Elgin (na raiz, ao lado do Main.java)
jna-5.15.0.jar         → biblioteca JNA
XMLSAT.xml             → XML de teste do cupom SAT (dados fictícios)
CANC_SAT.xml           → XML de teste do cancelamento SAT (dados fictícios)
```

---

## ⚙️ Como Executar

1. Abra a pasta do projeto no **IntelliJ IDEA** (File → Open) e confie no projeto.
2. Configure o JDK em *File → Project Structure → Project → SDK* (JDK 11 ou mais novo).
3. Adicione o JNA em *File → Project Structure → Libraries → + → Java* e escolha `jna-5.15.0.jar`.
4. Confira se `E1_Impressora01.dll` está na raiz do projeto, ao lado do `Main.java`.
5. Em *Run → Edit Configurations*, deixe o **Working directory** apontando para a pasta do projeto.
6. Ligue a impressora, conecte o cabo USB e rode o `Main` (▶️ Run).
7. Faça login, use a opção **2** para abrir a conexão e depois teste as demais opções.

Se o seu Java for de 32 bits (veja com `java -version`), troque a DLL pela versão 32 bits (x86).

---

## 🔑 Login de Teste

- **Usuário:** `admin`
- **Senha:** `1234`

São permitidas 3 tentativas. O programa avisa quantas restam e encerra se todas forem erradas.

---

## 📋 Opções do Menu

| Opção | O que faz |
|---|---|
| 1 | Configurar conexão (tipo, modelo, porta e parâmetro) |
| 2 | Abrir conexão |
| 3 | Fechar conexão |
| 4 | Imprimir texto (com número de cópias) |
| 5 | Imprimir QR Code |
| 6 | Imprimir código de barras (CODE 128) |
| 7 | Imprimir cupom XML SAT (`XMLSAT.xml`) |
| 8 | Imprimir cancelamento XML SAT (`CANC_SAT.xml`) |
| 9 | Abrir gaveta de dinheiro |
| 10 | Sinal sonoro (bips) |
| 11 | Avançar papel |
| 12 | Consultar status da impressora |
| 0 | Sair (fecha a conexão antes de encerrar) |

Configuração padrão: tipo = 1 (USB), modelo = "i9", conexão = "USB", parâmetro = 0.

---

## 📚 Documentação

- [Documentação da DLL Elgin E1](https://elgindevelopercommunity.github.io/group___m1.html)

---

## 👥 Autores
*(em ordem alfabética)*

- Gabriel Lopes
- Igor Germano
