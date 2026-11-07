using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LoansController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public LoansController(LibraryDbContext context)
    {
        _context = context;
    }

    // POST: api/loans (borrow)
    [HttpPost]
    public async Task<ActionResult<LoanDto>> BorrowBook(BorrowRequestDto request)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        // Check if book exists
        var book = await _context.Books.FindAsync(request.BookId);
        if (book == null)
        {
            return NotFound(new { Message = $"Book with ID {request.BookId} not found" });
        }

        // Check if member exists
        var member = await _context.Members.FindAsync(request.MemberId);
        if (member == null)
        {
            return NotFound(new { Message = $"Member with ID {request.MemberId} not found" });
        }

        // Check if book is already on loan
        var existingLoan = await _context.Loans
            .FirstOrDefaultAsync(l => l.BookId == request.BookId && l.ReturnedAt == null);

        if (existingLoan != null)
        {
            return Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" });
        }

        // Create new loan
        var loan = new Loan
        {
            BookId = request.BookId,
            MemberId = request.MemberId,
            BorrowedAt = DateTime.UtcNow,
            ReturnedAt = null
        };

        _context.Loans.Add(loan);
        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            // Unique open-loan index violated by a concurrent borrow.
            return Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" });
        }

        // Return the created loan with related data
        var loanDto = new LoanDto
        {
            Id = loan.Id,
            BookId = loan.BookId,
            MemberId = loan.MemberId,
            BorrowedAt = loan.BorrowedAt,
            ReturnedAt = loan.ReturnedAt,
            Book = new BookDto
            {
                Id = book.Id,
                ISBN = book.ISBN,
                Title = book.Title,
                Author = book.Author,
                PublishedYear = book.PublishedYear
            },
            Member = new MemberDto
            {
                Id = member.Id,
                Name = member.Name,
                Email = member.Email,
                JoinedDate = member.JoinedDate
            }
        };

        return CreatedAtAction(nameof(GetLoan), new { id = loan.Id }, loanDto);
    }

    // POST: api/loans/{id}/return (return)
    [HttpPost("{id}/return")]
    public async Task<IActionResult> ReturnBook(int id)
    {
        var loan = await _context.Loans.FindAsync(id);

        if (loan == null)
        {
            return NotFound(new { Message = $"Loan with ID {id} not found" });
        }

        if (loan.ReturnedAt != null)
        {
            return Conflict(new { Message = $"Loan with ID {id} has already been returned" });
        }

        loan.ReturnedAt = DateTime.UtcNow;
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // GET: api/loans
    [HttpGet]
    public async Task<ActionResult<IEnumerable<LoanDto>>> GetLoans()
    {
        var loans = await _context.Loans
            .Include(l => l.Book)
            .Include(l => l.Member)
            .Select(l => new LoanDto
            {
                Id = l.Id,
                BookId = l.BookId,
                MemberId = l.MemberId,
                BorrowedAt = l.BorrowedAt,
                ReturnedAt = l.ReturnedAt,
                Book = new BookDto
                {
                    Id = l.Book.Id,
                    ISBN = l.Book.ISBN,
                    Title = l.Book.Title,
                    Author = l.Book.Author,
                    PublishedYear = l.Book.PublishedYear
                },
                Member = new MemberDto
                {
                    Id = l.Member.Id,
                    Name = l.Member.Name,
                    Email = l.Member.Email,
                    JoinedDate = l.Member.JoinedDate
                }
            })
            .ToListAsync();

        return Ok(loans);
    }

    // GET: api/loans/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<LoanDto>> GetLoan(int id)
    {
        var loan = await _context.Loans
            .Include(l => l.Book)
            .Include(l => l.Member)
            .Where(l => l.Id == id)
            .Select(l => new LoanDto
            {
                Id = l.Id,
                BookId = l.BookId,
                MemberId = l.MemberId,
                BorrowedAt = l.BorrowedAt,
                ReturnedAt = l.ReturnedAt,
                Book = new BookDto
                {
                    Id = l.Book.Id,
                    ISBN = l.Book.ISBN,
                    Title = l.Book.Title,
                    Author = l.Book.Author,
                    PublishedYear = l.Book.PublishedYear
                },
                Member = new MemberDto
                {
                    Id = l.Member.Id,
                    Name = l.Member.Name,
                    Email = l.Member.Email,
                    JoinedDate = l.Member.JoinedDate
                }
            })
            .FirstOrDefaultAsync();

        if (loan == null)
        {
            return NotFound(new { Message = $"Loan with ID {id} not found" });
        }

        return Ok(loan);
    }
}
