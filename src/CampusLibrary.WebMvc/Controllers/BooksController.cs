using CampusLibrary.Data.Entities;
using Microsoft.AspNetCore.Mvc;
using CampusLibrary.WebMvc.Services;

namespace CampusLibrary.WebMvc.Controllers;

public class BooksController : Controller
{
    private readonly IBookService _bookService;

    public BooksController(IBookService bookService)
    {
        _bookService = bookService;
    }

    // GET: Books
    public async Task<IActionResult> Index(CancellationToken cancellationToken)
    {
        var books = await _bookService.GetAllAsync(cancellationToken);
        return View(books);
    }

    // GET: Books/Create
    public IActionResult Create()
    {
        return View();
    }

    // POST: Books/Create
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Create(Book book, CancellationToken cancellationToken)
    {
        if (ModelState.IsValid)
        {
            if (!await _bookService.CreateAsync(book, cancellationToken))
            {
                ModelState.AddModelError("ISBN", "A book with this ISBN already exists");
                return View(book);
            }

            return RedirectToAction(nameof(Index));
        }
        return View(book);
    }

    // GET: Books/Edit/5
    public async Task<IActionResult> Edit(int? id, CancellationToken cancellationToken)
    {
        if (id == null)
        {
            return NotFound();
        }

        var book = await _bookService.GetByIdAsync(id.Value, cancellationToken);
        if (book == null)
        {
            return NotFound();
        }
        return View(book);
    }

    // POST: Books/Edit/5
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Edit(int id, Book book, CancellationToken cancellationToken)
    {
        if (id != book.Id)
        {
            return NotFound();
        }

        if (ModelState.IsValid)
        {
            var result = await _bookService.UpdateAsync(book, cancellationToken);
            switch (result)
            {
                case BookUpdateResult.NotFound:
                    return NotFound();
                case BookUpdateResult.DuplicateIsbn:
                    ModelState.AddModelError("ISBN", "A book with this ISBN already exists");
                    return View(book);
                case BookUpdateResult.ConcurrencyConflict:
                    ModelState.AddModelError(string.Empty, "This title was changed by another user. Reload and try again.");
                    return View(book);
            }

            return RedirectToAction(nameof(Index));
        }
        return View(book);
    }

    // GET: Books/Delete/5
    public async Task<IActionResult> Delete(int? id, CancellationToken cancellationToken)
    {
        if (id == null)
        {
            return NotFound();
        }

        var book = await _bookService.GetByIdAsync(id.Value, cancellationToken);
        if (book == null)
        {
            return NotFound();
        }

        return View(book);
    }

    // POST: Books/Delete/5
    [HttpPost, ActionName("Delete")]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteConfirmed(int id, CancellationToken cancellationToken)
    {
        var result = await _bookService.DeleteAsync(id, cancellationToken);
        if (result == BookDeleteResult.NotFound)
        {
            return NotFound();
        }

        return RedirectToAction(nameof(Index));
    }
}
