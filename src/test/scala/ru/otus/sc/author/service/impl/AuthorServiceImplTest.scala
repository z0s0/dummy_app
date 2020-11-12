package ru.otus.sc.author.service.impl

import java.util.UUID
import java.util.concurrent.ForkJoinPool

import org.scalatest.matchers.should.Matchers._
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model.{
  Author,
  CreateAuthorRequest,
  CreateAuthorResponse,
  DeleteAuthorRequest,
  DeleteAuthorResponse,
  Genre,
  GetAuthorRequest,
  GetAuthorResponse,
  ListAuthorsResponse,
  UpdateAuthorRequest,
  UpdateAuthorResponse
}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class AuthorServiceImplTest extends AnyFreeSpec with MockFactory with ScalaFutures {
  implicit val ThreadPool: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  val author1: Author = Author(
    id = Some(UUID.randomUUID()),
    name = "Serega",
    genres = Set(Genre.NoGenre)
  )

  "getAuthor" - {
    "should retrieve single author" in {
      val id      = UUID.randomUUID()
      val dao     = mock[AuthorDao]
      val request = GetAuthorRequest(id)
      val srv     = new AuthorServiceImpl(dao, ThreadPool)

      (dao.getAuthor _).expects(id).returns(Future.successful(Some(author1)))

      srv.getAuthor(request).futureValue shouldBe GetAuthorResponse.Found(author1)
    }

    "should respond with NotFound with unknown author" in {
      val id      = UUID.randomUUID()
      val dao     = mock[AuthorDao]
      val request = GetAuthorRequest(id)
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      (dao.getAuthor _).expects(id).returns(Future.successful(None))

      srv.getAuthor(request).futureValue shouldBe GetAuthorResponse.NotFound
    }
  }

  "createAuthor" - {
    "should create new author" in {
      val dao = mock[AuthorDao]
      val srv = new AuthorServiceImpl(dao, ThreadPool)
      val req = CreateAuthorRequest(author1)

      (dao.createAuthor _).expects(author1).returns(Future.successful(Some(author1)))
      srv.createAuthor(req).futureValue shouldBe CreateAuthorResponse.Created(author1)
    }
  }

  "listAuthors" - {
    "when db is empty returns empty list" - {
      val dao = mock[AuthorDao]
      val srv = new AuthorServiceImpl(dao, ThreadPool)

      (dao.listAuthors _).expects().returns(Future.successful(Seq[Author]()))

      srv.listAuthors.futureValue shouldBe ListAuthorsResponse(Seq[Author]())
    }

    "when db is not empty returns list of authors" - {
      val dao = mock[AuthorDao]
      val srv = new AuthorServiceImpl(dao, ThreadPool)

      (dao.listAuthors _).expects().returns(Future.successful(Seq[Author](author1)))

      srv.listAuthors.futureValue shouldBe ListAuthorsResponse(Seq[Author](author1))
    }
  }

  "updateAuthor" - {
    "when author is known" - {
      val dao     = mock[AuthorDao]
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      val request = UpdateAuthorRequest(author1)

      (dao.updateAuthor _).expects(author1).returns(Future.successful(Some(author1)))
      srv.updateAuthor(request).futureValue shouldBe UpdateAuthorResponse.Updated(author1)
    }
    "when author is unknown" - {
      val dao     = mock[AuthorDao]
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      val request = UpdateAuthorRequest(author1)

      (dao.updateAuthor _).expects(author1).returns(Future.successful(None))
      srv.updateAuthor(request).futureValue shouldBe UpdateAuthorResponse.Invalid
    }
    "when id is not provided" - {
      val dao             = mock[AuthorDao]
      val srv             = new AuthorServiceImpl(dao, ThreadPool)
      val authorWithoutID = author1.copy(id = None)
      val request         = UpdateAuthorRequest(authorWithoutID)

      srv.updateAuthor(request).futureValue shouldBe UpdateAuthorResponse.CannotUpdateWithoutID
    }
  }

  "deleteAuthor" - {
    "when id is provided but author is unknown" - {
      val dao     = mock[AuthorDao]
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      val request = DeleteAuthorRequest(None)

      srv.deleteAuthor(request).futureValue shouldBe DeleteAuthorResponse.CannotDeleteWithoutID
    }
    "when author is unknown" - {
      val id      = UUID.randomUUID()
      val dao     = mock[AuthorDao]
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      val request = DeleteAuthorRequest(Some(id))

      (dao.deleteAuthor _).expects(id).returns(Future.successful(None))

      srv.deleteAuthor(request).futureValue shouldBe DeleteAuthorResponse.NotFound
    }
    "when author is known" - {
      val id      = UUID.randomUUID()
      val dao     = mock[AuthorDao]
      val srv     = new AuthorServiceImpl(dao, ThreadPool)
      val request = DeleteAuthorRequest(Some(id))

      (dao.deleteAuthor _).expects(id).returns(Future.successful(Some(author1)))
      srv.deleteAuthor(request).futureValue shouldBe DeleteAuthorResponse.Deleted(author1)
    }
  }
}
