public class Aresta {
	private final Vertice origem;
	private final Vertice destino;
	private double peso = 0;

	public Aresta(Vertice origem, Vertice destino) {
		this.origem = origem;
		this.destino = destino;
	}

	public Aresta(Vertice origem, Vertice destino, double peso) {
		this.origem = origem;
		this.destino = destino;
		this.peso = peso;
	}

	public Vertice getOrigem() {
		return origem;
	}

	public Vertice getDestino() {
		return destino;
	}

	public double getPeso() {return peso;}
	public void setPeso(double peso) {this.peso = peso;}

	@Override
	public String toString() {
		if (peso > 0) return origem + " -> " + destino + " [peso=" + peso + "]";
		return origem + " -> " + destino;
	}
}