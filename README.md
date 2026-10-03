# Estudo de Regressão Linear em Scala

Projeto de estudo que implementa um modelo simples de **Regressão Linear Simples** (mínimos quadrados) em Scala, prevendo o valor de doação (`doacao`) de uma pessoa a partir da sua renda (`renda`).

Existem duas branches com a **mesma ideia e o mesmo dataset**, mas implementadas de formas diferentes, propositalmente, para comparar o custo/benefício de usar uma ferramenta de processamento distribuído (Apache Spark) versus Scala puro:

| Branch | Abordagem |
|---|---|
| [`main`](../../tree/main) | Scala 3 puro, sem nenhuma dependência externa |
| [`spark`](../../tree/spark) | Apache Spark (Spark SQL / DataFrames) |

## O que o modelo faz

O dataset é uma lista de pessoas com `renda` (renda mensal) e `doacao` (quanto doaram):

```
(42, 9), (48, 10), (50, 8), (59, 5), (65, 6), (72, 3)
```

A partir desses pontos, o modelo ajusta uma reta `y = ax + b` pelo método dos **mínimos quadrados**, usando as somatórias clássicas:

```
a = (n·ΣXY − ΣX·ΣY) / (n·ΣX² − (ΣX)²)
b = (ΣY − a·ΣX) / n
```

Com `a` e `b` calculados, o modelo é usado para prever a doação de novos clientes a partir apenas da renda deles.

É uma regressão **ingênua** (sem bibliotecas de ML, sem regularização, sem validação cruzada) — o objetivo não é ter um modelo robusto, e sim entender, na mão, a matemática por trás de uma regressão linear e como estruturar esse cálculo em Scala.

## Por que Scala?

- É a linguagem nativa do **Apache Spark** (Spark é escrito em Scala) — aprender Scala aqui serve como base direta para trabalhar com Spark depois.
- Roda na JVM, o que facilita a convivência com outros projetos Java do autor.
- Combina bem paradigma funcional (`map`, `sum`, expressões imutáveis) com orientação a objetos, que é justamente o estilo usado nos dois branches para expressar as fórmulas de forma declarativa.

## Branch `main` — sem Spark (Scala puro)

- Scala 3.9.0, sem dependências de terceiros (`build.sbt` só define nome e versão).
- Os dados ficam em uma `List[Dado]` comum, e as somatórias são feitas com `map` + `sum` do próprio Scala.
- Além de treinar o modelo, essa branch calcula o **erro quadrático médio (MSE)** e sua raiz (**RMSE**) para medir o quão longe as previsões ficam dos valores reais — uma etapa de avaliação do modelo que a branch `spark` não tem.
- Executa instantaneamente, sem subir nenhum motor de processamento — ideal para esse volume de dados (6 linhas).

## Branch `spark` — com Apache Spark

- Scala **2.12.18** (não 2.13/3, por compatibilidade de binário com o Spark) e dependência de `spark-sql` 3.5.9.
- Os dados viram um `DataFrame` (`dataset.toDF()`), e as somatórias são feitas via `agg`, `sum`, `pow` do Spark SQL em vez de `map`/`sum` do Scala puro.
- Mesma fórmula matemática de regressão, mas calculada através do motor de execução distribuído do Spark (mesmo rodando local, em `local[*]`).
- Adiciona previsão para novos clientes como coluna do DataFrame e aplica uma regra de negócio (`when/otherwise`) para não deixar a doação prevista ser negativa.
- Precisa de configuração extra no `build.sbt` (`fork := true` e `--add-opens` da JVM) porque o Java 17+ trava, por padrão, o acesso reflexivo que o Spark usa internamente.

## Comparação

| Aspecto | `main` (sem Spark) | `spark` (com Spark) |
|---|---|---|
| Dependências | Nenhuma | Spark SQL (~várias dezenas de MB) |
| Tempo de inicialização | Imediato | Alguns segundos (sobe uma `SparkSession`) |
| Estrutura de dados | `List` em memória | `DataFrame` distribuído |
| Escala para datasets grandes | Não — processamento é sequencial em memória | Sim — mesmo código escala para milhões de linhas em um cluster |
| Verbosidade | Mais enxuto | Mais boilerplate (sessão, imports, configuração de JVM) |
| Avaliação do modelo | Calcula MSE/RMSE | Não calcula (foco é a previsão) |

Na prática, para 6 linhas de dados, o Spark é um exagero — o ganho dele só aparece quando o volume de dados não cabe mais confortavelmente em memória em uma única máquina. O propósito do exercício é justamente sentir esse contraste: a mesma regra de negócio implementada "na unha" versus implementada sobre uma ferramenta pensada para Big Data.

## Como rodar

Branch sem Spark:

```bash
git checkout main
sbt run
```

Branch com Spark:

```bash
git checkout spark
sbt run
```

Requer [sbt](https://www.scala-sbt.org/) instalado e JDK 17+ (a branch `spark` já lida com as flags de módulo necessárias no próprio `build.sbt`).
