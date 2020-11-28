package ru.otus.sc.author.json

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers._

import ru.otus.sc.support.Generators.arbitraryAuthor
import play.api.libs.json.{JsSuccess, Json}
import ru.otus.sc.author.model.Author
import ru.otus.sc.author.json.AuthorJsonProtocol._

class AuthorJsonProtocolTest extends AnyFreeSpec with ScalaCheckDrivenPropertyChecks {
  "Author format" in {
    forAll { author: Author =>
      Json.fromJson[Author](Json.toJson(author)) shouldBe JsSuccess(author)
    }
  }
}
