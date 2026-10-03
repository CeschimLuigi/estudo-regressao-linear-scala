// Descrição do projeto: Simulação simplificada de análise de dados (Regressão Linear)
//> using scala "3.3.0"

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{avg, pow, sum, when}

object Main {
  case class Dado(renda: Double, doacao: Double)


  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("MeuPrimeiroSpark")
      .master("local[*]") // usar todos os núcleos do processador
      .getOrCreate()

    import spark.implicits._

    val dataset = List(
      Dado(42.00, 9.00), Dado(48.00, 10.00),
      Dado(50.00, 8.00), Dado(59.00, 5.00),
      Dado(65.00, 6.00), Dado(72.00, 3.00))

    val dfDoacoes = dataset.toDF()

    println("--- 📊 Carregando dados no spark ---")

    dfDoacoes.show()

    dfDoacoes.filter($"renda" > 50).show()

    println("--- CRIANDO COLUNA COM RENDA AO QUADRADO ---")
    val dfComQuadrado = dfDoacoes.withColumn("renda_quadrado", pow($"renda", 2))
    dfComQuadrado.show()

    dfDoacoes.agg(avg($"doacao")).show()


    val n = dfDoacoes.count()

    val agregacoes = dfDoacoes.agg(
      sum($"renda").alias("somaX"),
      sum($"doacao").alias("somaY"),
      sum($"renda" * $"doacao").alias("somaXY"),
      sum(pow($"renda",2)).alias("somaX2")
    )

    val resultado  = agregacoes.first()

    val somaX = resultado.getAs[Double]("somaX")
    val somaY = resultado.getAs[Double]("somaY")
    val somaXY = resultado.getAs[Double]("somaXY")
    val somaX2 = resultado.getAs[Double]("somaX2")

    // 4. A partir daqui, a matemática é idêntica ao que você já fazia!
    val a = ((n * somaXY) - (somaX * somaY)) / ((n * somaX2) - Math.pow(somaX, 2))
    val b = (somaY - (a * somaX)) / n

    println(f"Equação da Reta no Spark: y = ${a}%.3fx + ${b}%.2f")

    val x = 25

    println( (a*x) + b)

    val dfComPrevisao = dfDoacoes.withColumn("previsao", ($"renda" * a) + b)

    println("--- 📊 TABELA COM AS PREVISÕES DO MODELO ---")
    dfComPrevisao.show()

    val novosClientes = List(80.0, 25.0,120.0,45.0)

    val dfNovosClientes = novosClientes.toDF("renda")

    val dfComPredicoes = dfNovosClientes.withColumn(
      "previsao_doacao_novos_c", ($"renda" * a) + b)

    println("--- PREVISÃO DE COMPORTAMENTO GERADA ---")
    dfComPredicoes.show()


    val dfFinal = dfComPredicoes.withColumn(
      "doacao_realista",
      when($"previsao_doacao_novos_c" < 0, 0.0).otherwise($"previsao_doacao_novos_c")
    )

    println("--- TABELA FINAL PRONTA PARA O NEGÓCIO ---")
    dfFinal.show()


    spark.stop()

  }
}


