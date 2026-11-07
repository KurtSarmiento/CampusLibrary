using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebMvc.Repositories;

public sealed class LoanRepository : ILoanRepository
{
    private readonly LibraryDbContext _context;

    public LoanRepository(LibraryDbContext context)
    {
        _context = context;
    }

    public async Task<IReadOnlyList<Loan>> GetOpenAsync(CancellationToken cancellationToken = default)
    {
        return await _context.Loans
            .AsNoTracking()
            .Include(loan => loan.Book)
            .Include(loan => loan.Member)
            .Where(loan => loan.ReturnedAt == null)
            .ToListAsync(cancellationToken);
    }

    public async Task<Loan?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        return await _context.Loans.FindAsync(new object[] { id }, cancellationToken);
    }

    public void Update(Loan loan)
    {
        _context.Loans.Update(loan);
    }

    public Task SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        return _context.SaveChangesAsync(cancellationToken);
    }
}
