using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Repositories;

namespace CampusLibrary.WebApi.Services;

public sealed class LoanService : ILoanService
{
    private readonly IBookRepository _bookRepository;
    private readonly IMemberRepository _memberRepository;
    private readonly ILoanRepository _loanRepository;

    public LoanService(
        IBookRepository bookRepository,
        IMemberRepository memberRepository,
        ILoanRepository loanRepository)
    {
        _bookRepository = bookRepository;
        _memberRepository = memberRepository;
        _loanRepository = loanRepository;
    }

    public async Task<BorrowResult> BorrowAsync(BorrowRequestDto request, CancellationToken cancellationToken = default)
    {
        var book = await _bookRepository.GetByIdAsync(request.BookId, cancellationToken);
        if (book is null)
        {
            return new BorrowResult(BorrowResultStatus.BookNotFound);
        }

        var member = await _memberRepository.GetByIdAsync(request.MemberId, cancellationToken);
        if (member is null)
        {
            return new BorrowResult(BorrowResultStatus.MemberNotFound);
        }

        if (await _loanRepository.HasOpenLoanAsync(request.BookId, cancellationToken))
        {
            return new BorrowResult(BorrowResultStatus.BookAlreadyOnLoan);
        }

        var loan = new Loan
        {
            BookId = book.Id,
            MemberId = member.Id,
            BorrowedAt = DateTime.UtcNow,
            Book = book,
            Member = member
        };

        await _loanRepository.AddAsync(loan, cancellationToken);
        if (!await _loanRepository.TrySaveChangesAsync(cancellationToken))
        {
            return new BorrowResult(BorrowResultStatus.Conflict);
        }

        return new BorrowResult(BorrowResultStatus.Success, DtoMapper.ToDto(loan));
    }

    public async Task<LoanReturnResult> ReturnAsync(int id, CancellationToken cancellationToken = default)
    {
        var loan = await _loanRepository.GetByIdAsync(id, cancellationToken);
        if (loan is null)
        {
            return LoanReturnResult.NotFound;
        }

        if (loan.ReturnedAt is not null)
        {
            return LoanReturnResult.AlreadyReturned;
        }

        loan.ReturnedAt = DateTime.UtcNow;
        _loanRepository.Update(loan);
        await _loanRepository.TrySaveChangesAsync(cancellationToken);
        return LoanReturnResult.Success;
    }

    public async Task<IReadOnlyList<LoanDto>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var loans = await _loanRepository.GetAllAsync(cancellationToken);
        return loans.Select(DtoMapper.ToDto).ToList();
    }

    public async Task<LoanDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        var loan = await _loanRepository.GetByIdAsync(id, cancellationToken);
        return loan is null ? null : DtoMapper.ToDto(loan);
    }
}
