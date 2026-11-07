using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Repositories;

public sealed class LoanRepository : ILoanRepository
{
    private readonly LibraryDbContext _context;

    public LoanRepository(LibraryDbContext context)
    {
        _context = context;
    }

    public async Task<IReadOnlyList<Loan>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return await _context.Loans
            .AsNoTracking()
            .Include(loan => loan.Book)
            .Include(loan => loan.Member)
            .ToListAsync(cancellationToken);
    }

    public Task<Loan?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        return _context.Loans
            .Include(loan => loan.Book)
            .Include(loan => loan.Member)
            .FirstOrDefaultAsync(loan => loan.Id == id, cancellationToken);
    }

    public Task<bool> HasOpenLoanAsync(int bookId, CancellationToken cancellationToken = default)
    {
        return _context.Loans.AnyAsync(
            loan => loan.BookId == bookId && loan.ReturnedAt == null,
            cancellationToken);
    }

    public async Task AddAsync(Loan loan, CancellationToken cancellationToken = default)
    {
        await _context.Loans.AddAsync(loan, cancellationToken);
    }

    public void Update(Loan loan)
    {
        _context.Loans.Update(loan);
    }

    public async Task<bool> TrySaveChangesAsync(CancellationToken cancellationToken = default)
    {
        try
        {
            await _context.SaveChangesAsync(cancellationToken);
            return true;
        }
        catch (DbUpdateException)
        {
            return false;
        }
    }
}
