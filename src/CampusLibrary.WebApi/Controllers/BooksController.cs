using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BooksController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public BooksController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: api/books
    [HttpGet]
    public async Task<ActionResult<IEnumerable<BookDto>>> GetBooks()
    {
        var books = await _context.Books
            .Select(b => new BookDto
            {
                Id = b.Id,
                ISBN = b.ISBN,
                Title = b.Title,
                Author = b.Author,
                PublishedYear = b.PublishedYear
            })
            .ToListAsync();

        return Ok(books);
    }

    // GET: api/books/5
    [HttpGet("{id}")]
    public async Task<ActionResult<BookDto>> GetBook(int id)
    {
        var book = await _context.Books
            .Where(b => b.Id == id)
            .Select(b => new BookDto
            {
                Id = b.Id,
                ISBN = b.ISBN,
                Title = b.Title,
                Author = b.Author,
                PublishedYear = b.PublishedYear
            })
            .FirstOrDefaultAsync();

        if (book == null)
        {
            return NotFound(new { Message = $"Book with ID {id} not found" });
        }

        return Ok(book);
    }

    // POST: api/books
    [HttpPost]
    public async Task<ActionResult<BookDto>> CreateBook(BookDto dto)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        // Check for duplicate ISBN
        var existingBook = await _context.Books
            .FirstOrDefaultAsync(b => b.ISBN == dto.ISBN);

        if (existingBook != null)
        {
            return Conflict(new { Message = $"A book with ISBN {dto.ISBN} already exists" });
        }

        var book = new Book
        {
            ISBN = dto.ISBN,
            Title = dto.Title,
            Author = dto.Author,
            PublishedYear = dto.PublishedYear
        };

        _context.Books.Add(book);
        await _context.SaveChangesAsync();

        dto.Id = book.Id;
        return CreatedAtAction(nameof(GetBook), new { id = book.Id }, dto);
    }

    // PUT: api/books/5
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateBook(int id, BookDto dto)
    {
        if (id != dto.Id)
        {
            return BadRequest(new { Message = "Book ID in URL does not match ID in request body" });
        }

        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var book = await _context.Books.FindAsync(id);

        if (book == null)
        {
            return NotFound(new { Message = $"Book with ID {id} not found" });
        }

        // Check for duplicate ISBN (ignore current book)
        var existingBook = await _context.Books
            .FirstOrDefaultAsync(b => b.ISBN == dto.ISBN && b.Id != id);

        if (existingBook != null)
        {
            return Conflict(new { Message = $"A book with ISBN {dto.ISBN} already exists" });
        }

        book.ISBN = dto.ISBN;
        book.Title = dto.Title;
        book.Author = dto.Author;
        book.PublishedYear = dto.PublishedYear;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!BookExists(id))
            {
                return NotFound(new { Message = $"Book with ID {id} not found" });
            }
            else
            {
                throw;
            }
        }

        return NoContent();
    }

    // DELETE: api/books/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteBook(int id)
    {
        var book = await _context.Books.FindAsync(id);

        if (book == null)
        {
            return NotFound(new { Message = $"Book with ID {id} not found" });
        }

        _context.Books.Remove(book);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool BookExists(int id)
    {
        return _context.Books.Any(e => e.Id == id);
    }
}
