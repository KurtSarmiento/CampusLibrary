using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BooksController : ControllerBase
{
    private readonly IBookService _bookService;

    public BooksController(IBookService bookService)
    {
        _bookService = bookService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<BookDto>>> GetBooks(CancellationToken cancellationToken)
    {
        return Ok(await _bookService.GetAllAsync(cancellationToken));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<BookDto>> GetBook(int id, CancellationToken cancellationToken)
    {
        var book = await _bookService.GetByIdAsync(id, cancellationToken);
        return book is null
            ? NotFound(new { Message = $"Book with ID {id} not found" })
            : Ok(book);
    }

    [HttpPost]
    public async Task<ActionResult<BookDto>> CreateBook(BookDto dto, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var result = await _bookService.CreateAsync(dto, cancellationToken);
        if (result == BookCreateResult.DuplicateIsbn)
        {
            return Conflict(new { Message = $"A book with ISBN {dto.ISBN} already exists" });
        }

        return CreatedAtAction(nameof(GetBook), new { id = dto.Id }, dto);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateBook(int id, BookDto dto, CancellationToken cancellationToken)
    {
        if (id != dto.Id)
        {
            return BadRequest(new { Message = "Book ID in URL does not match ID in request body" });
        }

        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var result = await _bookService.UpdateAsync(id, dto, cancellationToken);
        return result switch
        {
            BookUpdateResult.NotFound => NotFound(new { Message = $"Book with ID {id} not found" }),
            BookUpdateResult.DuplicateIsbn => Conflict(new { Message = $"A book with ISBN {dto.ISBN} already exists" }),
            BookUpdateResult.ConcurrencyConflict => Conflict(new { Message = "The book was modified by another request" }),
            _ => NoContent()
        };
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteBook(int id, CancellationToken cancellationToken)
    {
        var result = await _bookService.DeleteAsync(id, cancellationToken);
        return result == BookDeleteResult.NotFound
            ? NotFound(new { Message = $"Book with ID {id} not found" })
            : NoContent();
    }
}