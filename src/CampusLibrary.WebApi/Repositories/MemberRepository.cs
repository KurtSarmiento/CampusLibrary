using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Repositories;

public sealed class MemberRepository : IMemberRepository
{
    private readonly LibraryDbContext _context;

    public MemberRepository(LibraryDbContext context)
    {
        _context = context;
    }

    public async Task<IReadOnlyList<Member>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return await _context.Members
            .AsNoTracking()
            .ToListAsync(cancellationToken);
    }

    public Task<Member?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        return _context.Members.FindAsync(new object[] { id }, cancellationToken).AsTask();
    }

    public Task<bool> ExistsWithEmailAsync(string email, CancellationToken cancellationToken = default)
    {
        return _context.Members.AnyAsync(member => member.Email == email, cancellationToken);
    }

    public async Task AddAsync(Member member, CancellationToken cancellationToken = default)
    {
        await _context.Members.AddAsync(member, cancellationToken);
    }

    public Task SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        return _context.SaveChangesAsync(cancellationToken);
    }
}
