using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebMvc.Controllers;

public class MembersController : Controller
{
    private readonly LibraryDbContext _context;

    public MembersController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: Members
    public async Task<IActionResult> Index()
    {
        var members = await _context.Members.ToListAsync();
        return View(members);
    }

    // GET: Members/Create
    public IActionResult Create()
    {
        return View();
    }

    // POST: Members/Create
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Create(Member member)
    {
        if (ModelState.IsValid)
        {
            // Check for duplicate email
            var existingMember = await _context.Members
                .FirstOrDefaultAsync(m => m.Email == member.Email);

            if (existingMember != null)
            {
                ModelState.AddModelError("Email", "A member with this email already exists");
                return View(member);
            }

            // Set JoinedDate if not provided
            if (member.JoinedDate == default)
            {
                member.JoinedDate = DateTime.UtcNow;
            }

            _context.Members.Add(member);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }
        return View(member);
    }
}
