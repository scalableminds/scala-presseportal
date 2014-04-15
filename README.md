scala-presseportal
==================

A simple scala wrapper for accessing press articles published via http://presseportal.de

#Installation

tbd

#Usage

```scala
import com.scalableminds.presseportal._
import scala.concurrent._
import scala.util.{Success, Failure}

object ExampleUsage extends App {
  import ExecutionContext.Implicits.global // execution context for futures

  val APIKey = "Your API key"
  val pressePortal = new PressePortal(APIKey)

  //Printing Titles of the 20 newest articles
  pressePortal.find().andThen{
    case Success(result) => result match {
      case APISuccess(success, request, articles) =>
        articles.foreach{a => println(s"[*] ${a.title}")}
      case APIFailure(success, error) =>
        println(s"Error(${error.code}): ${error.msg}")
    }
    case Failure(e) =>
      println(e.getMessage)
  }.andThen{
    case _ => pressePortal.shutDown()
  }
}
```


