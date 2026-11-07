using CampusLibrary.WebApi.DTOs;

namespace CampusLibrary.WebApi.Services;

public interface IBookService
{
    Task<IReadOnlyList<BookDto>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<BookDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    Task<BookCreateResult> CreateAsync(BookDto dto, CancellationToken cancellationToken = default);
    Task<BookUpdateResult> UpdateAsync(int id, BookDto dto, CancellationToken cancellationToken = default);
    Task<BookDeleteResult> DeleteAsync(int id, CancellationToken cancellationToken = default);
}

public enum BookCreateResult
{
    Success,
    DuplicateIsbn
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
    NotFound
}
