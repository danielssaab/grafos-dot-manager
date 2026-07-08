import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
	private Grafo grafo;

	public MainFrame() {
		setTitle("Gerenciador de Grafos");
		setSize(750, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Painel superior para escolha do tipo de grafo
		JPanel panelTop = new JPanel();
		JLabel labelEscolha = new JLabel("Escolha o tipo de grafo:");
		JButton btnOrientado = new JButton("Grafo Orientado");
		JButton btnNaoOrientado = new JButton("Grafo Não Orientado");
		panelTop.add(labelEscolha);
		panelTop.add(btnOrientado);
		panelTop.add(btnNaoOrientado);
		add(panelTop, BorderLayout.NORTH);

		// Painel central para exibição do grafo
		JTextArea textArea = new JTextArea(25, 50);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);

		// Painel inferior para operações com GridLayout
		JPanel panelBottom = new JPanel(new GridLayout(5, 4, 5, 5));
		Dimension buttonSize = new Dimension(150, 40); // Tamanho uniforme para os botões

		JButton btnAddVertice = new JButton("Adicionar Vértice");
		JButton btnRemoverVertice = new JButton("Remover Vértice");
		JButton btnAddAresta = new JButton("Adicionar Aresta");
		JButton btnRemoverAresta = new JButton("Remover Aresta");
		JButton btnExibirOrdem = new JButton("Exibir Ordem");
		JButton btnExibirGraus = new JButton("Exibir Graus");
		JButton btnExibirDot = new JButton("Exibir Dot");
		JButton btnCalcularCaminho = new JButton("Calcular Caminho");
		JButton btnCalcularMSTPrim = new JButton("Calc. MST Prim");
		JButton btnCalcularMSTKruskal= new JButton("Calc. MST Kruskal");
		JButton btnAbrirDot = new JButton("Abrir Dot");
		JButton btnSalvarDot = new JButton("Salvar Dot");
		JButton btnGerarPNG = new JButton("Gerar Imagem");
		JButton btnEncontrarFCon = new JButton("Encontrar f-conexos");
		JButton btnGerarGrafoReduzido = new JButton("Reduzir Grafo");
		JButton btnBuscaLargura = new JButton("Busca em Largura");
		JButton btnBuscaProfundidade = new JButton("Busca em Profundidade");

		// Definindo tamanho uniforme para os botões
		JButton[] botoesDesativar = {
			btnAddVertice,
			btnRemoverVertice,
			btnAddAresta,
			btnRemoverAresta,
			btnExibirOrdem,
			btnExibirGraus,
			btnExibirDot,
			btnCalcularCaminho,
			btnCalcularMSTPrim,
			btnAbrirDot,
			btnSalvarDot,
			btnGerarPNG,
			btnCalcularMSTKruskal,
			btnEncontrarFCon,
			btnGerarGrafoReduzido,
			btnBuscaLargura,
			btnBuscaProfundidade
		};
		JButton[] botoesAtivar = {
			btnAddVertice,
			btnRemoverVertice,
			btnAddAresta,
			btnRemoverAresta,
			btnExibirOrdem,
			btnExibirGraus,
			btnExibirDot,
			btnSalvarDot,
			btnCalcularCaminho,
			btnGerarPNG,
			btnCalcularMSTPrim,
			btnCalcularMSTKruskal,
			btnEncontrarFCon,
			btnGerarGrafoReduzido,
			btnBuscaLargura,
			btnBuscaProfundidade
		};

		JButton[] botoesDesativarOrientado = {
			btnCalcularMSTPrim,
			btnCalcularMSTKruskal
		};

		JButton[] botoesDesativarNaoOrientado = {
			btnEncontrarFCon,
			btnGerarGrafoReduzido
		};

		ativaDesativaBotoes(botoesDesativar, false);
		for (JButton btn : botoesDesativar) {
			btn.setPreferredSize(buttonSize);
			panelBottom.add(btn);
		}
		ativaDesativaBotoes(botoesDesativar, false);

		add(panelBottom, BorderLayout.SOUTH);
		btnAbrirDot.setEnabled(true);

		//escolha do tipo de grafo
		btnOrientado.addActionListener(e -> {
			grafo = new GrafoOrientado();
			String nomeGrafo = JOptionPane.showInputDialog(this, "Digite o nome do grafo(Opcional):");
			if (nomeGrafo != null && !nomeGrafo.trim().isEmpty()) {
				grafo.setNome(nomeGrafo);
			}
			textArea.setText("");
			textArea.append("Grafo orientado selecionado.\n");
			ativaDesativaBotoes(botoesAtivar, true);
			ativaDesativaBotoes(botoesDesativarOrientado, false);
			ativaDesativaBotoes(botoesDesativarNaoOrientado, true);

		});

		btnNaoOrientado.addActionListener(e -> {
			grafo = new GrafoNaoOrientado();
			String nomeGrafo = JOptionPane.showInputDialog(this, "Digite o nome do grafo(Opcional):");
			if (nomeGrafo != null && !nomeGrafo.trim().isEmpty()) {
				grafo.setNome(nomeGrafo);
			}
			textArea.setText("");
			textArea.append("Grafo não orientado selecionado.\n");
			ativaDesativaBotoes(botoesAtivar, true);
			ativaDesativaBotoes(botoesDesativarNaoOrientado, false);
			ativaDesativaBotoes(botoesDesativarOrientado, true);
		});

		// adição de vértices
		btnAddVertice.addActionListener(e -> {
			String nomeVertice = JOptionPane.showInputDialog(this, "Digite o nome do vértice:");
			if (nomeVertice != null && !nomeVertice.trim().isEmpty()) {
				grafo.adicionarVertice(nomeVertice);
				textArea.append("Vértice " + nomeVertice + " adicionado.\n");
			}
		});

		// remoção de vértices
		btnRemoverVertice.addActionListener(e -> {
			String nomeVertice = JOptionPane.showInputDialog(this, "Digite o nome do vértice a ser removido:");
			if (nomeVertice != null && !nomeVertice.trim().isEmpty()) {
				grafo.removerVertice(nomeVertice);
				textArea.append("Vértice " + nomeVertice + " removido.\n");
			}
		});

		// adição de arestas
		btnAddAresta.addActionListener(e -> {
			String origem = JOptionPane.showInputDialog(this, "Digite o vértice de origem:");
			String destino = JOptionPane.showInputDialog(this, "Digite o vértice de destino:");
			String strPeso = JOptionPane.showInputDialog(this, "Digite o peso da aresta:");
			strPeso = strPeso.isEmpty() ? "0" : strPeso;
			double peso = Double.parseDouble(strPeso);
			if (origem != null && destino != null && !origem.trim().isEmpty() && !destino.trim().isEmpty()) {
				grafo.adicionarAresta(origem, destino, peso);
				textArea.append("Aresta de " + origem + " para " + destino + " adicionada.\n");
			}
		});

		// remoção de arestas
		btnRemoverAresta.addActionListener(e -> {
			String nomeOrigem = JOptionPane.showInputDialog(this, "Digite o vértice de origem da aresta a ser removida:");
			String nomeDestino = JOptionPane.showInputDialog(this, "Digite o vértice de destino da aresta a ser removida:");
			if (nomeOrigem != null && nomeDestino != null && !nomeOrigem.trim().isEmpty() && !nomeDestino.trim().isEmpty()) {
				grafo.removerAresta(nomeOrigem, nomeDestino);
				textArea.append("Aresta de " + nomeOrigem + " para " + nomeDestino + " removida.\n");
			}
		});

		// exibição da ordem do grafo
		btnExibirOrdem.addActionListener(e -> {
			if (grafo != null) {
				textArea.append("Ordem do grafo: " + grafo.ordem() + "\n");
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		// exibição dos graus dos vértices
		btnExibirGraus.addActionListener(e -> {
			if (grafo != null) {
				if (grafo instanceof GrafoOrientado) {
					textArea.append("Graus dos vértices (orientado):\n");
					textArea.append(grafo.calcularGraus());
				} else {
					textArea.append("Graus dos vértices (não orientado):\n");
					textArea.append(grafo.calcularGraus());
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		btnExibirDot.addActionListener(e -> {
			if (grafo != null) {
				textArea.append("Representação DOT do grafo:\n");
				textArea.append(grafo.gerarDot() + "\n");
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//Salvar Dot
		btnSalvarDot.addActionListener(e -> {
			if (grafo != null) {
				try {
					String arquivo = JOptionPane.showInputDialog(this, "Digite o nome do arquivo para salvar (com extensão .dot):");
					if (arquivo != null && !arquivo.trim().isEmpty()) {
						DotFileHandler.salvarDot(grafo, arquivo);
						textArea.append("Arquivo DOT salvo como: " + arquivo + "\n");
					}
				} catch (Exception ex) {
					textArea.append("Erro ao salvar o arquivo DOT: " + ex.getMessage() + "\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//abrir dot
		btnAbrirDot.addActionListener(e -> {
			try {
				String caminhoArquivo = JOptionPane.showInputDialog(this, "Digite o caminho do arquivo DOT:");
				if (caminhoArquivo != null && !caminhoArquivo.trim().isEmpty()) {
					grafo = DotFileHandler.abrirDot(caminhoArquivo);
					textArea.setText("");
					textArea.append("Arquivo DOT carregado com sucesso.\n"+ grafo.getTipo()+": "+grafo.getNome() + "\n" );
					ativaDesativaBotoes(botoesAtivar, true);
					if (grafo instanceof GrafoOrientado) {
						ativaDesativaBotoes(botoesDesativarOrientado, false);
						ativaDesativaBotoes(botoesDesativarNaoOrientado, true);
					} else {
						ativaDesativaBotoes(botoesDesativarNaoOrientado, false);
						ativaDesativaBotoes(botoesDesativarOrientado, true);
					}
				}
			} catch (Exception ex) {
				textArea.append("Erro ao abrir o arquivo DOT: " + ex.getMessage() + "\n");
			}
		});

		// calcular o menor caminho
		btnCalcularCaminho.addActionListener(e -> {
			if (grafo != null) {
				String origem = JOptionPane.showInputDialog(this, "Digite o vértice de origem:");
				if (origem != null && !origem.trim().isEmpty()) {
					try {
						textArea.append(grafo.getMenorCaminho(origem) + "\n");
					} catch (Exception ex) {
						textArea.append("Erro: " + ex.getMessage() + "\n");
					}
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//gerar imagem
		btnGerarPNG.addActionListener(e -> {
			if (grafo != null) {
				String caminhoPNG = JOptionPane.showInputDialog(this, "Digite o caminho do arquivo .png:");
				if (caminhoPNG != null && !caminhoPNG.trim().isEmpty()) {
					try {
						grafo.gerarPNG(grafo.gerarDot(), caminhoPNG);
						textArea.append("Arquivo PNG salvo\n");
					} catch (Exception ex) {
						textArea.append("Erro: " + ex.getMessage() + "\n");
					}
				}
				else {
					textArea.append("Nome do arquivo inválido\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//calcular MST Prim
		btnCalcularMSTPrim.addActionListener(e -> {
			if (grafo != null) {
				String verticeInicial = JOptionPane.showInputDialog(this, "Selecione o vértice inicial:");
				if (verticeInicial != null && !verticeInicial.trim().isEmpty()) {
					try {
						grafo.calcularArvoreGeradoraMinimaPrim(verticeInicial);
						textArea.append(grafo.calcularArvoreGeradoraMinimaPrim(verticeInicial)+"\n");
					} catch (Exception ex) {
						textArea.append("Erro: " + ex.getMessage() + "\n");
					}
				}
				else {
					textArea.append("Vertice inválido\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//calcular MST Kruskal
		btnCalcularMSTKruskal.addActionListener(e -> {
			if (grafo != null) {
				try {
					textArea.append(grafo.calcularArvoreGeradoraMinimaKruskal()+"\n");
				}
				catch (Exception ex) {
					textArea.append("Erro: " + ex.getMessage() + "\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		//calcular f-conexos
		btnEncontrarFCon.addActionListener(e -> {
			if (grafo != null&&grafo instanceof GrafoOrientado) {
				try {
					GrafoOrientado grafoAux = (GrafoOrientado) grafo;
					textArea.append(grafoAux.encontrarFConexos()+"\n");
				}
				catch (Exception ex) {
					textArea.append("Erro: " + ex.getMessage() + "\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo orientado.\n");
			}
		});

		//gerar grafo reduzido
		btnGerarGrafoReduzido.addActionListener(e -> {
			if (grafo != null&&grafo instanceof GrafoOrientado) {
				try {
					GrafoOrientado grafoAux = (GrafoOrientado)grafo;
					textArea.append(grafoAux.gerarGrafoReduzido().gerarDot());
				}
				catch (Exception ex) {
					textArea.append("Erro: " + ex.getMessage() + "\n");
				}
			} else {
				textArea.append("Selecione um tipo de grafo orientado.\n");
			}
		});

		btnBuscaLargura.addActionListener(e -> {
			if (grafo != null) {
				String verticeInicial = JOptionPane.showInputDialog(this, "Digite o vértice inicial:");
				if (verticeInicial != null && !verticeInicial.trim().isEmpty()) {
					try {
						String ordem = grafo.buscaEmLargura(verticeInicial);
						textArea.append("Ordem de visitação (BFS): " + ordem + "\n");
					} catch (Exception ex) {
						textArea.append("Erro: " + ex.getMessage() + "\n");
					}
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});

		btnBuscaProfundidade.addActionListener(e -> {
			if (grafo != null) {
				String verticeInicial = JOptionPane.showInputDialog(this, "Digite o vértice inicial:");
				if (verticeInicial != null && !verticeInicial.trim().isEmpty()) {
					try {
						String ordem = grafo.buscaEmProfundidade(verticeInicial);
						textArea.append("Ordem de visitação (DFS): " + ordem + "\n");
					} catch (Exception ex) {
						textArea.append("Erro: " + ex.getMessage() + "\n");
					}
				}
			} else {
				textArea.append("Selecione um tipo de grafo primeiro.\n");
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		});
	}

	private void ativaDesativaBotoes(JButton[] botoes, boolean b) {
		for (JButton btn : botoes) {
			btn.setEnabled(b);
		}
	}
}


