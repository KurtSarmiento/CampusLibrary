using CampusLibrary.Data.Entities;
using CampusLibrary.WebMvc.Repositories;

namespace CampusLibrary.WebMvc.Services;

public sealed class LoanService : ILoanService
{
    private readonly ILoanRepository _repository;

    public LoanService(ILoanRepository repository)
    {
        _repository = repository;
    }

    public Task<IReadOnlyList<Loan>> GetOpenAsync(CancellationToken cancellationToken = default)
    {
        return _repository.GetOpenAsync(cancellationToken);
    }

    public async Task<LoanReturnResult> ReturnAsync(int id, CancellationToken cancellationToken = default)
    {
        var loan = await _repository.GetByIdAsync(id, cancellationToken);
        if (loan is null)
        {
            return LoanReturnResult.NotFound;
        }

        if (loan.ReturnedAt is null)
        {
            loan.ReturnedAt = DateTime.UtcNow;
            _repository.Update(loan);
            await _repository.SaveChangesAsync(cancellationToken);
        }

        return LoanReturnResult.Success;
    }
}
