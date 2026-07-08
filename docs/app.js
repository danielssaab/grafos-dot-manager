class ModeloGrafo {
  constructor({ orientado = true, nome = "grafo" } = {}) {
    this.orientado = orientado;
    this.nome = nome;
    this.adjacencias = new Map();
  }

  static criarDeDot(conteudoDot) {
    const texto = conteudoDot.replace(/\r/g, "").trim();
    const cabecalho = texto.match(/^(digraph|graph)(?:\s+([^\s{]+))?\s*\{/m);
    if (!cabecalho) {
      throw new Error("DOT invalido: cabecalho nao encontrado.");
    }

    const orientado = cabecalho[1] === "digraph";
    const grafo = new ModeloGrafo({ orientado, nome: cabecalho[2] || "grafo" });

    const linhas = texto
      .split("\n")
      .map((linha) => linha.trim())
      .filter((linha) => linha && linha !== "}" && !linha.startsWith("//"));

    for (const linha of linhas) {
      if (linha.startsWith("digraph") || linha.startsWith("graph")) {
        continue;
      }

      if (linha.includes("->") || linha.includes("--")) {
        const operador = linha.includes("->") ? "->" : "--";
        const partes = linha.match(/^"?([^"\[]+?)"?\s*(->|--)\s*"?([^"\[]+?)"?\s*(\[.*\])?\s*;?$/);
        if (!partes) {
          continue;
        }

        const origem = partes[1].trim();
        const destino = partes[3].trim();
        const pesoEncontrado = linha.match(/label\s*=\s*"?([-+]?[0-9]*\.?[0-9]+)"?/i);
        const peso = pesoEncontrado ? Number.parseFloat(pesoEncontrado[1]) : 0;

        if ((operador === "->" && grafo.orientado) || (operador === "--" && !grafo.orientado)) {
          grafo.adicionarAresta(origem, destino, Number.isNaN(peso) ? 0 : peso);
        }
      } else if (linha.endsWith(";")) {
        const nomeVertice = linha.replace(/;/g, "").replace(/"/g, "").trim();
        if (nomeVertice) {
          grafo.adicionarVertice(nomeVertice);
        }
      }
    }

    return grafo;
  }

  definirTipo(orientado) {
    this.orientado = orientado;
    this.adjacencias = new Map();
  }

  definirNome(nome) {
    this.nome = nome?.trim() || "grafo";
  }

  adicionarVertice(nome) {
    const chave = String(nome || "").trim();
    if (!chave) {
      throw new Error("Nome de vertice vazio.");
    }
    if (!this.adjacencias.has(chave)) {
      this.adjacencias.set(chave, new Map());
    }
  }

  removerVertice(nome) {
    const chave = String(nome || "").trim();
    this.adjacencias.delete(chave);
    for (const [, arestas] of this.adjacencias) {
      arestas.delete(chave);
    }
  }

  adicionarAresta(origem, destino, peso = 0) {
    const valor = Number(peso);
    const pesoSeguro = Number.isFinite(valor) ? valor : 0;

    this.adicionarVertice(origem);
    this.adicionarVertice(destino);

    this.adjacencias.get(origem).set(destino, pesoSeguro);
    if (!this.orientado) {
      this.adjacencias.get(destino).set(origem, pesoSeguro);
    }
  }

  removerAresta(origem, destino) {
    if (!this.adjacencias.has(origem) || !this.adjacencias.has(destino)) {
      return;
    }

    this.adjacencias.get(origem).delete(destino);
    if (!this.orientado) {
      this.adjacencias.get(destino).delete(origem);
    }
  }

  ordem() {
    return this.adjacencias.size;
  }

  listarVertices() {
    return [...this.adjacencias.keys()];
  }

  relatorioGraus() {
    const vertices = this.listarVertices();

    if (this.orientado) {
      const grauEntrada = new Map(vertices.map((v) => [v, 0]));
      const grauSaida = new Map(vertices.map((v) => [v, this.adjacencias.get(v).size]));

      for (const [, arestas] of this.adjacencias) {
        for (const destino of arestas.keys()) {
          grauEntrada.set(destino, (grauEntrada.get(destino) || 0) + 1);
        }
      }

      return vertices
        .map((v) => `Vertice ${v} - Grau de entrada: ${grauEntrada.get(v)}, Grau de saida: ${grauSaida.get(v)}`)
        .join("\n");
    }

    return vertices
      .map((v) => `Vertice ${v} - Grau: ${this.adjacencias.get(v).size}`)
      .join("\n");
  }

  gerarDot() {
    const linhas = [];
    const nomeGrafo = this.nome?.trim() || "grafo";
    const cabecalho = `${this.orientado ? "digraph" : "graph"} ${nomeGrafo} {`;
    const operador = this.orientado ? "->" : "--";

    if (this.adjacencias.size === 0) {
      return `${cabecalho}\n}`;
    }

    if (this.orientado) {
      for (const [origem, arestas] of this.adjacencias) {
        if (arestas.size === 0) {
          linhas.push(`    "${origem}";`);
          continue;
        }

        for (const [destino, peso] of arestas) {
          const rotulo = peso !== 0 ? ` [label="${peso}"]` : "";
          linhas.push(`    "${origem}" ${operador} "${destino}"${rotulo};`);
        }
      }
    } else {
      const jaAdicionadas = new Set();

      for (const [origem, arestas] of this.adjacencias) {
        if (arestas.size === 0) {
          linhas.push(`    "${origem}";`);
          continue;
        }

        for (const [destino, peso] of arestas) {
          const chave = [origem, destino].sort().join("--");
          if (jaAdicionadas.has(chave)) {
            continue;
          }

          jaAdicionadas.add(chave);
          const rotulo = peso !== 0 ? ` [label="${peso}"]` : "";
          linhas.push(`    "${origem}" ${operador} "${destino}"${rotulo};`);
        }
      }
    }

    return `${cabecalho}\n${linhas.join("\n")}\n}`;
  }

  calcularMenorCaminho(origem) {
    if (!this.adjacencias.has(origem)) {
      throw new Error("Vertice de origem nao existe.");
    }

    const distancias = new Map(this.listarVertices().map((v) => [v, Number.POSITIVE_INFINITY]));
    distancias.set(origem, 0);

    const fila = [{ vertice: origem, distancia: 0 }];

    while (fila.length > 0) {
      fila.sort((a, b) => a.distancia - b.distancia);
      const atual = fila.shift();

      if (atual.distancia > distancias.get(atual.vertice)) {
        continue;
      }

      for (const [vizinho, peso] of this.adjacencias.get(atual.vertice)) {
        const novaDistancia = distancias.get(atual.vertice) + peso;
        if (novaDistancia < distancias.get(vizinho)) {
          distancias.set(vizinho, novaDistancia);
          fila.push({ vertice: vizinho, distancia: novaDistancia });
        }
      }
    }

    return distancias;
  }

  relatorioMenorCaminho(origem) {
    const distancias = this.calcularMenorCaminho(origem);
    const linhas = [`Menores caminhos a partir do vertice ${origem}:`];

    for (const [vertice, distancia] of distancias) {
      linhas.push(`${vertice}: ${distancia}`);
    }

    return linhas.join("\n");
  }

  buscaEmLargura(origem) {
    if (!this.adjacencias.has(origem)) {
      throw new Error("Vertice de origem nao existe.");
    }

    const visitados = new Set([origem]);
    const fila = [origem];
    const ordem = [];

    while (fila.length > 0) {
      const atual = fila.shift();
      ordem.push(atual);

      for (const vizinho of this.adjacencias.get(atual).keys()) {
        if (!visitados.has(vizinho)) {
          visitados.add(vizinho);
          fila.push(vizinho);
        }
      }
    }

    return ordem.join(" ");
  }

  buscaEmProfundidade(origem) {
    if (!this.adjacencias.has(origem)) {
      throw new Error("Vertice de origem nao existe.");
    }

    const visitados = new Set([origem]);
    const pilha = [origem];
    const ordem = [];

    while (pilha.length > 0) {
      const atual = pilha.pop();
      ordem.push(atual);

      for (const vizinho of this.adjacencias.get(atual).keys()) {
        if (!visitados.has(vizinho)) {
          visitados.add(vizinho);
          pilha.push(vizinho);
        }
      }
    }

    return ordem.join(" ");
  }

  calcularArvoreGeradoraMinimaPrim(origem) {
    if (this.orientado) {
      return "Invalido para grafos orientados";
    }

    if (!this.adjacencias.has(origem)) {
      return "Vertice inicial nao encontrado";
    }

    const visitados = new Set([origem]);
    const arvore = [];
    const candidatos = [];
    let custoTotal = 0;

    const adicionarArestasDoVertice = (vertice) => {
      for (const [destino, peso] of this.adjacencias.get(vertice)) {
        if (!visitados.has(destino)) {
          candidatos.push({ origem: vertice, destino, peso });
        }
      }
    };

    adicionarArestasDoVertice(origem);

    while (candidatos.length > 0 && visitados.size < this.ordem()) {
      candidatos.sort((a, b) => a.peso - b.peso);
      const menor = candidatos.shift();

      if (visitados.has(menor.destino)) {
        continue;
      }

      visitados.add(menor.destino);
      arvore.push(menor);
      custoTotal += menor.peso;
      adicionarArestasDoVertice(menor.destino);
    }

    if (visitados.size < this.ordem()) {
      return "O grafo nao e conexo, portanto nao e possivel calcular a MST.";
    }

    const linhas = ["Arvore Geradora Minima (Prim):"];
    for (const aresta of arvore) {
      linhas.push(`${aresta.origem} -> ${aresta.destino}${aresta.peso !== 0 ? ` [peso=${aresta.peso}]` : ""}`);
    }
    linhas.push(`Custo total: ${custoTotal}`);

    return linhas.join("\n");
  }

  calcularArvoreGeradoraMinimaKruskal() {
    if (this.orientado) {
      return "Invalido para grafos orientados";
    }

    const representante = new Map();
    for (const vertice of this.listarVertices()) {
      representante.set(vertice, vertice);
    }

    const encontrar = (v) => {
      if (representante.get(v) !== v) {
        representante.set(v, encontrar(representante.get(v)));
      }
      return representante.get(v);
    };

    const unir = (a, b) => {
      representante.set(encontrar(a), encontrar(b));
    };

    const arestas = [];
    const vistas = new Set();

    for (const [origem, destinos] of this.adjacencias) {
      for (const [destino, peso] of destinos) {
        const chave = [origem, destino].sort().join("--");
        if (vistas.has(chave)) {
          continue;
        }
        vistas.add(chave);
        arestas.push({ origem, destino, peso });
      }
    }

    arestas.sort((a, b) => a.peso - b.peso);

    const arvore = [];
    for (const aresta of arestas) {
      const repOrigem = encontrar(aresta.origem);
      const repDestino = encontrar(aresta.destino);
      if (repOrigem !== repDestino) {
        arvore.push(aresta);
        unir(repOrigem, repDestino);
      }
    }

    const linhas = ["Arvore Geradora Minima (Kruskal):"];
    for (const aresta of arvore) {
      linhas.push(`${aresta.origem} -> ${aresta.destino}${aresta.peso !== 0 ? ` [peso=${aresta.peso}]` : ""}`);
    }

    return linhas.join("\n");
  }

  encontrarComponentesFortementeConectados() {
    if (!this.orientado) {
      throw new Error("F-conexos sao validos apenas para grafos orientados.");
    }

    const visitados = new Set();
    const pilhaOrdem = [];

    const dfsOrdem = (vertice) => {
      visitados.add(vertice);
      for (const vizinho of this.adjacencias.get(vertice).keys()) {
        if (!visitados.has(vizinho)) {
          dfsOrdem(vizinho);
        }
      }
      pilhaOrdem.push(vertice);
    };

    for (const vertice of this.listarVertices()) {
      if (!visitados.has(vertice)) {
        dfsOrdem(vertice);
      }
    }

    const reverso = new Map(this.listarVertices().map((v) => [v, []]));
    for (const [origem, destinos] of this.adjacencias) {
      for (const destino of destinos.keys()) {
        reverso.get(destino).push(origem);
      }
    }

    visitados.clear();
    const componentes = [];

    const dfsReverso = (vertice, componente) => {
      visitados.add(vertice);
      componente.push(vertice);

      for (const vizinho of reverso.get(vertice)) {
        if (!visitados.has(vizinho)) {
          dfsReverso(vizinho, componente);
        }
      }
    };

    while (pilhaOrdem.length > 0) {
      const vertice = pilhaOrdem.pop();
      if (!visitados.has(vertice)) {
        const componente = [];
        dfsReverso(vertice, componente);
        componentes.push(componente);
      }
    }

    return componentes;
  }

  relatorioComponentesFortementeConectados() {
    const componentes = this.encontrarComponentesFortementeConectados();
    const linhas = ["Componentes Fortemente Conectados:"];
    for (const componente of componentes) {
      linhas.push(componente.join(" "));
    }
    return linhas.join("\n");
  }

  gerarGrafoReduzido() {
    if (!this.orientado) {
      throw new Error("Grafo reduzido valido apenas para orientado.");
    }

    const componentes = this.encontrarComponentesFortementeConectados();
    const indicePorVertice = new Map();
    const nomeComponente = [];

    componentes.forEach((componente, indice) => {
      nomeComponente[indice] = componente.join("_");
      for (const vertice of componente) {
        indicePorVertice.set(vertice, indice);
      }
    });

    const reduzido = new ModeloGrafo({ orientado: true, nome: `${this.nome}_reduzido` });
    for (const nome of nomeComponente) {
      reduzido.adicionarVertice(nome);
    }

    for (const [origem, destinos] of this.adjacencias) {
      for (const [destino, peso] of destinos) {
        const iOrigem = indicePorVertice.get(origem);
        const iDestino = indicePorVertice.get(destino);
        if (iOrigem !== iDestino) {
          reduzido.adicionarAresta(nomeComponente[iOrigem], nomeComponente[iDestino], peso);
        }
      }
    }

    return reduzido;
  }
}

function criarElementoSvg(tag, atributos = {}) {
  const el = document.createElementNS("http://www.w3.org/2000/svg", tag);
  for (const [chave, valor] of Object.entries(atributos)) {
    el.setAttribute(chave, String(valor));
  }
  return el;
}

const historicoSaida = document.getElementById("historicoSaida");
const resumoGrafo = document.getElementById("resumoGrafo");
const painelGrafo = document.getElementById("painelGrafo");

let grafo = new ModeloGrafo({ orientado: true, nome: "grafo_web" });
let vizPromise = null;

function carregarViz() {
  if (!vizPromise) {
    vizPromise = import("https://cdn.jsdelivr.net/npm/@viz-js/viz@3.28.0/+esm").then((modulo) => modulo.instance());
  }

  return vizPromise;
}

function registrarMensagem(mensagem) {
  const horario = new Date().toLocaleTimeString();
  historicoSaida.textContent = `[${horario}] ${mensagem}\n${historicoSaida.textContent}`;
}

function atualizarResumoGrafo() {
  resumoGrafo.textContent = `${grafo.orientado ? "Orientado" : "Nao orientado"} | ${grafo.nome} | ordem ${grafo.ordem()}`;
}

function renderizarGrafoManual() {
  painelGrafo.innerHTML = "";

  const largura = painelGrafo.clientWidth || 700;
  const altura = painelGrafo.clientHeight || 420;
  const svg = criarElementoSvg("svg", {
    width: largura,
    height: altura,
    viewBox: `0 0 ${largura} ${altura}`,
    role: "img",
    "aria-label": "Visualizacao do grafo"
  });

  const defs = criarElementoSvg("defs");
  const marcadorSeta = criarElementoSvg("marker", {
    id: "seta",
    viewBox: "0 0 10 10",
    refX: "9",
    refY: "5",
    markerWidth: "7",
    markerHeight: "7",
    orient: "auto-start-reverse"
  });
  marcadorSeta.appendChild(criarElementoSvg("path", { d: "M 0 0 L 10 5 L 0 10 z", fill: "#3f3c35" }));
  defs.appendChild(marcadorSeta);
  svg.appendChild(defs);

  const vertices = grafo.listarVertices();
  const raio = Math.max(120, Math.min(largura, altura) * 0.33);
  const cx = largura / 2;
  const cy = altura / 2;
  const posicao = new Map();

  vertices.forEach((v, i) => {
    const angulo = (2 * Math.PI * i) / Math.max(vertices.length, 1) - Math.PI / 2;
    posicao.set(v, {
      x: cx + raio * Math.cos(angulo),
      y: cy + raio * Math.sin(angulo)
    });
  });

  const grupoArestas = criarElementoSvg("g", { "stroke-width": 2, stroke: "#3f3c35", fill: "none" });
  const grupoRotulosArestas = criarElementoSvg("g", { "font-size": 12, "font-family": "IBM Plex Mono, monospace", fill: "#1f1d1a" });

  const arestasRenderizadas = [];
  if (grafo.orientado) {
    for (const [origem, destinos] of grafo.adjacencias) {
      for (const [destino, peso] of destinos) {
        arestasRenderizadas.push({ origem, destino, peso });
      }
    }
  } else {
    const vistas = new Set();
    for (const [origem, destinos] of grafo.adjacencias) {
      for (const [destino, peso] of destinos) {
        const chave = [origem, destino].sort().join("--");
        if (vistas.has(chave)) {
          continue;
        }
        vistas.add(chave);
        arestasRenderizadas.push({ origem, destino, peso });
      }
    }
  }

  for (const aresta of arestasRenderizadas) {
    const a = posicao.get(aresta.origem);
    const b = posicao.get(aresta.destino);
    if (!a || !b) {
      continue;
    }

    const linha = criarElementoSvg("line", { x1: a.x, y1: a.y, x2: b.x, y2: b.y });
    if (grafo.orientado) {
      linha.setAttribute("marker-end", "url(#seta)");
    }
    grupoArestas.appendChild(linha);

    if (aresta.peso !== 0) {
      const mx = (a.x + b.x) / 2;
      const my = (a.y + b.y) / 2;
      const fundo = criarElementoSvg("rect", {
        x: mx - 16,
        y: my - 10,
        width: 32,
        height: 18,
        rx: 6,
        fill: "#fffaf2",
        stroke: "#d9cbb8"
      });
      const texto = criarElementoSvg("text", {
        x: mx,
        y: my + 3,
        "text-anchor": "middle"
      });
      texto.textContent = String(aresta.peso);
      grupoRotulosArestas.appendChild(fundo);
      grupoRotulosArestas.appendChild(texto);
    }
  }

  svg.appendChild(grupoArestas);
  svg.appendChild(grupoRotulosArestas);

  const grupoVertices = criarElementoSvg("g", { "font-family": "Space Grotesk, sans-serif", "font-size": 13 });
  for (const vertice of vertices) {
    const p = posicao.get(vertice);

    grupoVertices.appendChild(
      criarElementoSvg("circle", {
        cx: p.x,
        cy: p.y,
        r: 22,
        fill: "#fffdf6",
        stroke: "#b08968",
        "stroke-width": 2
      })
    );

    const texto = criarElementoSvg("text", {
      x: p.x,
      y: p.y + 4,
      "text-anchor": "middle",
      fill: "#1f1d1a"
    });
    texto.textContent = vertice;
    grupoVertices.appendChild(texto);
  }

  svg.appendChild(grupoVertices);
  painelGrafo.appendChild(svg);
  atualizarResumoGrafo();
}

async function renderizarGrafo() {
  const dot = grafo.gerarDot();

  try {
    const viz = await carregarViz();
    const svg = await viz.renderSVGElement(dot);
    painelGrafo.innerHTML = "";
    painelGrafo.appendChild(svg);

    const svgElement = painelGrafo.querySelector("svg");
    if (svgElement) {
      svgElement.style.width = "100%";
      svgElement.style.height = "auto";
      svgElement.style.maxWidth = "100%";
      svgElement.style.display = "block";
    }

    atualizarResumoGrafo();
  } catch (erro) {
    console.warn("Viz.js nao carregou, usando renderizacao manual.", erro);
    renderizarGrafoManual();
    registrarMensagem("Viz.js indisponivel no momento; visualizacao manual aplicada.");
  }
}

function obterValorCampo(id) {
  return document.getElementById(id).value.trim();
}

function obterNumeroCampo(id) {
  const n = Number.parseFloat(document.getElementById(id).value);
  return Number.isFinite(n) ? n : 0;
}

function solicitarVertice(texto) {
  const resposta = window.prompt(texto);
  return resposta ? resposta.trim() : "";
}

async function executarComCaptura(acao) {
  try {
    await acao();
  } catch (erro) {
    registrarMensagem(`Erro: ${erro.message}`);
  }
}

document.getElementById("botaoCriarGrafo").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const orientado = obterValorCampo("tipoGrafo") === "directed";
    const nome = obterValorCampo("nomeGrafo") || "grafo_web";
    grafo = new ModeloGrafo({ orientado, nome });
    renderizarGrafo();
    registrarMensagem(`Novo grafo criado: ${orientado ? "orientado" : "nao orientado"} (${nome}).`);
  });
});

