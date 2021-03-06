package mark_marker

import java.io.File

import scala.io.Source

/**
  * Created by eb1314 on 27/11/16.
  */
object Utils {
  private final val regex = """\w*Bench|Test\w+""".r

  def matchType(t: String): String = t match {
    case s if s.contains("haskell") => ".hs"
    case s if s.contains("java") => ".java"
    case s => throw new IllegalArgumentException(s"Invalid language: $s")
  }

  def getFiles(file: File, extension: String): Array[File] = {
    file.listFiles().filter(_.getName.endsWith(extension)).filterNot(skipBenchAndTests)
  }

  private def skipBenchAndTests(f: File) = regex.findFirstIn(f.getName).isDefined

  def stringifyFile(files: Array[File]): String = {
    files.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
  }

}
