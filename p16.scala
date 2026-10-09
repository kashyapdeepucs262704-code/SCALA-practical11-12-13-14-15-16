
import breeze.linalg._
import breeze.plot._
import com.github.tototoshi.csv._
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object CombinedPlot {
  def main(args: Array[String]): Unit = {

    val inputFile = new File("AUBANK.NS.csv")

    if (!inputFile.exists()) {
      println("Error: AUBANK.NS.csv file not found!")
      println("Place the CSV file in your project working directory.")
      return
    }

    val reader = CSVReader.open(inputFile)

    val data = try {
      reader.allWithHeaders()
    } finally {
      reader.close()
    }

    if (data.isEmpty) {
      println("Error: CSV file contains no data.")
      return
    }

    val requiredColumns = Set("Date", "Close")

    if (!requiredColumns.subsetOf(data.head.keySet)) {
      println("Error: CSV must contain Date and Close columns.")
      println("Available columns: " + data.head.keys.mkString(", "))
      return
    }

    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val parsedData = data.flatMap { row =>
      for {
        date <- Try(
          LocalDate.parse(row("Date").trim, dateFormat)
        ).toOption
        close <- Try(row("Close").trim.toDouble).toOption
        if close.isFinite
      } yield (date, close)
    }.sortBy(_._1)

    if (parsedData.isEmpty) {
      println("Error: No valid Date and Close values found.")
      return
    }

    val x = DenseVector(
      (0 until parsedData.length).map(_.toDouble).toArray
    )

    val y = DenseVector(
      parsedData.map(_._2).toArray
    )

    val fig = Figure("AUBANK.NS - Line + Scatter Plot")
    val plt = fig.subplot(0)

    plt += plot(
      x, y,
      name = "Close Price Line",
      colorcode = "blue"
    )

    plt += plot(
      x, y,
      '.',
      name = "Close Price Points",
      colorcode = "red"
    )

    plt.xlabel = "Time (Days)"
    plt.ylabel = "Close Price"
    plt.title = "AUBANK.NS Close Price - Line + Scatter"

    fig.refresh()

    println("Combined line and scatter plot displayed successfully.")
    println("Total valid records: " + parsedData.length)
  }
}