document.getElementById("botaoCarregarDot").addEventListener("click", async () => {
  await executarComCaptura(async () => {
    const entrada = document.getElementById("arquivoDot");
    const arquivo = entrada.files?.[0];
    if (!arquivo) {
      throw new Error("Selecione um arquivo .dot primeiro.");
    }

    const conteudo = await arquivo.text();
    grafo = ModeloGrafo.criarDeDot(conteudo);
    document.getElementById("tipoGrafo").value = grafo.orientado ? "directed" : "undirected";
    document.getElementById("nomeGrafo").value = grafo.nome;
    renderizarGrafo();
    registrarMensagem(`Arquivo DOT carregado: ${arquivo.name}`);
  });
});

document.getElementById("botaoBaixarDot").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const conteudo = grafo.gerarDot();
    const blob = new Blob([conteudo], { type: "text/plain;charset=utf-8" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `${grafo.nome || "grafo"}.dot`;
    link.click();
    URL.revokeObjectURL(link.href);
    registrarMensagem("Arquivo DOT baixado.");
  });
});

document.getElementById("botaoAdicionarVertice").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const nome = obterValorCampo("nomeVertice");
    grafo.adicionarVertice(nome);
    renderizarGrafo();
    registrarMensagem(`Vertice ${nome} adicionado.`);
  });
});

