using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebMvc.Repositories;

public interface ILoanRepository
{
    Task<IReadOnlyList<Loan>> GetOpenAsync(CancellationToken cancellationToken = default);
    Task<Loan?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    void Update(Loan loan);
    Task SaveChangesAsync(CancellationToken cancellationToken = default);
}
