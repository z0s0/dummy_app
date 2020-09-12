package ru.otus.sc.filters.service.impl

import ru.otus.sc.author.service.AuthorService
import ru.otus.sc.book.service.BookService
import ru.otus.sc.filters.service.FilterService
import ru.otus.sc.filters.model.{
  AuthorsFilter,
  BooksFilter,
  FilterAuthorsRequest,
  FilterBooksRequest
}
import ru.otus.sc.author.model.{Author, Genre}
import ru.otus.sc.book.model.Book

object FilterServiceImpl {
  val PublicationSizeToBeSignificant = 10
}

class FilterServiceImpl(authorService: AuthorService, bookService: BookService)
    extends FilterService {

  def booksBy(request: FilterBooksRequest): Seq[Book] = {
    val filters = request.filter.getOrElse(Seq.empty)

    val filteredBooks =
      filters.foldLeft(bookService.listBooks()) { (booksList: Seq[Book], filter: BooksFilter) =>
        {
          filter match {
            case BooksFilter.ByName(name)            => booksList.filter(_.name == name)
            case BooksFilter.ByGenre(genre)          => booksList.filter(_.genre == genre)
            case BooksFilter.MinPagesCount(count)    => booksList.filter(_.pagesCount >= count)
            case BooksFilter.MaxPagesCount(maxCount) => booksList.filter(_.pagesCount <= maxCount)
            case BooksFilter.WithSignificantAuthors  => filterBooksWithSignificantAuthors(booksList)
          }
        }
      }

    maybeApplyLimit(filteredBooks, request.limit)
  }

  def authorsBy(request: FilterAuthorsRequest): Seq[Author] = {
    val filters = request.filter.getOrElse(Seq.empty)

    val filteredAuthors =
      filters.foldLeft(
        authorService.list()
      )((listAuthors: Seq[Author], filter: AuthorsFilter) => {
        filter match {
          case AuthorsFilter.ByName(name)            => authorsByName(name, listAuthors)
          case AuthorsFilter.ByGenre(genre)          => authorsByGenre(genre, listAuthors)
          case AuthorsFilter.ByPublicationYear(year) => authorsByPublicationYear(year, listAuthors)
        }
      })

    maybeApplyLimit(filteredAuthors, request.limit)
  }

  private def authorsByName(name: String, authors: Seq[Author]): Seq[Author] =
    authors.filter(_.name == name)

  private def authorsByGenre(genre: Genre, authors: Seq[Author]): Seq[Author] =
    authors.filter(_.genres.contains(genre))

  private def authorsByPublicationYear(year: Int, authors: Seq[Author]): Seq[Author] = {
    val authorNames = bookService
      .listBooks()
      .filter(_.publishedYear == year)
      .map(_.authorName)

    authors.filter(author => authorNames.contains(author.name))
  }

  private def maybeApplyLimit[T](listEntities: Seq[T], limit: Option[Int]): Seq[T] = {
    limit match {
      case Some(value) =>
        listEntities.take(value)

      case None =>
        listEntities
    }
  }

  private def filterBooksWithSignificantAuthors(books: Seq[Book]): Seq[Book] = {
    val significantAuthorsNames =
      bookService
        .listBooks()
        .foldLeft(Set[String]()) { (acc: Set[String], book: Book) =>
          {
            if (book.pagesCount >= FilterService.PublicationSizeToBeSignificant)
              acc + book.authorName
            else
              acc
          }
        }

    books.filter(book => significantAuthorsNames.contains(book.authorName))
  }
}
