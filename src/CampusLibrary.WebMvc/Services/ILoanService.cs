using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebMvc.Services;

public interface ILoanService
{
    Task<IReadOnlyList<Loan>> GetOpenAsync(CancellationToken cancellationToken = default);
    Task<LoanReturnResult> ReturnAsync(int id, CancellationToken cancellationToken = default);
}

public enum LoanReturnResult
{
    Success,
    NotFound
}
