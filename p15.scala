
import breeze.linalg._
import breeze.plot._
import com.github.tototoshi.csv._
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object StockLinePlot {

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
    val availableColumns = data.head.keySet

    if (!requiredColumns.subsetOf(availableColumns)) {
      println("Error: CSV must contain Date and Close columns.")
      println("Available columns: " + availableColumns.mkString(", "))
      return
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val parsedData = data.flatMap { row =>
      for {
        date <- Try(LocalDate.parse(row("Date").trim, dateFormatter)).toOption
        close <- Try(row("Close").trim.toDouble).toOption
        if close.isFinite
      } yield (date, close)
    }.sortBy(_._1)

    if (parsedData.isEmpty) {
      println("Error: No valid Date and Close values found.")
      println("Check the date format and closing prices in the CSV.")
      return
    }

    val x = DenseVector(
      (0 until parsedData.length).map(_.toDouble).toArray
    )

    val y = DenseVector(
      parsedData.map(_._2).toArray
    )

    val fig = Figure("AUBANK.NS - Close Price Trend")
    val plt = fig.subplot(0)

    plt += plot(x, y, name = "Close Price", colorcode = "blue")

    plt.xlabel = "Time (Days)"
    plt.ylabel = "Close Price"
    plt.title = "AUBANK.NS Closing Price Over Time"

    fig.refresh()

    println("Stock line plot displayed successfully.")
    println("Valid records: " + parsedData.length)
    println("First closing price: " + parsedData.head._2)
    println("Latest closing price: " + parsedData.last._2)
  }
}