document.getElementById("botaoRemoverVertice").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const nome = obterValorCampo("nomeVertice");
    grafo.removerVertice(nome);
    renderizarGrafo();
    registrarMensagem(`Vertice ${nome} removido.`);
  });
});

document.getElementById("botaoAdicionarAresta").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = obterValorCampo("origemAresta");
    const destino = obterValorCampo("destinoAresta");
    const peso = obterNumeroCampo("pesoAresta");
    grafo.adicionarAresta(origem, destino, peso);
    renderizarGrafo();
    registrarMensagem(`Aresta ${origem} -> ${destino} adicionada com peso ${peso}.`);
  });
});

document.getElementById("botaoRemoverAresta").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = obterValorCampo("origemAresta");
    const destino = obterValorCampo("destinoAresta");
    grafo.removerAresta(origem, destino);
    renderizarGrafo();
    registrarMensagem(`Aresta ${origem} -> ${destino} removida.`);
  });
});

document.getElementById("botaoOrdem").addEventListener("click", () => {
  registrarMensagem(`Ordem do grafo: ${grafo.ordem()}`);
});

document.getElementById("botaoGraus").addEventListener("click", () => {
  registrarMensagem(grafo.relatorioGraus() || "Sem vertices.");
});

document.getElementById("botaoExibirDot").addEventListener("click", () => {
  registrarMensagem(grafo.gerarDot());
});

