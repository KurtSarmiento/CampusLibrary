using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebMvc.Controllers;

public class LoansController : Controller
{
    private readonly LibraryDbContext _context;

    public LoansController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: Loans
    public async Task<IActionResult> Index()
    {
        var loans = await _context.Loans
            .Include(l => l.Book)
            .Include(l => l.Member)
            .Where(l => l.ReturnedAt == null)
            .ToListAsync();

        return View(loans);
    }

    // POST: Loans/Return/5
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Return(int id)
    {
        var loan = await _context.Loans.FindAsync(id);

        if (loan == null)
        {
            return NotFound();
        }

        if (loan.ReturnedAt != null)
        {
            return RedirectToAction(nameof(Index));
        }

        loan.ReturnedAt = DateTime.UtcNow;
        await _context.SaveChangesAsync();

        return RedirectToAction(nameof(Index));
    }
}
