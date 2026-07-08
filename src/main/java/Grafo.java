import java.io.IOException;
import java.util.*;
import br.com.davesmartins.api.Graph;

abstract class Grafo {
	String nome;
	protected List<Vertice> vertices;

	public Grafo() {
		this.vertices = new ArrayList<>();
	}

	public int ordem() {
		return vertices.size();
	}
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	public abstract void removerAresta(String nomeOrigem, String nomeDestino);
	public abstract void adicionarAresta(String nomeOrigem, String nomeDestino, double peso);
	public abstract String getTipo();
	public abstract String calcularGraus();
	public abstract String gerarDot();
	public abstract String calcularArvoreGeradoraMinimaPrim(String nomeOrigem);
	public abstract String calcularArvoreGeradoraMinimaKruskal();

	public void adicionarVertice(String nome) {
		Vertice vertice = new Vertice(nome);
		if (!vertices.contains(vertice)) {
			vertices.add(vertice);
		}
	}

	public Vertice buscarVertice(String nome) {
		for (Vertice v : vertices) {
			if (v.getNome().equals(nome)) {
				return v;
			}
		}
		return null;
	}

	public void removerVertice(String nome) {
		Vertice vertice = buscarVertice(nome);
		if (vertice != null) {
			// Remove arestas que têm o vértice como destino
			for (Vertice v : vertices) {
				v.removerAresta(vertice);
			}
			// Remove o próprio vértice
			vertices.remove(vertice);
		}
	}

	public Map<String, Double> calcularMenorCaminho(String nomeOrigem) {
		Vertice origem = buscarVertice(nomeOrigem);
		if (origem == null) {
			throw new IllegalArgumentException("O vértice de origem não existe no grafo.");
		}

		//armazenar as menores distâncias
		Map<String, Double> distancias = new HashMap<>();
		for (Vertice v : vertices) {
			distancias.put(v.getNome(), Double.POSITIVE_INFINITY);
		}
		distancias.put(nomeOrigem, 0.0);

		//armazenar os predecessores
		Map<String, String> predecessores = new HashMap<>();

		//fila de prioridade para explorar os vértices
		PriorityQueue<VerticeDistancia> fila = new PriorityQueue<>(Comparator.comparingDouble(v -> v.distancia));
		fila.add(new VerticeDistancia(origem, 0.0));

		while (!fila.isEmpty()) {
			VerticeDistancia atual = fila.poll();
			Vertice verticeAtual = atual.vertice;

			//verifica as arestas do vértice atual
			for (Aresta aresta : verticeAtual.getArestas()) {
				Vertice vizinho = aresta.getDestino();
				double novaDistancia = distancias.get(verticeAtual.getNome()) + aresta.getPeso();

				if (novaDistancia < distancias.get(vizinho.getNome())) {
					distancias.put(vizinho.getNome(), novaDistancia);
					predecessores.put(vizinho.getNome(), verticeAtual.getNome());
					fila.add(new VerticeDistancia(vizinho, novaDistancia));
				}
			}
		}
		return distancias;
	}

	public String getMenorCaminho(String origem){
		Map<String, Double> menoresCaminhos = this.calcularMenorCaminho(origem);
		StringBuilder saida = new StringBuilder("Menores caminhos a partir do vértice " + origem + ":\n");
		for (Map.Entry<String, Double> entrada : menoresCaminhos.entrySet()) {
			saida.append(entrada.getKey() + ": " + entrada.getValue() + "\n");
		}
		return saida.toString();
	}

	private static class VerticeDistancia {
		Vertice vertice;
		double distancia;

		public VerticeDistancia(Vertice vertice, double distancia) {
			this.vertice = vertice;
			this.distancia = distancia;
		}
	}

	public String gerarPNG(String grafoDot, String nomeArquivo){
		try {
			Graph.createStringDotToPng(grafoDot,nomeArquivo);
			Graph.testDotToPng();
			return("Gerou a imagem");
		} catch (IOException ex) {
			return("Não foi possível gerar a imagem");
		}
	}

	public String buscaEmLargura(String nomeOrigem) {
		Vertice origem = buscarVertice(nomeOrigem);
		if (origem == null) {
			throw new IllegalArgumentException("O vértice de origem não existe no grafo.");
		}

		StringBuilder ordemVisita = new StringBuilder();
		Queue<Vertice> fila = new LinkedList<>();
		Set<Vertice> visitados = new HashSet<>();

		fila.add(origem);
		visitados.add(origem);

		while (!fila.isEmpty()) {
			Vertice atual = fila.poll();
			ordemVisita.append(atual.getNome()).append(" ");

			for (Aresta aresta : atual.getArestas()) {
				Vertice vizinho = aresta.getDestino();
				if (!visitados.contains(vizinho)) {
					visitados.add(vizinho);
					fila.add(vizinho);
				}
			}
		}

		return ordemVisita.toString().trim();
	}

	public String buscaEmProfundidade(String nomeOrigem) {
		Vertice origem = buscarVertice(nomeOrigem);
		if (origem == null) {
			throw new IllegalArgumentException("O vértice de origem não existe no grafo.");
		}

		StringBuilder ordemVisita = new StringBuilder();
		Set<Vertice> visitados = new HashSet<>();
		Stack<Vertice> pilha = new Stack<>();

		pilha.push(origem);
		visitados.add(origem);

		while (!pilha.isEmpty()) {
			Vertice atual = pilha.pop();
			ordemVisita.append(atual.getNome()).append(" ");

			for (Aresta aresta : atual.getArestas()) {
				Vertice vizinho = aresta.getDestino();
				if (!visitados.contains(vizinho)) {
					visitados.add(vizinho);
					pilha.push(vizinho);
				}
			}
		}

		return ordemVisita.toString().trim();
	}
}
