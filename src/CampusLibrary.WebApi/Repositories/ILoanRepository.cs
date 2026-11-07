using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebApi.Repositories;

public interface ILoanRepository
{
    Task<IReadOnlyList<Loan>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<Loan?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    Task<bool> HasOpenLoanAsync(int bookId, CancellationToken cancellationToken = default);
    Task AddAsync(Loan loan, CancellationToken cancellationToken = default);
    void Update(Loan loan);
    Task<bool> TrySaveChangesAsync(CancellationToken cancellationToken = default);
}
