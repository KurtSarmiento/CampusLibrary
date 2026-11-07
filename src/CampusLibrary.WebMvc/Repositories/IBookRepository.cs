using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebMvc.Repositories;

public interface IBookRepository
{
    Task<IReadOnlyList<Book>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<Book?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    Task<bool> ExistsAsync(int id, CancellationToken cancellationToken = default);
    Task<bool> ExistsWithIsbnAsync(string isbn, int? excludedId = null, CancellationToken cancellationToken = default);
    Task<bool> HasOpenLoanAsync(int bookId, CancellationToken cancellationToken = default);
    Task AddAsync(Book book, CancellationToken cancellationToken = default);
    void Update(Book book);
    void Remove(Book book);
    Task SaveChangesAsync(CancellationToken cancellationToken = default);
    Task<bool> TrySaveChangesAsync(CancellationToken cancellationToken = default);
}
