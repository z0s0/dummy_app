package ru.otus.sc.author.route

import java.util.UUID

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import org.scalatest.concurrent.ScalaFutures
import ru.otus.sc.auth.AuthService
import ru.otus.sc.author.model.{
  Author,
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  GetAuthorRequest,
  GetAuthorResponse,
  ListAuthorsResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}
import ru.otus.sc.author.json.AuthorJsonProtocol._
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.support.Generators._

import scala.concurrent.Future

class AuthorRouterTest
    extends AnyFreeSpec
    with ScalatestRouteTest
    with MockFactory
    with ScalaCheckDrivenPropertyChecks
    with ScalaFutures {

  val srv: AuthorService = mock[AuthorService]

  val authSrv: AuthService = mock[AuthService]
  val router: AuthorRouter = new AuthorRouter(srv, authSrv)

  "GET /authors" in {
    val authors = Vector(genAuthor.sample.get, genAuthor.sample.get)
    val request = Get("/authors")

    (srv.listAuthors _).expects().returns(Future.successful(ListAuthorsResponse(authors)))

    request ~> router.route ~> check {
      handled shouldBe true
      status shouldBe StatusCodes.OK
    }
  }

  "GET /authors/:uuid" - {
    "when unknown author" in {
      val randUUID = UUID.randomUUID()
      val request  = Get(s"/authors/${randUUID}")

      (srv.getAuthor _)
        .expects(GetAuthorRequest(randUUID))
        .returns(Future.successful(GetAuthorResponse.NotFound))

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
      }
    }

    "when known author" in {
      forAll { author: Author =>
        val request = Get(s"/authors/${author.id.get}")

        (srv.getAuthor _)
          .expects(GetAuthorRequest(author.id.get))
          .returns(Future.successful(GetAuthorResponse.Found(author)))

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.OK
        }
      }
    }
  }

  "POST /authors" in {
    forAll { author: Author =>
      val authorEntity = Marshal(author).to[MessageEntity].futureValue
      val request = Post("/authors")
        .withEntity(authorEntity)
        .withHeaders(List(RawHeader("authorization", "serega serega")))

      (authSrv.is_authenticated _).expects("serega", "serega").returns(Future.successful(true))

      (srv.createAuthor _)
        .expects(CreateAuthorRequest(author))
        .returns(Future.successful(CreateAuthorResponse.Created(author)))

      request ~> router.route ~> check {
        status shouldBe StatusCodes.Created
      }
    }
  }

  "PUT /authors/:uuid" - {
    "when known author" in {
      forAll { author: Author =>
        val authorEntity = Marshal(author).to[MessageEntity].futureValue
        val request      = Put(s"/authors/${author.id.get}").withEntity(authorEntity)

        (srv.updateAuthor _) expects UpdateAuthorRequest(author) returns Future.successful(
          UpdateAuthorResponse.Updated(author)
        )

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.OK
        }
      }
    }

    "when unknown author" in {
      val author = genAuthor.sample.get

      (srv.updateAuthor _)
        .expects(UpdateAuthorRequest(author))
        .returns(Future.successful(UpdateAuthorResponse.Invalid))

      val authorEntity = Marshal(author).to[MessageEntity].futureValue
      val request      = Put(s"/authors/${author.id.get}").withEntity(authorEntity)

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.UnprocessableEntity
      }
    }
  }

  "DELETE /authors/:uuid" - {
    "when known author" in {
      forAll { author: Author =>
        val request = Delete(s"/authors/${author.id.get}")

        (srv.deleteAuthor _)
          .expects(DeleteAuthorRequest(author.id.get))
          .returns(Future.successful(DeleteAuthorResponse.Deleted(author)))

        request ~> router.route ~> check {
          handled shouldBe true
          status shouldBe StatusCodes.Found
        }
      }
    }

    "when unknown author" in {
      val randUUID = UUID.randomUUID()
      val request  = Delete(s"/authors/${randUUID}")

      (srv.deleteAuthor _)
        .expects(DeleteAuthorRequest(randUUID))
        .returns(Future.successful(DeleteAuthorResponse.NotFound))

      request ~> router.route ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
