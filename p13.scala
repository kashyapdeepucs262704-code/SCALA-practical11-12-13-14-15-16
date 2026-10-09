
import com.github.tototoshi.csv._
import java.io.File

object CSVTest {
  def main(args: Array[String]): Unit = {
    val file = new File("test.csv")

    if (file.exists()) {
      val reader = CSVReader.open(file)
      try {
        val rows = reader.all()
        println("CSV Data:")
        rows.foreach(row => println(row.mkString(", ")))
      } finally {
        reader.close()
      }
    } else {
      println("Error: test.csv file not found!")
    }
  }
}
