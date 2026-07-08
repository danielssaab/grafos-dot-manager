# Grafo Lab Web (GitHub Pages)

Esta pasta contem a versao web do projeto de grafos, sem backend, pronta para publicar no GitHub Pages.

## Nota didatica

- A nomenclatura do codigo em JavaScript foi mantida em portugues para ficar alinhada com o projeto original em Java.
- Os nomes de operacoes seguem a mesma ideia do trabalho original: adicionar/remover vertice, adicionar/remover aresta, busca em largura, busca em profundidade, menor caminho, MST, f-conexos e grafo reduzido.
- A pagina e 100% estatica (HTML, CSS e JS) e nao depende de backend para executar os algoritmos.
- A renderizacao do grafo usa o Viz.js via CDN para ficar mais parecida com a saida do Java.
- DOTs sem nome explicito, como `graph { ... }`, tambem sao aceitos e recebem nome padrao automaticamente.
- Os nomes da interface e das funcoes foram organizados em portugues para facilitar a leitura academica do repositorio.

## O que foi portado do Java

- Criar grafo orientado ou nao orientado
- Adicionar/remover vertices
- Adicionar/remover arestas com peso
- Ordem do grafo
- Graus dos vertices
- Exibir e baixar DOT
- Carregar DOT local
- Dijkstra (menor caminho)
- BFS e DFS
- MST Prim (nao orientado)
- MST Kruskal (nao orientado)
- Componentes fortemente conectados (orientado)
- Grafo reduzido (orientado)
- Exportar visualizacao para PNG

## Publicar no GitHub Pages

1. Suba o repositorio para o GitHub.
2. Abra Settings > Pages.
3. Em Build and deployment:
   - Source: Deploy from a branch
   - Branch: main
   - Folder: /docs
4. Salve.
5. Aguarde o link publico ser gerado pelo GitHub.

## Desenvolvimento local

Opcao 1: abrir `docs/index.html` direto no navegador.

Opcao 2 (recomendado): servir com um servidor estatico local:

```powershell
cd docs
python -m http.server 5500
```

Depois abrir `http://localhost:5500`.
