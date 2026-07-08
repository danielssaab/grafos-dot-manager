# Grafos .dot Manager

Projeto academico em Java para manipulacao de grafos orientados e nao orientados, com leitura e escrita de arquivos DOT.

A versao web para GitHub Pages fica em `docs/` e foi criada como espelho didatico do projeto original em Java.

## Objetivo

Organizar uma aplicacao de grafos com interface grafica em Swing, permitindo:

- criar grafos orientados e nao orientados;
- adicionar e remover vertices;
- adicionar e remover arestas com peso;
- calcular ordem e graus;
- carregar e salvar arquivos DOT;
- calcular menor caminho;
- executar busca em largura e em profundidade;
- calcular arvore geradora minima por Prim e Kruskal;
- identificar componentes fortemente conectados;
- gerar grafo reduzido em grafos orientados.

## Estrutura do projeto

- `src/main/java/` - codigo-fonte principal em Java;
- `grafao.dot` e outros arquivos `.dot` - exemplos de entrada e saida;
- `docs/` - versao web para GitHub Pages;
- `pom.xml` - configuracao Maven.

## Tecnologias

- Java 19;
- Swing para interface grafica;
- Maven para organizacao e compilacao;
- DOT para representacao dos grafos.

## Como executar o Java

### Requisitos

- JDK 19 ou superior;
- Maven instalado.

### Compilar

```powershell
mvn clean compile
```

### Executar

Se o projeto estiver aberto em uma IDE, rode a classe principal da interface grafica.

A classe principal da interface fica em `src/main/java/MainFrame.java`.

## Como usar

1. Abra a aplicacao.
2. Escolha se o grafo sera orientado ou nao orientado.
3. Adicione vertices e arestas.
4. Carregue ou salve arquivos DOT quando necessario.
5. Execute os algoritmos pelos botoes da interface.

## Versao web

A pasta `docs/` contem uma versao estatica em HTML, CSS e JS, pronta para ser publicada no GitHub Pages.

## Observacao academica

Este repositorio foi organizado para fins de estudo e apresentacao academica. A interface Java foi mantida como base principal do trabalho, e a versao web serve como demonstracao complementar para publicacao online.
