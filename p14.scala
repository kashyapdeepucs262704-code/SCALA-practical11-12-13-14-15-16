
import breeze.linalg._
import breeze.plot._
import com.github.tototoshi.csv._
import java.io.File
import scala.util.Try

object IrisHistogram {
  def main(args: Array[String]): Unit = {

    val inputFile = new File("iris.csv")

    if (!inputFile.exists()) {
      println("Error: iris.csv file not found!")
      println("Place iris.csv in your project working directory.")
      return
    }

    val reader = CSVReader.open(inputFile)

    val data = try {
      reader.allWithHeaders()
    } finally {
      reader.close()
    }

    if (data.isEmpty) {
      println("Error: iris.csv contains no data rows.")
      return
    }

    if (!data.head.contains("sepal_length")) {
      println("Error: Column 'sepal_length' not found.")
      println("Available columns: " + data.head.keys.mkString(", "))
      return
    }

    val sepalLengths = DenseVector(
      data.flatMap { row =>
        Try(row("sepal_length").trim.toDouble).toOption
      }.toArray
    )

    if (sepalLengths.length == 0) {
      println("Error: No valid sepal_length values found.")
      return
    }

    val fig = Figure("Histogram of Sepal Length")
    val binSizes = List(5, 10, 20)

    for ((bins, idx) <- binSizes.zipWithIndex) {
      val plt = fig.subplot(1, binSizes.length, idx)
      plt += hist(sepalLengths, bins)
      plt.title = s"Histogram with $bins bins"
      plt.xlabel = "Sepal Length"
      plt.ylabel = "Frequency"
    }

    fig.refresh()

    println("Histogram displayed successfully.")
    println("Number of records: " + sepalLengths.length)
  }
}
