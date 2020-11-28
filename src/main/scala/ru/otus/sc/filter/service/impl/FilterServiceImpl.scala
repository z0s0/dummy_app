package ru.otus.sc.filter.service.impl

import ru.otus.sc.author.model.Author
import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.book.model.Book
import ru.otus.sc.book.service.BookService
import ru.otus.sc.filter.model.{
  AuthorsFilter,
  BooksFilter,
  FilterAuthorsRequest,
  FilterAuthorsResponse,
  FilterBooksRequest,
  FilterBooksResponse
}
import ru.otus.sc.filter.service.FilterService

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class FilterServiceImpl(
    authorService: AuthorService,
    bookService: BookService
)(implicit val ThreadPool: ExecutionContext)
    extends FilterService {
  override def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse] = {
    authorService.listAuthors.map { listAuthorsResponse =>
      val authors = listAuthorsResponse.authors

      val filteredAuthors = request.filters match {
        case Some(filters) =>
          filters.foldLeft(authors) {
            case (acc, AuthorsFilter.ByGenre(genre))          => acc.filter(_.genres.contains(genre))
            case (acc, AuthorsFilter.ByName(name))            => acc.filter(_.name == name)
            case (acc, AuthorsFilter.ByPublicationYear(year)) => filterByPublicationYear(acc, year)
          }

        case None => authors
      }

      FilterAuthorsResponse(maybeApplyLimit(filteredAuthors, request.limit))
    }

  }

  override def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse] = {

    bookService.listBooks.map { listBooksResponse =>
      val books = listBooksResponse.books

      val filteredBooks =
        request.filters match {
          case Some(filters) =>
            filters.foldLeft(books) {
              case (acc, BooksFilter.ByGenre(genre))     => acc.filter(_.genre == genre)
              case (acc, BooksFilter.ByName(name))       => acc.filter(_.name == name)
              case (acc, BooksFilter.MinPagesCount(cnt)) => acc.filter(_.pagesCount >= cnt)
              case (acc, BooksFilter.MaxPagesCount(cnt)) => acc.filter(_.pagesCount <= cnt)
              case (acc, BooksFilter.WithSignificantAuthors) =>
                filterBooksWithSignificantAuthors(acc)
            }
          case None => books
        }

      FilterBooksResponse(maybeApplyLimit(filteredBooks, request.limit))
    }
  }

  private def maybeApplyLimit[T](collection: Seq[T], limit: Option[Int]): Seq[T] = {
    limit match {
      case Some(lim) => collection.take(lim)
      case None      => collection
    }
  }

  private def filterByPublicationYear(authors: Seq[Author], year: Int): Seq[Author] = {
    val listBooks =
      Await.result(bookService.listBooks, 1.second).books.filter(_.publishedYear == year)
    val authorNames = listBooks.map(_.authorName).toSet

    authors.filter(author => authorNames.contains(author.name))
  }

  private def filterBooksWithSignificantAuthors(books: Seq[Book]): Seq[Book] = {
    val listBooks = Await.result(bookService.listBooks, 1.second).books
    val significantAuthorsNames =
      listBooks
        .foldLeft(Set[String]()) { (acc, book) =>
          if (book.pagesCount >= FilterService.MinPagesCountToBeSignificant)
            acc + book.authorName
          else
            acc
        }

    books.filter(book => significantAuthorsNames.contains(book.authorName))
  }
}