document.getElementById("botaoMenorCaminho").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = solicitarVertice("Digite o vertice de origem para Dijkstra:");
    if (!origem) {
      return;
    }
    registrarMensagem(grafo.relatorioMenorCaminho(origem));
  });
});

document.getElementById("botaoBuscaLargura").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = solicitarVertice("Digite o vertice inicial para BFS:");
    if (!origem) {
      return;
    }
    registrarMensagem(`Ordem de visitacao (BFS): ${grafo.buscaEmLargura(origem)}`);
  });
});

document.getElementById("botaoBuscaProfundidade").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = solicitarVertice("Digite o vertice inicial para DFS:");
    if (!origem) {
      return;
    }
    registrarMensagem(`Ordem de visitacao (DFS): ${grafo.buscaEmProfundidade(origem)}`);
  });
});

document.getElementById("botaoPrim").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const origem = solicitarVertice("Digite o vertice inicial do Prim:");
    if (!origem) {
      return;
    }
    registrarMensagem(grafo.calcularArvoreGeradoraMinimaPrim(origem));
  });
});

document.getElementById("botaoKruskal").addEventListener("click", async () => {
  await executarComCaptura(() => {
    registrarMensagem(grafo.calcularArvoreGeradoraMinimaKruskal());
  });
});

document.getElementById("botaoComponentesConexos").addEventListener("click", async () => {
  await executarComCaptura(() => {
    registrarMensagem(grafo.relatorioComponentesFortementeConectados());
  });
});

