package ru.otus.sc.filter.service.impl

import ru.otus.sc.author.service.AuthorService
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

import scala.concurrent.{ExecutionContextExecutor, Future}

class FilterServiceImpl(
    authorService: AuthorService,
    bookService: BookService,
    implicit val ThreadPool: ExecutionContextExecutor
) extends FilterService {
  override def filterAuthors(request: FilterAuthorsRequest): Future[FilterAuthorsResponse] =
    authorService.listAuthors.map { listAuthors =>
      val filteredAuthors = request.filters.foldLeft(listAuthors.authors) {
        case (acc, AuthorsFilter.ByGenre(genre))          => acc.filter(_.genres.contains(genre))
        case (acc, AuthorsFilter.ByName(name))            => acc.filter(_.name == name)
        case (acc, AuthorsFilter.ByPublicationYear(year)) => acc
      }

      FilterAuthorsResponse(filteredAuthors)
    }

  override def filterBooks(request: FilterBooksRequest): Future[FilterBooksResponse] =
    bookService.listBooks.map { listBooks =>
      val filteredBooks = request.filters.foldLeft(listBooks.books) {
        case (acc, BooksFilter.ByGenre(genre))         => acc.filter(_.genre == genre)
        case (acc, BooksFilter.ByName(name))           => acc.filter(_.name == name)
        case (acc, BooksFilter.MinPagesCount(cnt))     => acc.filter(_.pagesCount >= cnt)
        case (acc, BooksFilter.MaxPagesCount(cnt))     => acc.filter(_.pagesCount <= cnt)
        case (acc, BooksFilter.WithSignificantAuthors) => acc
      }

      FilterBooksResponse(filteredBooks)
    }
}
