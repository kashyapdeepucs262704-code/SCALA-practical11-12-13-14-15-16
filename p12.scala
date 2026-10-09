
import com.github.tototoshi.csv._
import java.io.File

object OneHotEncoding {
  def main(args: Array[String]): Unit = {

    val inputFile = new File("iris.csv")

    if (!inputFile.exists()) {
      println("Error: iris.csv file not found!")
      println("Please place iris.csv in your project working directory.")
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

    if (!data.head.contains("species")) {
      println("Error: CSV must contain a column named species.")
      println("Available columns: " + data.head.keys.mkString(", "))
      return
    }

    val categories = data
      .map(row => row("species").trim)
      .distinct
      .sorted

    val newData = data.map { row =>
      val species = row("species").trim

      val otherColumns = row - "species"

      val oneHot = categories.map { category =>
        category -> (if (category == species) "1" else "0")
      }.toMap

      otherColumns ++ oneHot
    }

    val headers = data.head.keys.toList.filter(_ != "species") ++ categories

    println("One-Hot Encoded Data:")
    println(headers.mkString(","))

    newData.foreach { row =>
      println(headers.map(h => row.getOrElse(h, "")).mkString(","))
    }

    val outputFile = new File("iris_encoded.csv")
    val writer = CSVWriter.open(outputFile)

    try {
      writer.writeRow(headers)
      newData.foreach { row =>
        writer.writeRow(headers.map(h => row.getOrElse(h, "")))
      }
    } finally {
      writer.close()
    }

    println("\nOne-hot encoded file written to iris_encoded.csv")
  }
}
