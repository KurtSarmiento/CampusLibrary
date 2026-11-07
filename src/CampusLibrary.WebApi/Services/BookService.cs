using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Repositories;

namespace CampusLibrary.WebApi.Services;

public sealed class BookService : IBookService
{
    private readonly IBookRepository _repository;

    public BookService(IBookRepository repository)
    {
        _repository = repository;
    }

    public async Task<IReadOnlyList<BookDto>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var books = await _repository.GetAllAsync(cancellationToken);
        return books.Select(DtoMapper.ToDto).ToList();
    }

    public async Task<BookDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        var book = await _repository.GetByIdAsync(id, cancellationToken);
        return book is null ? null : DtoMapper.ToDto(book);
    }

    public async Task<BookCreateResult> CreateAsync(BookDto dto, CancellationToken cancellationToken = default)
    {
        if (await _repository.ExistsWithIsbnAsync(dto.ISBN, cancellationToken: cancellationToken))
        {
            return BookCreateResult.DuplicateIsbn;
        }

        var book = new Book
        {
            ISBN = dto.ISBN,
            Title = dto.Title,
            Author = dto.Author ?? string.Empty,
            PublishedYear = dto.PublishedYear
        };

        await _repository.AddAsync(book, cancellationToken);
        if (!await _repository.TrySaveChangesAsync(cancellationToken))
        {
            return BookCreateResult.DuplicateIsbn;
        }

        dto.Id = book.Id;
        return BookCreateResult.Success;
    }

    public async Task<BookUpdateResult> UpdateAsync(int id, BookDto dto, CancellationToken cancellationToken = default)
    {
        var book = await _repository.GetByIdAsync(id, cancellationToken);
        if (book is null)
        {
            return BookUpdateResult.NotFound;
        }

        if (await _repository.ExistsWithIsbnAsync(dto.ISBN, id, cancellationToken))
        {
            return BookUpdateResult.DuplicateIsbn;
        }

        book.ISBN = dto.ISBN;
        book.Title = dto.Title;
        book.Author = dto.Author ?? string.Empty;
        book.PublishedYear = dto.PublishedYear;

        return await _repository.TrySaveChangesAsync(cancellationToken)
            ? BookUpdateResult.Success
            : BookUpdateResult.ConcurrencyConflict;
    }

    public async Task<BookDeleteResult> DeleteAsync(int id, CancellationToken cancellationToken = default)
    {
        var book = await _repository.GetByIdAsync(id, cancellationToken);
        if (book is null)
        {
            return BookDeleteResult.NotFound;
        }

        _repository.Remove(book);
        await _repository.TrySaveChangesAsync(cancellationToken);
        return BookDeleteResult.Success;
    }
}