document.getElementById("botaoGrafoReduzido").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const reduzido = grafo.gerarGrafoReduzido();
    registrarMensagem(`DOT do grafo reduzido:\n${reduzido.gerarDot()}`);
    grafo = reduzido;
    document.getElementById("tipoGrafo").value = "directed";
    document.getElementById("nomeGrafo").value = grafo.nome;
    renderizarGrafo();
  });
});

document.getElementById("botaoBaixarImagem").addEventListener("click", async () => {
  await executarComCaptura(() => {
    const svg = painelGrafo.querySelector("svg");
    if (!svg) {
      throw new Error("Nada para exportar ainda.");
    }

    const textoSvg = new XMLSerializer().serializeToString(svg);
    const blobSvg = new Blob([textoSvg], { type: "image/svg+xml;charset=utf-8" });
    const urlSvg = URL.createObjectURL(blobSvg);

    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement("canvas");
      const tamanho = svg.getBoundingClientRect();
      const largura = Math.ceil(svg.viewBox?.baseVal?.width || tamanho.width || areaGrafo.clientWidth || 1200);
      const altura = Math.ceil(svg.viewBox?.baseVal?.height || tamanho.height || areaGrafo.clientHeight || 800);
      canvas.width = largura;
      canvas.height = altura;
      const ctx = canvas.getContext("2d");
      ctx.fillStyle = "#fffaf2";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(img, 0, 0);

      const link = document.createElement("a");
      link.download = `${grafo.nome || "grafo"}.png`;
      link.href = canvas.toDataURL("image/png");
      link.click();
      URL.revokeObjectURL(urlSvg);
      registrarMensagem("PNG baixado.");
    };

    img.onerror = () => {
      URL.revokeObjectURL(urlSvg);
      registrarMensagem("Erro ao gerar PNG.");
    };

    img.src = urlSvg;
  });
});

document.getElementById("botaoLimparHistorico").addEventListener("click", () => {
  historicoSaida.textContent = "";
});

renderizarGrafo();
registrarMensagem("Aplicacao carregada. Crie um grafo ou abra um DOT.");
