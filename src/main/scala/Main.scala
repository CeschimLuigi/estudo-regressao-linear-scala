// Descrição do projeto: Simulação simplificada de análise de dados (Regressão Linear)
//> using scala "3.3.0"

case class Dado(renda: Double, doacao: Double)
val dataset = List(Dado(42.00, 9.00), Dado(48.00, 10.00), Dado(50.00, 8.00), Dado(59.00, 5.00), Dado(65.00, 6.00), Dado(72.00, 3.00))


@main def simularCienciaDados(): Unit = {
  println("--- 📊 PROJETO DATA SCIENCE HIPER SIMPLES (SCALA 3) ---")

  val mse = dataset.map {dado =>
    val previsaoY = previsao(dado.renda)
    val erro = dado.doacao - previsaoY
    Math.pow(erro,2)





  }.sum / dataset.length
  val performance = Math.sqrt(mse)

  println("ERRO QUADRADO MÉDIO " + mse)
  println("PONTO DE VARIAÇÃO DE ACORDO COM A PREVISÃO " + performance)



}

def previsao(x:Double): Double = {
  val n = dataset.length
  val somaX = dataset.map(dado => dado.renda).sum
  val somaY = dataset.map(dado => dado.doacao).sum
  val somaXY = dataset.map(dado => dado.renda * dado.doacao).sum
  val somaX2 = dataset.map(dado => Math.pow(dado.renda, 2)).sum

  val a = ((n * somaXY) - (somaX * somaY)) / ((n * somaX2) - Math.pow(somaX, 2))

  val b = (somaY - (a * somaX)) / n

  (a * x) + b



}



