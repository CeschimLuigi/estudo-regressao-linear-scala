// Descrição do projeto: Simulação simplificada de análise de dados (Regressão Linear)
//> using scala "3.3.0"

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{avg, pow}

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

    spark.stop()

  }
}


