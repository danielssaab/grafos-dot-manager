import java.io.*;

public class DotFileHandler {

    public static void salvarDot(Grafo grafo, String caminhoArquivo) throws Exception {
        File arquivo = new File(caminhoArquivo);
        try {
            if (!arquivo.exists()) { // Se o arquivo não existe, cria um novo
                arquivo.createNewFile();
            }

            // Escreve o conteúdo do grafo em formato DOT no arquivo
            FileWriter fw = new FileWriter(arquivo);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(grafo.gerarDot());
            bw.newLine();

            // Fecha os buffers
            bw.close();
            fw.close();
        } catch (Exception e) {
            throw new Exception("Erro ao salvar o arquivo DOT no caminho: " + caminhoArquivo, e);
        }
    }

    public static Grafo abrirDot(String caminhoArquivo) throws Exception {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            throw new FileNotFoundException("O arquivo DOT no caminho especificado não foi encontrado: " + caminhoArquivo);
        }

        BufferedReader br = new BufferedReader(new FileReader(arquivo));
        Grafo grafo = null; // Grafo será criado dinamicamente dependendo do tipo
        String linha;

        try {
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                // Identificar o tipo do grafo na primeira linha
                if (linha.startsWith("digraph")) {
                    grafo = new GrafoOrientado();
                    String nomeGrafo = linha.split(" ")[1].replace("{", "").trim();
                    grafo.setNome(nomeGrafo);
                } else if (linha.startsWith("graph")) {
                    grafo = new GrafoNaoOrientado();
                    String nomeGrafo = linha.split(" ")[1].replace("{", "").trim();
                    grafo.setNome(nomeGrafo);
                }

                // Ignorar linhas vazias ou fechamentos
                if (linha.isEmpty() || linha.equals("}")) {
                    continue;
                }

                // Adicionar arestas
                if (linha.contains("->") || linha.contains("--")) {
                    boolean isOrientado = linha.contains("->");
                    String[] partes = linha.split(isOrientado ? "->" : "--");
                    String origem = limparNome(partes[0]);
                    String destino = limparNome(partes[1].split("\\[")[0]);

                    double peso = 0.0; // Peso padrão
                    if (linha.contains("label=")) {
                        String pesoStr = linha.substring(linha.indexOf("label=") + 6).split("]")[0].replace("\"", "").trim();
                        peso = Double.parseDouble(pesoStr);
                    }

                    grafo.adicionarAresta(origem, destino, peso);
                }
                // Adicionar vértices isolados
                else if (linha.endsWith(";")) {
                    String vertice = limparNome(linha.replace(";", ""));
                    grafo.adicionarVertice(vertice);
                }
            }

            if (grafo == null) {
                throw new IllegalArgumentException("O arquivo DOT é inválido ou está vazio.");
            }

        } finally {
            br.close();
        }
        return grafo;
    }

    // limpar e padronizar nomes de vértices
    private static String limparNome(String nome) {
        return nome.replace("\"", "").replace(";", "").trim();
    }
}