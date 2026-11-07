using CampusLibrary.Data.Entities;
using CampusLibrary.WebMvc.Repositories;

namespace CampusLibrary.WebMvc.Services;

public sealed class BookService : IBookService
{
    private readonly IBookRepository _repository;

    public BookService(IBookRepository repository)
    {
        _repository = repository;
    }

    public Task<IReadOnlyList<Book>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return _repository.GetAllAsync(cancellationToken);
    }

    public Task<Book?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        return _repository.GetByIdAsync(id, cancellationToken);
    }

    public async Task<bool> CreateAsync(Book book, CancellationToken cancellationToken = default)
    {
        if (await _repository.ExistsWithIsbnAsync(book.ISBN, cancellationToken: cancellationToken))
        {
            return false;
        }

        await _repository.AddAsync(book, cancellationToken);
        await _repository.SaveChangesAsync(cancellationToken);
        return true;
    }

    public async Task<BookUpdateResult> UpdateAsync(Book book, CancellationToken cancellationToken = default)
    {
        if (!await _repository.ExistsAsync(book.Id, cancellationToken))
        {
            return BookUpdateResult.NotFound;
        }

        if (await _repository.ExistsWithIsbnAsync(book.ISBN, book.Id, cancellationToken))
        {
            return BookUpdateResult.DuplicateIsbn;
        }

        _repository.Update(book);
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

        if (await _repository.HasOpenLoanAsync(id, cancellationToken))
        {
            return BookDeleteResult.HasOpenLoan;
        }

        _repository.Remove(book);
        await _repository.SaveChangesAsync(cancellationToken);
        return BookDeleteResult.Success;
    }
}
