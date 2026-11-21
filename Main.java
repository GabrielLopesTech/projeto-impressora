import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Scanner;
import java.nio.file.*;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                Paths.get("E1_Impressora01.dll").toAbsolutePath().toString(), ImpressoraDLL.class); // DLL na raiz do projeto

        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin(int pino, int ti, int tf);

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    // ===================== VARIÁVEIS DO PROGRAMA =====================

    // Login e senha corretos
    private static final String USUARIO = "admin";
    private static final String SENHA = "1234";

    // Chave do QR Code usada no cancelamento do XML SAT
    private static final String ASS_QRCODE = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    // Dados da conexão (valores padrão para teste: USB com i9)
    private static boolean conexaoAberta = false;
    private static int tipo = 1;            // 1 = USB, 2 = Serial, 3 = Rede, 4 = Bluetooth
    private static String modelo = "i9";    // "i8" ou "i9"
    private static String conexao = "USB";  // "USB" para USB
    private static int parametro = 0;       // 0 para USB
    private static final Scanner scanner = new Scanner(System.in); // Scanner estático e final

    // ===================== FUNÇÕES DE ENTRADA =====================

    // Função para capturar entradas do usuário
    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    // Mostra a mensagem e devolve um número. Se digitar letras, pergunta de novo
    private static int lerInteiro(String mensagem) {
        while (true) {
            String texto = capturarEntrada(mensagem).trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite apenas números.");
            }
        }
    }

    // Lê um arquivo (XML) e devolve o texto dele, ou null se não encontrar
    private static String lerArquivo(String nome) {
        try {
            return new String(Files.readAllBytes(Paths.get(nome)), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== LOGIN =====================

    // Pede usuário e senha com 3 tentativas. Devolve true se acertar, false se errar as 3
    public static boolean fazerLogin() {
        System.out.println("=========== LOGIN ===========");
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            String usuario = capturarEntrada("Usuário: ");
            String senha = capturarEntrada("Senha: ");

            if (usuario.equals(USUARIO) && senha.equals(SENHA)) {
                System.out.println("Login realizado com sucesso!");
                return true;
            }

            int restantes = 3 - tentativa;
            if (restantes > 0) {
                System.out.println("Usuário ou senha incorretos. Tentativas restantes: " + restantes);
            }
        }
        // Chegou aqui: errou as 3 vezes
        System.out.println("Você errou 3 vezes. Acesso bloqueado.");
        return false;
    }

    // ===================== CONEXÃO =====================

    // Função para configurar conexão
    public static void configurarConexao() {
        if (conexaoAberta) {
            System.out.println("A conexão está aberta. Feche-a (opção 3) antes de mudar a configuração.");
            return;
        }
        System.out.println("Tipos: 1 = USB, 2 = Serial, 3 = Rede (IP), 4 = Bluetooth");
        tipo = lerInteiro("Tipo de conexão: ");
        modelo = capturarEntrada("Modelo da impressora (i8 ou i9): ").trim();
        conexao = capturarEntrada("Porta / conexão (ex.: USB): ").trim();
        parametro = lerInteiro("Parâmetro (0 para USB): ");
        System.out.println("Configuração salva! Use a opção 2 para abrir a conexão.");
    }

    public static void abrirConexao() {
        // Função para abrir a conexão com a impressora
        if (!conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso.");
            } else {
                System.out.println("Erro ao abrir conexão. Código de erro: " + retorno);
            }
        } else {
            System.out.println("Conexão já está aberta.");
        }
    }

    // Fecha a conexão e coloca conexaoAberta = false
    public static void fecharConexao() {
        if (!conexaoAberta) {
            System.out.println("A conexão já está fechada.");
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (retorno == 0) {
            conexaoAberta = false;
            System.out.println("Conexão fechada com sucesso.");
        } else {
            System.out.println("Erro ao fechar conexão. Código de erro: " + retorno);
        }
    }

    // ===================== IMPRESSÃO E COMANDOS =====================
    // Todos começam conferindo a conexão: se estiver fechada, avisam e saem sem perguntar nada.

    // Imprime um texto quantas vezes o usuário pedir
    public static void imprimirTexto() {
        if (!conexaoAberta) {                                  // 1) conexão aberta?
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        String texto = capturarEntrada("Texto a imprimir: ");  // 2) só agora pergunta
        int copias = lerInteiro("Quantas cópias? ");

        for (int i = 1; i <= copias; i++) {
            // 3) chama o comando e guarda o retorno
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto, 1, 4, 0);
            if (retorno >= 0) {                                // 4) ImpressaoTexto: erro só se for negativo
                System.out.println("Cópia " + i + " impressa!");
            } else {
                System.out.println("Erro ao imprimir. Código: " + retorno);
            }
        }
        cortarPapel();                                         // 5) corta o papel
    }

    // Pergunta o conteúdo e imprime um QR Code
    public static void imprimirQRCode() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        String dados = capturarEntrada("Conteúdo do QR Code (texto ou link): ");

        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6, 4);
        if (retorno == 0) {
            System.out.println("QR Code impresso!");
        } else {
            System.out.println("Erro ao imprimir o QR Code. Código: " + retorno);
        }
        cortarPapel();
    }

    // Imprime um código de barras CODE 128
    public static void imprimirCodigoBarras() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        // tipo 8 = CODE 128 (texto começa com {A), altura 100, largura 2, HRI 3 = número acima e abaixo
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A012345678912", 100, 2, 3);
        if (retorno == 0) {
            System.out.println("Código de barras impresso!");
        } else {
            System.out.println("Erro ao imprimir o código de barras. Código: " + retorno);
        }
        cortarPapel();
    }

    // Pergunta o nome do arquivo, lê o XML e imprime o cupom SAT
    public static void imprimirXMLSAT() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        String nome = capturarEntrada("Nome do arquivo (ex.: XMLSAT.xml): ").trim();
        String xml = lerArquivo(nome);
        if (xml == null) {
            System.out.println("Não foi possível ler o arquivo: " + nome);
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);
        if (retorno == 0) {
            System.out.println("Cupom SAT impresso!");
        } else {
            System.out.println("Erro ao imprimir o XML SAT. Código: " + retorno);
        }
        cortarPapel();
    }

    // Igual ao anterior, mas para o XML de cancelamento (usa a ASS_QRCODE)
    public static void imprimirXMLCancelamentoSAT() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        String nome = capturarEntrada("Nome do arquivo (ex.: CANC_SAT.xml): ").trim();
        String xml = lerArquivo(nome);
        if (xml == null) {
            System.out.println("Não foi possível ler o arquivo: " + nome);
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xml, ASS_QRCODE, 0);
        if (retorno == 0) {
            System.out.println("Cupom de cancelamento SAT impresso!");
        } else {
            System.out.println("Erro ao imprimir o cancelamento SAT. Código: " + retorno);
        }
        cortarPapel();
    }

    // Abre a gaveta de dinheiro
    public static void abrirGaveta() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
        if (retorno == 0) {
            System.out.println("Gaveta aberta!");
        } else {
            System.out.println("Erro ao abrir a gaveta. Código: " + retorno);
        }
    }

    // Pergunta quantos bips e faz a impressora apitar
    public static void sinalSonoro() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        int quantidade = lerInteiro("Quantos bips? (1 a 63) ");
        if (quantidade < 1 || quantidade > 63) {
            System.out.println("Quantidade inválida. Use um número de 1 a 63.");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5);
        if (retorno == 0) {
            System.out.println("Bip!");
        } else {
            System.out.println("Erro no sinal sonoro. Código: " + retorno);
        }
    }

    // (extra) Pergunta quantas linhas e avança o papel
    public static void avancarPapel() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        int linhas = lerInteiro("Quantas linhas avançar? ");
        if (linhas < 1) {
            System.out.println("Digite um número maior que zero.");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        if (retorno == 0) {
            System.out.println("Papel avançado " + linhas + " linha(s)!");
        } else {
            System.out.println("Erro ao avançar o papel. Código: " + retorno);
        }
    }

    // (extra) Consulta o status geral da impressora (5 = geral)
    public static void consultarStatus() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes (opção 2).");
            return;
        }
        int retorno = ImpressoraDLL.INSTANCE.StatusImpressora(5);
        if (retorno >= 0) {
            System.out.println("Status da impressora (valor devolvido): " + retorno);
        } else {
            System.out.println("Erro ao consultar o status. Código: " + retorno);
        }
    }

    // Auxiliar: avança e corta o papel, conferindo o retorno
    private static void cortarPapel() {
        int retorno = ImpressoraDLL.INSTANCE.Corte(5);
        if (retorno != 0) {
            System.out.println("Aviso: erro ao cortar o papel. Código: " + retorno);
        }
    }

    // ===================== MENU =====================

    public static void exibirMenu() {
        System.out.println("\n*************************************************");
        System.out.println("**************** MENU IMPRESSORA *******************");
        System.out.println("*************************************************\n");

        System.out.println("Conexão: " + (conexaoAberta ? "ABERTA" : "FECHADA"));
        System.out.println("1  - Configurar Conexao");
        System.out.println("2  - Abrir Conexao");
        System.out.println("3  - Fechar Conexao");
        System.out.println("4  - Imprimir Texto");
        System.out.println("5  - Imprimir QR Code");
        System.out.println("6  - Imprimir Codigo de Barras");
        System.out.println("7  - Imprimir XML SAT");
        System.out.println("8  - Imprimir XML Cancelamento SAT");
        System.out.println("9  - Abrir Gaveta");
        System.out.println("10 - Sinal Sonoro");
        System.out.println("11 - Avancar Papel");
        System.out.println("12 - Status da Impressora");
        System.out.println("0  - Sair");
    }

    // ===================== PROGRAMA PRINCIPAL =====================

    public static void main(String[] args) throws java.io.IOException {
        if (!fazerLogin()) {
            scanner.close();
            return; // errou 3 vezes: encerra o programa
        }

        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            opcao = lerInteiro("\nDigite a opção desejada: ");

            if (opcao == 1) {
                configurarConexao();
            } else if (opcao == 2) {
                abrirConexao();
            } else if (opcao == 3) {
                fecharConexao();
            } else if (opcao == 4) {
                imprimirTexto();
            } else if (opcao == 5) {
                imprimirQRCode();
            } else if (opcao == 6) {
                imprimirCodigoBarras();
            } else if (opcao == 7) {
                imprimirXMLSAT();
            } else if (opcao == 8) {
                imprimirXMLCancelamentoSAT();
            } else if (opcao == 9) {
                abrirGaveta();
            } else if (opcao == 10) {
                sinalSonoro();
            } else if (opcao == 11) {
                avancarPapel();
            } else if (opcao == 12) {
                consultarStatus();
            } else if (opcao == 0) {
                if (conexaoAberta) {
                    fecharConexao(); // fecha a conexão antes de sair
                }
                System.out.println("Programa encerrado.");
            } else {
                System.out.println("Opção inválida.");
            }
        }

        scanner.close(); // Fecha o scanner ao final do programa
    }
}
