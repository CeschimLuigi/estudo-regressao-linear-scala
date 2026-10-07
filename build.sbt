ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "aula-teste",
    scalaVersion := "2.12.18", // Versão do Scala que combina com o Spark
      // Pede ao sbt para baixar o Spark SQL e o Core
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % "3.5.9",
      "org.apache.spark" %% "spark-mllib" % "3.5.9"
      ),


      // --- CONFIGURAÇÕES PARA O JAVA 17+ ---
      // 1. Avisa o sbt para rodar o projeto em uma nova janela de memória
    fork := true,
      // 2. Destranca as portas de segurança do Java para o Spark passar
    javaOptions ++= Seq(
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
  )
