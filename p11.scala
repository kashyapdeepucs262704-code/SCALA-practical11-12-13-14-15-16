
import scala.io.Source
import java.io.File

object WordFrequencyCounter {
  def main(args: Array[String]): Unit = {

    val filename = "mvlu.txt"
    val file = new File(filename)

    if (!file.exists()) {
      println("Error: mvlu.txt file not found!")
      println("Please place mvlu.txt in your project folder.")
      return
    }

    val source = Source.fromFile(file, "UTF-8")

    val lines = try {
      source.getLines().toList
    } finally {
      source.close()
    }

    val words = lines
      .flatMap(_.toLowerCase.split("\\W+"))
      .filter(_.nonEmpty)

    val wordCounts = words
      .groupBy(identity)
      .map { case (word, list) => word -> list.size }

    println("Total Words: " + words.size)
    println("\nWord Frequencies:")

    wordCounts.toSeq
      .sortBy { case (word, count) => (-count, word) }
      .foreach { case (word, count) =>
        println(f"$word%-15s -> $count")
      }
  }
}
