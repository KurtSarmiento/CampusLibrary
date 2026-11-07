using CampusLibrary.WebApi.DTOs;

namespace CampusLibrary.WebApi.Services;

public interface ILoanService
{
    Task<BorrowResult> BorrowAsync(BorrowRequestDto request, CancellationToken cancellationToken = default);
    Task<LoanReturnResult> ReturnAsync(int id, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<LoanDto>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<LoanDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
}

public enum BorrowResultStatus
{
    Success,
    BookNotFound,
    MemberNotFound,
    BookAlreadyOnLoan,
    Conflict
}

public sealed record BorrowResult(BorrowResultStatus Status, LoanDto? Loan = null);

public enum LoanReturnResult
{
    Success,
    NotFound,
    AlreadyReturned
}
