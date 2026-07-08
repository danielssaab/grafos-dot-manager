import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vertice {
	private final String nome;
	private final List<Aresta> arestas;

	public Vertice(String nome) {
		this.nome = nome;
		this.arestas = new ArrayList<>();
	}

	public String getNome() {
		return nome;
	}

	public List<Aresta> getArestas() {
		return arestas;
	}

	public void adicionarAresta(Aresta aresta) {
		arestas.add(aresta);
	}

	public int getGrau() {
		return arestas.size(); // Grau do vértice (apenas em grafos não orientados)
	}

	public void removerAresta(Vertice destino) {
		Iterator<Aresta> it = arestas.iterator();
		while (it.hasNext()) {
			Aresta aresta = it.next();
			if (aresta.getDestino().equals(destino)) {
				it.remove();
				break;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Vertice vertice = (Vertice) obj;
		return nome.equals(vertice.nome);
	}

	@Override
	public int hashCode() {
		return nome.hashCode();
	}

	@Override
	public String toString() {
		return nome;
	}

}