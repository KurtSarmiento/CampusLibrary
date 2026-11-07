using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebMvc.Services;

public interface IBookService
{
    Task<IReadOnlyList<Book>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<Book?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    Task<bool> CreateAsync(Book book, CancellationToken cancellationToken = default);
    Task<BookUpdateResult> UpdateAsync(Book book, CancellationToken cancellationToken = default);
    Task<BookDeleteResult> DeleteAsync(int id, CancellationToken cancellationToken = default);
}

public enum BookUpdateResult
{
    Success,
    NotFound,
    DuplicateIsbn,
    ConcurrencyConflict
}

public enum BookDeleteResult
{
    Success,
    NotFound,
    HasOpenLoan
}
