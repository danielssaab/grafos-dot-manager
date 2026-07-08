import java.util.*;

class GrafoNaoOrientado extends Grafo {
	@Override
	public void adicionarAresta(String nomeOrigem, String nomeDestino, double peso) {
		Vertice origem = buscarVertice(nomeOrigem);
		Vertice destino = buscarVertice(nomeDestino);

		if (origem == null) {
			origem = new Vertice(nomeOrigem);
			vertices.add(origem);
		}
		if (destino == null) {
			destino = new Vertice(nomeDestino);
			vertices.add(destino);
		}

		Aresta arestaOrigemDestino = new Aresta(origem, destino, peso);
		Aresta arestaDestinoOrigem = new Aresta(destino, origem, peso);

		origem.adicionarAresta(arestaOrigemDestino);
		destino.adicionarAresta(arestaDestinoOrigem);
	}

	@Override
	public void removerAresta(String nomeOrigem, String nomeDestino) {
		Vertice origem = buscarVertice(nomeOrigem);
		Vertice destino = buscarVertice(nomeDestino);
		if (origem != null && destino != null) {
			origem.removerAresta(destino);
			destino.removerAresta(origem);
		}
	}

	public String calcularGraus() {
		StringBuilder saida = new StringBuilder();
		for (Vertice vertice : vertices) {
			int grau = vertice.getArestas().size();
			saida.append("Vértice ").append(vertice.getNome())
					.append(" - Grau: ").append(grau)
					.append("\n");
		}
		return saida.toString();
	}

	public String getTipo(){return "Grafo Não Orientado";}

	@Override
	public String gerarDot() {
		StringBuilder dot = new StringBuilder();
		dot.append("graph "+this.getNome() +" {\n"); // Grafos não direcionados começam com "graph"

		Set<String> arestasAdicionadas = new HashSet<>(); // Para evitar duplicação de arestas

		for (Vertice vertice : vertices) {
			for (Aresta aresta : vertice.getArestas()) {
				String origem = vertice.getNome();
				String destino = aresta.getDestino().getNome();
				String representacao = origem.compareTo(destino) < 0
						? origem + "--" + destino
						: destino + "--" + origem;

				if (!arestasAdicionadas.contains(representacao)) {
					if (aresta.getPeso() != 0) {
						dot.append("    \"" + origem + "\" -- \"" + destino + "\" [label=\"" + aresta.getPeso() + "\"];\n");
					} else {
						dot.append("    \"" + origem + "\" -- \"" + destino + "\";\n");
					}
					arestasAdicionadas.add(representacao);
				}
			}
			if (vertice.getArestas().isEmpty()) {
				dot.append("    \"").append(vertice.getNome()).append("\";\n");
			}
		}

		dot.append("}");
		return dot.toString();
	}

	public String calcularArvoreGeradoraMinimaPrim(String nomeOrigem) {
		Vertice origem = buscarVertice(nomeOrigem);
		if (origem == null) {
			return "Vértice de origem não encontrado no grafo.";
		}

		// Fila de prioridade para selecionar a aresta de menor peso
		PriorityQueue<Aresta> fila = new PriorityQueue<>((a1, a2) -> Double.compare(a1.getPeso(), a2.getPeso()));

		// Conjunto para rastrear os vértices já incluídos na MST
		Set<Vertice> visitados = new HashSet<>();
		visitados.add(origem);

		// Adicionar todas as arestas do vértice inicial à fila
		fila.addAll(origem.getArestas());

		// Lista para armazenar as arestas que compõem a MST
		List<Aresta> mst = new ArrayList<>();
		double custoTotal = 0;

		while (!fila.isEmpty() && visitados.size() < vertices.size()) {
			Aresta menorAresta = fila.poll();

			// Verificar se a aresta conecta a um vértice não visitado
			if (!visitados.contains(menorAresta.getDestino())) {
				mst.add(menorAresta);
				custoTotal += menorAresta.getPeso();

				// Marcar o destino como visitado e adicionar suas arestas à fila
				Vertice novoVertice = menorAresta.getDestino();
				visitados.add(novoVertice);
				for (Aresta aresta : novoVertice.getArestas()) {
					if (!visitados.contains(aresta.getDestino())) {
						fila.add(aresta);
					}
				}
			}
		}

		// Verificar se todos os vértices foram conectados
		if (visitados.size() < vertices.size()) {
			return "O grafo não é conexo, portanto não é possível calcular a MST.";
		}

		// Construir a saída da MST
		StringBuilder resultado = new StringBuilder();
		resultado.append("Árvore Geradora Mínima (Prim):\n");
		for (Aresta aresta : mst) {
			resultado.append(aresta.toString()).append("\n");
		}
		resultado.append("Custo total: ").append(custoTotal);

		return resultado.toString();
	}

	private List<Aresta> kruskal() {
		List<Aresta> arestas = new ArrayList<>();
		for (Vertice vertice : vertices) {
			arestas.addAll(vertice.getArestas());
		}

		// Ordenar as arestas pelo peso
		arestas.sort(Comparator.comparingDouble(Aresta::getPeso));

		// Estruturas auxiliares para o algoritmo
		Map<Vertice, Vertice> representante = new HashMap<>();
		for (Vertice vertice : vertices) {
			representante.put(vertice, vertice); // Cada vértice é seu próprio representante inicialmente
		}

		List<Aresta> mst = new ArrayList<>();
		for (Aresta aresta : arestas) {
			Vertice origem = aresta.getOrigem();
			Vertice destino = aresta.getDestino();

			// Encontra os representantes dos conjuntos
			Vertice repOrigem = encontrar(representante, origem);
			Vertice repDestino = encontrar(representante, destino);

			// Se os representantes são diferentes, adiciona a aresta na MST
			if (!repOrigem.equals(repDestino)) {
				mst.add(aresta);
				unir(representante, repOrigem, repDestino); // Une os conjuntos
			}
		}
		return mst;
	}

	// Metodo para encontrar o representante de um conjunto
	private Vertice encontrar(Map<Vertice, Vertice> representante, Vertice vertice) {
		if (!representante.get(vertice).equals(vertice)) {
			representante.put(vertice, encontrar(representante, representante.get(vertice)));
		}
		return representante.get(vertice);
	}

	// Metodo para unir dois conjuntos
	private void unir(Map<Vertice, Vertice> representante, Vertice origem, Vertice destino) {
		representante.put(encontrar(representante, origem), encontrar(representante, destino));
	}

	public String calcularArvoreGeradoraMinimaKruskal(){
		List<Aresta> mst = this.kruskal();
		StringBuilder saida = new StringBuilder("Árvore Geradora Mínima:\n");
		for (Aresta aresta : mst) {
			saida.append(aresta.toString()).append("\n");
		}
		return saida.toString();
	}
}