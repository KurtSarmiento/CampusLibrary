using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Repositories;

public sealed class BookRepository : IBookRepository
{
    private readonly LibraryDbContext _context;

    public BookRepository(LibraryDbContext context)
    {
        _context = context;
    }

    public async Task<IReadOnlyList<Book>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return await _context.Books
            .AsNoTracking()
            .Include(book => book.Loans)
            .ThenInclude(loan => loan.Member)
            .ToListAsync(cancellationToken);
    }

    public Task<Book?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        return _context.Books.FindAsync(new object[] { id }, cancellationToken).AsTask();
    }

    public Task<bool> ExistsAsync(int id, CancellationToken cancellationToken = default)
    {
        return _context.Books.AnyAsync(book => book.Id == id, cancellationToken);
    }

    public Task<bool> ExistsWithIsbnAsync(string isbn, int? excludedId = null, CancellationToken cancellationToken = default)
    {
        return _context.Books.AnyAsync(
            book => book.ISBN == isbn && (!excludedId.HasValue || book.Id != excludedId.Value),
            cancellationToken);
    }

    public async Task AddAsync(Book book, CancellationToken cancellationToken = default)
    {
        await _context.Books.AddAsync(book, cancellationToken);
    }

    public void Update(Book book)
    {
        _context.Books.Update(book);
    }

    public void Remove(Book book)
    {
        _context.Books.Remove(book);
    }

    public async Task<bool> TrySaveChangesAsync(CancellationToken cancellationToken = default)
    {
        try
        {
            await _context.SaveChangesAsync(cancellationToken);
            return true;
        }
        catch (DbUpdateConcurrencyException)
        {
            return false;
        }
    }
}
