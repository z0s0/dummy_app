package ru.otus.sc.book.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.otus.sc.book.model.Book
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.support.Generators.arbitraryBook
import ru.otus.sc.book.json.BookJsonProtocol._

class BookJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Book format" in {
    forAll { book: Book =>
      Json.fromJson[Book](Json.toJson(book)) shouldBe JsSuccess(book)
    }
  }
}
