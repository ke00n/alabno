package complexity_analyser

import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.concurrent.{Callable, Executors, TimeUnit}

import json_parser.Error

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class JavaProcessor(modelAnswer: File, studentAnswer: File) {
  private final lazy val beforePattern = """NO (\d+) BEFORE (\w+) => (\d+)""".r
  private final lazy val afterPattern = """NO (\d+) AFTER (\w+) => (\d+)""".r
  private final lazy val jarFile = new File("complexity_analyser/res/javassist.jar")
  // Used to run the compilations and the benchmarks
  private final lazy val eS = Executors.newFixedThreadPool(2)

  def prepare(): Unit = {
    val benchFile = new File("complexity_analyser/res/Benchmarker.java")
    val modPath = new File(modelAnswer.toString + "/" + benchFile.getName).toPath
    val studPath = new File(studentAnswer.toString + "/" + benchFile.getName).toPath
    println(benchFile, modPath)
    copy(benchFile.toPath, modPath, REPLACE_EXISTING)
    copy(benchFile.toPath, studPath, REPLACE_EXISTING)
    compile()
  }

  private def compile() = {
    val modJavaFiles =
      modelAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    val studJavaFiles =
      studentAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    eS.submit(new ShellExecutor(s"javac -cp $jarFile $studJavaFiles"))
    eS.submit(new ShellExecutor(s"javac -cp $jarFile $modJavaFiles"))
    eS.awaitTermination(1, TimeUnit.SECONDS)
  }

  private def findTestSuite() = {
    val names = modelAnswer.listFiles().map(_.getName)
    names.find(_.endsWith("TestSuite.java")).get.replace(".java", "")
  }

  private def findMatch(lines: String) = {
    val map = new mutable.HashMap[String, ArrayBuffer[Long]]
    val split = lines.split("\n")
    val list = new ArrayBuffer[Long]
    for (line <- split) {
      line match {
        case beforePattern(index, name, time) =>
          list.insert(index.toInt, time.toLong)
        case afterPattern(index, name, time) =>
          val diff = time.toLong - list.apply(index.toInt)
          if(!map.contains(name)) {
            map(name) = new ArrayBuffer[Long]
          }
          map(name) += diff
        case _ => None
      }
    }
    map
  }

  private class MeanMaker(input: String) extends Callable[Map[String, Long]] {
    override def call() = {
      val m = findMatch(input)
      genMean(m).toMap
    }
  }

  private def genMean(diffMap: mutable.HashMap[String, ArrayBuffer[Long]]) = diffMap.mapValues(v => v.sum / v.length)

  def calculateTestScores(modMean: Map[String, Long], studMean: Map[String, Long]) = {
    var score = 100.0d
    val annotations = new ArrayBuffer[Error]
    var eff = ""
    for (fun <- modMean) {
      // Difference in microseconds
      val diff = (fun._2 - studMean(fun._1)) / 1000
      if (Math.abs(diff) > 250){
        score -= (diff / 100).toInt
        if (diff > 0) {
          eff = s"Function used in test ${fun._1} is " +
            s"inefficient -> $diff ms diff!"
        } else {
          eff = s"Function used in test ${fun._1} is " +
            s"more efficient than model solution -> $diff ms diff!"
        }
        annotations.append(new Error(eff, studentAnswer.getName, 0, 0, "complexity"))
      }
    }
    (annotations, Math.min(Math.max(score, 0), 100))
  }

  def benchmark() = {
    val testClass = findTestSuite()
    val modThread = eS.submit(new ShellExecutor(s"java -cp $modelAnswer:$jarFile Benchmarker $testClass"))
    val studThread = eS.submit(new ShellExecutor(s"java -cp $studentAnswer:$jarFile Benchmarker $testClass"))
    val modMeanT = eS.submit(new MeanMaker(modThread.get))
    val studMeanT = eS.submit(new MeanMaker(studThread.get))
    val modMean = modMeanT.get
    val studMean = studMeanT.get
    calculateTestScores(modMean, studMean)
  }
}
