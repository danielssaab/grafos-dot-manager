import java.util.*;

class GrafoOrientado extends Grafo {
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

		Aresta aresta = new Aresta(origem, destino, peso);
		origem.adicionarAresta(aresta);
	}

	@Override
	public void removerAresta(String nomeOrigem, String nomeDestino) {
		Vertice origem = buscarVertice(nomeOrigem);
		Vertice destino = buscarVertice(nomeDestino);
		if (origem != null && destino != null) {
			origem.removerAresta(destino);
		}
	}

	public String calcularGraus() {
		StringBuilder saida = new StringBuilder();
		for (Vertice vertice : vertices) {
			int grauEntrada = 0;
			int grauSaida = vertice.getArestas().size();

			for (Vertice v : vertices) {
				for (Aresta aresta : v.getArestas()) {
					if (aresta.getDestino().equals(vertice)) {
						grauEntrada++;
					}
				}
			}
			saida.append("Vértice " + vertice.getNome() + " - Grau de entrada: " + grauEntrada + ", Grau de saída: " + grauSaida);
			saida.append("\n");
		}
		return  saida.toString();
	}

	public String getTipo(){return "Grafo Orientado";}

	@Override
	public String gerarDot() {
		if (vertices == null || vertices.isEmpty()) {
			return "digraph "+this.getNome() +" {\n}";
		}

		StringBuilder dot = new StringBuilder();
		dot.append("digraph "+this.getNome() +" {\n"); // Grafos direcionados começam com "digraph"

		for (Vertice vertice : vertices) {
			for (Aresta aresta : vertice.getArestas()) {
				if (aresta.getDestino() == null) {
					throw new IllegalStateException("A aresta não possui destino válido.");
				}

				String pesoString = aresta.getPeso() != 0 ? "[label=\"" + aresta.getPeso() + "\"]" : "";
				dot.append(
						"    \"" + vertice.getNome() + "\" -> \"" +
								aresta.getDestino().getNome() + "\" " + pesoString + ";\n"
				);
			}
			if (vertice.getArestas().isEmpty()) {
				dot.append("    \"").append(vertice.getNome()).append("\";\n");
			}
		}
		dot.append("}");
		return dot.toString();
	}

	@Override
	public String calcularArvoreGeradoraMinimaPrim(String nomeOrigem) {
		return "Invalido para Grafos Orientados";
	}

	@Override
	public String calcularArvoreGeradoraMinimaKruskal() {
		return "Invalido para Grafos Orientados";
	}

	public GrafoOrientado getGrafoReverso() {
		GrafoOrientado grafoReverso = new GrafoOrientado();
		grafoReverso.setNome(this.nome + "_Reverso");

		for (Vertice v : this.vertices) {
			grafoReverso.adicionarVertice(v.getNome());
		}

		//inversao das arestas
		for (Vertice v : this.vertices) {
			for (Aresta a : v.getArestas()) {
				grafoReverso.adicionarAresta(a.getDestino().getNome(), v.getNome(), a.getPeso());
			}
		}
		return grafoReverso;
	}

	private void ordenacaoTopologicaUtil(Vertice v, Set<Vertice> visitados, Stack<Vertice> pilha) {
		visitados.add(v);

		for (Aresta a : v.getArestas()) {
			Vertice vizinho = a.getDestino();
			if (!visitados.contains(vizinho)) {
				ordenacaoTopologicaUtil(vizinho, visitados, pilha);
			}
		}
		pilha.push(v);
	}

	public Stack<Vertice> ordenacaoTopologica() {
		Stack<Vertice> pilha = new Stack<>();
		Set<Vertice> visitados = new HashSet<>();

		for (Vertice v : this.vertices) {
			if (!visitados.contains(v)) {
				ordenacaoTopologicaUtil(v, visitados, pilha);
			}
		}
		return pilha;
	}

	private void DFSUtilCFC(Vertice v, Set<Vertice> visitados, List<Vertice> componente) {
		visitados.add(v);
		componente.add(v);

		for (Aresta a : v.getArestas()) {
			Vertice vizinho = a.getDestino();
			if (!visitados.contains(vizinho)) {
				DFSUtilCFC(vizinho, visitados, componente);
			}
		}
	}

	public List<List<Vertice>> encontrarCFCs() {
		// Passo 1: Ordenação topológica do grafo original
		Stack<Vertice> pilha = this.ordenacaoTopologica();

		// Passo 2: Criação do grafo reverso
		GrafoOrientado grafoReverso = this.getGrafoReverso();

		// Passo 3: Exploração dos CFCs no grafo reverso
		Set<Vertice> visitados = new HashSet<>();
		List<List<Vertice>> cfcList = new ArrayList<>();

		while (!pilha.isEmpty()) {
			Vertice v = pilha.pop();
			Vertice vReverso = grafoReverso.buscarVertice(v.getNome());

			if (!visitados.contains(vReverso)) {
				List<Vertice> componente = new ArrayList<>();
				grafoReverso.DFSUtilCFC(vReverso, visitados, componente);
				cfcList.add(componente);
			}
		}
		return cfcList;
	}
	public Vertice buscarVertice(String nome) {
		for (Vertice v : this.vertices) {
			if (v.getNome().equals(nome)) {
				return v;
			}
		}
		return null;
	}

	public GrafoOrientado gerarGrafoReduzido() {
		// Passo 1: Encontrar os CFCs
		List<List<Vertice>> cfcList = this.encontrarCFCs();

		// Passo 2: Criar um mapeamento de vértices para seus CFCs
		Map<Vertice, Integer> verticeParaCFC = new HashMap<>();
		Map<Integer, String> cfcParaNome = new HashMap<>();
		for (int i = 0; i < cfcList.size(); i++) {
			StringBuilder nomeCFC = new StringBuilder();
			for (Vertice v : cfcList.get(i)) {
				verticeParaCFC.put(v, i);
				nomeCFC.append(v.getNome()).append("_");
			}
			// Remove o último underscore
			if (nomeCFC.length() > 0) {
				nomeCFC.setLength(nomeCFC.length() - 1);
			}
			cfcParaNome.put(i, nomeCFC.toString());
		}

		// Passo 3: Criar o grafo reduzido
		GrafoOrientado grafoReduzido = new GrafoOrientado();
		grafoReduzido.setNome(this.nome + "_Reduzido");

		// Adicionar vértices ao grafo reduzido (um vértice para cada CFC)
		for (int i = 0; i < cfcList.size(); i++) {
			grafoReduzido.adicionarVertice(cfcParaNome.get(i));
		}

		// Adicionar arestas ao grafo reduzido
		for (Vertice v : this.vertices) {
			int cfcOrigem = verticeParaCFC.get(v);

			for (Aresta a : v.getArestas()) {
				Vertice destino = a.getDestino();
				int cfcDestino = verticeParaCFC.get(destino);

				// Se os CFCs forem diferentes, adiciona uma aresta no grafo reduzido
				if (cfcOrigem != cfcDestino) {
					grafoReduzido.adicionarAresta(cfcParaNome.get(cfcOrigem), cfcParaNome.get(cfcDestino), a.getPeso());
				}
			}
		}
		return grafoReduzido;
	}

	public String encontrarFConexos(){
		// Encontrar CFCs
		List<List<Vertice>> cfcList = this.encontrarCFCs();
		StringBuilder saida = new StringBuilder("Componentes Fortemente Conectados:\n");
		for (List<Vertice> componente : cfcList) {
			for (Vertice v : componente) {
				saida.append(v.getNome() + " ");
			}
			saida.append("\n");
		}
		return saida.toString();
	}
}