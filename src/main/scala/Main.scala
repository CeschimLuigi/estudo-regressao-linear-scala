// Descrição do projeto: Simulação simplificada de análise de dados (Regressão Linear)
//> using scala "3.3.0"

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{avg, pow, sum, when}

object Main {
  case class Dado(renda: Double, doacao: Double)


  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("ONG_Machinelearning")
      .master("local[*]") // usar todos os núcleos do processador
      .getOrCreate()

    import spark.implicits._

    val dataset = List(
      Dado(42.00, 9.00), Dado(48.00, 10.00),
      Dado(50.00, 8.00), Dado(59.00, 5.00),
      Dado(65.00, 6.00), Dado(72.00, 3.00))

    val dfTreino = dataset.toDF()

    // --- MACHINE LEARNING COMEÇA AQUI ---

    val preparadorVector = new VectorAssembler()
      .setInputCols(Array("renda"))
      .setOutputCol("features")

    val dfTreinoPronto = preparadorVector.transform(dfTreino)

    dfTreinoPronto.show()

    val algoritmo = new LinearRegression()
      .setFeaturesCol("features")
      .setLabelCol("doacao")
      .setPredictionCol("previsao_doacao")

    val modeloTreinado = algoritmo.fit(dfTreinoPronto)

    println(f"Equação gerada pela IA: y = ${modeloTreinado.coefficients(0)}%.3fx + ${modeloTreinado.intercept}%.2f\n")












    spark.stop()

  }
}


